package cl.exercise.users.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDTO {

    @JsonProperty(value = "number")
    @NotBlank(message = "El número es obligatorio")
    @Pattern(regexp = "\\d{7,10}", message = "mensaje: El número debe tener entre 7 y 10 dígitos")
    private String phoneNumber;
    @JsonProperty(value = "citycode")
    @NotBlank(message = "El código de ciudad es obligatorio")
    private String codCity;
    @JsonProperty(value = "countrycode")
    @NotBlank(message = "El código de país es obligatorio")
    private String codCountry;

}
