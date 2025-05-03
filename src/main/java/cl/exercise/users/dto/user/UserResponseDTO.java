package cl.exercise.users.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private UUID id;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "phones")
    private List<PhoneDTO> phoneList;
    @JsonProperty(value = "created")
    private String created;
    @JsonProperty(value = "modified")
    private String modified;
    @JsonProperty(value = "last_login")
    private String lastLogin;
    private String token;
    @JsonProperty(value = "isactive")
    private boolean isActive;

}
