package cl.exercise.users.service;

import cl.exercise.users.dto.log.UserLogDTO;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserLogService {

    Page<UserLogDTO> getAllLog(Integer page, Integer size, UUID userId);

}
