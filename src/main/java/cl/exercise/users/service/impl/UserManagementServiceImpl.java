package cl.exercise.users.service.impl;

import cl.exercise.users.dto.ServiceResponseDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.dto.user.UserResponseDTO;
import cl.exercise.users.exception.EmailExistException;
import cl.exercise.users.exception.ValidationException;
import cl.exercise.users.mapper.MapperHelper;
import cl.exercise.users.model.UserLogModel;
import cl.exercise.users.model.UserModel;
import cl.exercise.users.repository.UserLogRepository;
import cl.exercise.users.repository.UserManagementRepository;
import cl.exercise.users.util.JwtUtil;
import cl.exercise.users.service.UserManagementService;
import cl.exercise.users.util.Constants;
import cl.exercise.users.util.Utils;
import cl.exercise.users.validator.ValidationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserManagementServiceImpl implements UserManagementService {

    private final ValidationHandler validationHandler;
    private final UserManagementRepository userManagementRepository;
    private final UserLogRepository userLogRepository;
    private final MapperHelper mapperHelper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public UserManagementServiceImpl(UserManagementRepository userManagementRepository,
                                     UserLogRepository userLogRepository,
                                     MapperHelper mapperHelper,
                                     ValidationHandler validationHandler,
                                     JwtUtil jwtUtil,
                                     PasswordEncoder passwordEncoder) {
        this.userManagementRepository = userManagementRepository;
        this.userLogRepository = userLogRepository;
        this.mapperHelper = mapperHelper;
        this.validationHandler = validationHandler;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ServiceResponseDTO addUser(UserRequestDTO userDto) {
        log.info("UserManagementServiceImpl:::addUser for eMail: {}", userDto.getEmail());
        validationHandler.validateUserRequest(userDto);
        validationHandler.validatePhoneList(userDto.getPhoneList());
        if (userManagementRepository.existsByEmail(userDto.getEmail())) {
            log.error("UserManagementServiceImpl:::addUser: eMail {} exist", userDto.getEmail());
            throw new EmailExistException(Constants.EMAIL_REGISTERED);
        }
        UserModel model = mapperHelper.toEntity(userDto);
        model.setCreationDate(LocalDateTime.now());
        model.setIsActive(true);
        userManagementRepository.save(model);
        model.setToken(this.generateToken(model));
        userManagementRepository.save(model);

        updateLog(Constants.CREATED, model);

        return mapperHelper.mapToGenericResponse(model);
    }

    @Override
    public UserResponseDTO getByEmail(String email) {
        log.info("UserManagementServiceImpl:::getByEmail for eMail: {}", email);
        validationHandler.validateEmail(email);
        if (!userManagementRepository.existsByEmail(email)) {
            log.error("UserManagementServiceImpl:::getByEmail: eMail {} no exist", email);
            throw new EmailExistException(Constants.USER_NOT_FOUND_W_EMAIL + email);
        }
        UserModel user = userManagementRepository.findByEmailIgnoreCase(email);
        return mapperHelper.mapToUserResponse(user);
    }

    @Override
    public Page<UserResponseDTO> getAllUser(Integer page, Integer size, String sortBy, String status) {
        log.info("UserManagementServiceImpl:::getAllUser");
        int currentPage = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (size == null || size < 1) ? 10 : size;
        String sortField = (sortBy == null || sortBy.isBlank()) ? "creationDate" : sortBy;
        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(sortField));

        Page<UserModel> usuariosPage = userManagementRepository.findAll(pageable);

        List<UserResponseDTO> userList = usuariosPage.getContent().stream()
                .filter(UserModel -> {
                    if(Constants.ACTIVE.equalsIgnoreCase(status)) return UserModel.getIsActive();
                    if(Constants.INACTIVE.equalsIgnoreCase(status)) return !UserModel.getIsActive();
                    return true;
                })
                .map(mapperHelper::mapToUserResponse)
                .toList();
        return new PageImpl<>(userList, pageable, usuariosPage.getTotalElements());
    }

    @Override
    @Transactional
    public UserResponseDTO updateUser(UUID id, UserRequestDTO userDto) {
        log.info("UserManagementServiceImpl:::updateUser for UUID: {}", id);

        UserModel model = userManagementRepository.findById(id)
                .orElseThrow(() -> new ValidationException(Constants.USER_NOT_FOUND_W_ID + id));

        validationHandler.validateUserIsActive(model);

        Utils.updateBasicInfo(model, userDto, passwordEncoder, validationHandler);
        Utils.updatePhones(model, userDto.getPhoneList());

        model.setToken(generateToken(model));
        model.setUpdateDate(LocalDateTime.now());
        model.setLastLogin(LocalDateTime.now());

        UserModel updated = userManagementRepository.save(model);

        updateLog(Constants.UPDATED, model);

        return mapperHelper.mapToUserResponse(updated);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.info("UserManagementServiceImpl:::deleteUser for UUID: {}", id);
        UserModel model = userManagementRepository
                .findById(id)
                .orElseThrow(() -> new ValidationException(Constants.USER_NOT_FOUND));
        if(!model.getIsActive()){
            log.error("UserManagementServiceImpl:::deleteUser: User already deactivated");
            throw new ValidationException(Constants.ALREADY_DEACTIVATED);
        }
        model.setIsActive(false);
        model.setUpdateDate(LocalDateTime.now());
        userManagementRepository.save(model);

        updateLog(Constants.DEACTIVATED, model);
    }

    @Override
    @Transactional
    public void activateUser(UUID id) {
        log.info("UserManagementServiceImpl:::activateUser for UUID: {}", id);
        UserModel model = userManagementRepository.findById(id)
                .orElseThrow(() -> new ValidationException(Constants.USER_NOT_FOUND));
        if(model.getIsActive()){
            log.error("UserManagementServiceImpl:::activateUser: User already activated");
            throw new ValidationException(Constants.ALREADY_ACTIVATED);
        }
        model.setIsActive(true);
        model.setUpdateDate(LocalDateTime.now());
        model.setLastLogin(LocalDateTime.now());
        userManagementRepository.save(model);

        updateLog(Constants.REACTIVATED, model);
    }

    public String generateToken(UserModel request) {
        log.info("UserManagementServiceImpl:::generateToken");
        UserModel model = userManagementRepository.findById(request.getId())
                .orElseThrow(() -> new ValidationException(Constants.USER_NOT_FOUND));
        return jwtUtil.generateToken(model);
    }

    private void updateLog(String action, UserModel model) {
        UserLogModel log = new UserLogModel();
        log.setUserModel(model);
        log.setAction(action);
        log.setCreationDate(LocalDateTime.now());
        userLogRepository.save(log);
    }
}
