package cl.exercise.users.service.impl;

import cl.exercise.users.dto.log.UserLogDTO;
import cl.exercise.users.mapper.MapperHelper;
import cl.exercise.users.model.UserLogModel;
import cl.exercise.users.repository.UserLogRepository;
import cl.exercise.users.service.UserLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserLogServiceImpl implements UserLogService {

    private final UserLogRepository userLogRepository;
    private final MapperHelper mapperHelper;

    public UserLogServiceImpl(UserLogRepository userLogRepository,
                              MapperHelper mapperHelper) {
        this.userLogRepository = userLogRepository;
        this.mapperHelper = mapperHelper;
    }


    @Override
    public Page<UserLogDTO> getAllLog(Integer page, Integer size, UUID userId) {
        log.info("UserLogServiceImpl:::getAllLog");

        int currentPage = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (size == null || size < 1) ? 10 : size;
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<UserLogModel> logPage;

        if (userId != null) {
            log.info("UserLogServiceImpl:::getAllLog:::Obteniendo Logs para usuario: {}", userId);
            logPage = userLogRepository.findByUserModel_Id(userId, pageable);
        } else {
            log.info("UserLogServiceImpl:::getAllLog:::Obteniendo todo el Log");
            logPage = userLogRepository.findAll(pageable);
        }

        List<UserLogDTO> logDTOs = logPage.getContent().stream()
                .map(mapperHelper::mapLogToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(logDTOs, pageable, logPage.getTotalElements());
    }

}
