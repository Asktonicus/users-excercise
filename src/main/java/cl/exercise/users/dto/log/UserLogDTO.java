package cl.exercise.users.dto.log;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserLogDTO {
    private UUID id;
    private String idUser;
    private String action;
    private LocalDateTime creationDate;
}
