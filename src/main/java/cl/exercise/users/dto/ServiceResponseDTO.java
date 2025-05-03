package cl.exercise.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {

    @JsonProperty(value = "id")
    private UUID id;
    @JsonProperty(value = "created")
    private LocalDateTime created;
    @JsonProperty(value = "modified")
    private LocalDateTime modified;
    @JsonProperty(value = "last_login")
    private LocalDateTime lastLogin;
    @JsonProperty(value = "token")
    private String token;
    @JsonProperty(value = "isactive")
    private boolean isActive;

}
