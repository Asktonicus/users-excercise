package cl.exercise.users.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    @JsonProperty(value = "name")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @JsonProperty(value = "email")
    @NotBlank(message = "El email es obligatorio")
    private String email;
    @JsonProperty(value = "password")
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String passwd;
    @JsonProperty(value = "phones")
    @NotEmpty(message = "Debe incluir al menos un teléfono")
    private List<PhoneDTO> phoneList;

}
