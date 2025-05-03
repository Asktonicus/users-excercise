package cl.exercise.users.validator;

import cl.exercise.users.dto.user.PhoneDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.exception.ValidationException;
import cl.exercise.users.model.UserModel;
import cl.exercise.users.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class ValidationHandler {

    @Value("${app.regex.email}")
    private String emailRegex;

    @Value("${app.regex.pswd}")
    private String pswdRegex;

    public void validateUserRequest(UserRequestDTO body) {
        this.validateEmail(body.getEmail());
        this.validatePassword(body.getPasswd());
    }

    public void validatePhoneList(List<PhoneDTO> body) {
        if (body == null || body.isEmpty()) {
            throw new ValidationException("La lista de teléfonos no puede estar vacía");
        }

        for (int i = 0; i < body.size(); i++) {
            PhoneDTO phone = body.get(i);
            int position = i + 1;

            if (phone == null) {
                throw new ValidationException("El teléfono en la posición " + position + " es nulo");
            }
            if (isNullOrEmpty(phone.getPhoneNumber())) {
                throw new ValidationException("El número de teléfono en la posición " + position + " es requerido");
            }
            if (!phone.getPhoneNumber().matches("\\d{7,8}")) {
                throw new ValidationException("El número de teléfono en la posición " + position + " debe tener solo dígitos y entre 7 y 8 caracteres");
            }
            if (isNullOrEmpty(phone.getCodCity())) {
                throw new ValidationException("El código de ciudad en la posición " + position + " es requerido");
            }
            if (!phone.getCodCity().matches("\\d+")) {
                throw new ValidationException("El código de ciudad en la posición " + position + " debe contener solo números");
            }
            if (isNullOrEmpty(phone.getCodCountry())) {
                throw new ValidationException("El código de país en la posición " + position+ " es requerido");
            }
            if (!phone.getCodCountry().matches("\\d+")) {
                throw new ValidationException("El código de país en la posición " + position + " debe contener solo números");
            }
        }
    }

    public void validateEmail(String email) {
        if (!email.matches(emailRegex)) {
            throw new ValidationException(Constants.INVALID_EMAIL_FORMAT);
        }
    }

    public void validatePassword(String password) {
        if (!password.matches(pswdRegex)) {
            throw new ValidationException(Constants.INVALID_PASSWORD_FORMAT);
        }
    }

    public void validateUserIsActive(UserModel model) {
        if (!model.getIsActive()) {
            log.error("Inactive user can't be updated");
            throw new ValidationException(Constants.INACTIVE_CANT_UPDATE);
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
