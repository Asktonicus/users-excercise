package cl.exercise.users.mapper;

import cl.exercise.users.dto.ServiceResponseDTO;
import cl.exercise.users.dto.log.UserLogDTO;
import cl.exercise.users.dto.user.PhoneDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.dto.user.UserResponseDTO;
import cl.exercise.users.model.PhoneModel;
import cl.exercise.users.model.UserLogModel;
import cl.exercise.users.model.UserModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperHelper {

    private final PasswordEncoder passwordEncoder;

    public MapperHelper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public UserModel toEntity(UserRequestDTO dto) {
        UserModel user = new UserModel();

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswd(passwordEncoder.encode(dto.getPasswd()));
        user.setCreationDate(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setIsActive(true);
        List<PhoneModel> phoneList = dto.getPhoneList().stream()
                .map(this::mapPhone)
                .collect(Collectors.toList());
        phoneList.forEach(t -> t.setUser(user));
        user.setPhoneList(phoneList);

        return user;
    }

    private PhoneModel mapPhone(PhoneDTO t) {
        PhoneModel phone = new PhoneModel();
        phone.setPhoneNumber(t.getPhoneNumber());
        phone.setCodCity(t.getCodCity());
        phone.setCodCountry(t.getCodCountry());
        return phone;
    }

    public ServiceResponseDTO mapToGenericResponse(UserModel u) {
        ServiceResponseDTO dto = new ServiceResponseDTO();
        dto.setId(u.getId());
        dto.setCreated(u.getCreationDate());
        dto.setModified(u.getUpdateDate());
        dto.setLastLogin(u.getLastLogin());
        dto.setToken(u.getToken());
        dto.setActive(u.getIsActive());
        return dto;
    }

    public UserResponseDTO mapToUserResponse(UserModel u) {
        UserResponseDTO dto = new UserResponseDTO();

        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setPhoneList(
                u.getPhoneList().stream().map(t -> {
                    PhoneDTO phone = new PhoneDTO();
                    phone.setPhoneNumber(t.getPhoneNumber());
                    phone.setCodCity(t.getCodCity());
                    phone.setCodCountry(t.getCodCountry());
                    return phone;
                }).collect(Collectors.toList())
        );
        dto.setCreated(String.valueOf(u.getCreationDate()));
        dto.setModified(String.valueOf(u.getUpdateDate()));
        dto.setLastLogin(String.valueOf(u.getLastLogin()));
        dto.setToken(u.getToken());
        dto.setActive(u.getIsActive());
        return dto;
    }

    public UserLogDTO mapLogToDTO(UserLogModel model) {
        UserLogDTO dto = new UserLogDTO();
        dto.setId(model.getId());
        dto.setIdUser(String.valueOf(model.getUserModel().getId()));
        dto.setAction(model.getAction());
        dto.setCreationDate(model.getCreationDate());
        return dto;
    }

}
