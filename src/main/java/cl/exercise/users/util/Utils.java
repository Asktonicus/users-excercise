package cl.exercise.users.util;

import cl.exercise.users.dto.user.PhoneDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.model.PhoneModel;
import cl.exercise.users.model.UserModel;
import cl.exercise.users.validator.ValidationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
public class Utils {

    private Utils() {
        throw new UnsupportedOperationException("Utils class cannot be instantiated");
    }

    public static void updateBasicInfo(UserModel model,
                                       UserRequestDTO userDto,
                                       PasswordEncoder passwordEncoder,
                                       ValidationHandler validationHandler) {
        if (StringUtils.hasText(userDto.getName())) {
            model.setName(userDto.getName());
        }
        if (StringUtils.hasText(userDto.getEmail()) &&
                !userDto.getEmail().equalsIgnoreCase(model.getEmail())) {
            validationHandler.validateEmail(userDto.getEmail());
            model.setEmail(userDto.getEmail());
        }
        if (StringUtils.hasText(userDto.getPasswd())) {
            validationHandler.validatePassword(userDto.getPasswd());
            model.setPasswd(passwordEncoder.encode(userDto.getPasswd()));
        }
    }

    public static void updatePhones(UserModel model, List<PhoneDTO> phoneList) {
        if (phoneList == null || phoneList.isEmpty()) {
            return;
        }
        for (PhoneDTO phoneDto : phoneList) {
            Optional<PhoneModel> existingPhone = model.getPhoneList().stream()
                    .filter(phone -> phone.getPhoneNumber().equals(phoneDto.getPhoneNumber()))
                    .findFirst();

            if (existingPhone.isPresent()) {
                PhoneModel phone = existingPhone.get();
                phone.setCodCity(phoneDto.getCodCity());
                phone.setCodCountry(phoneDto.getCodCountry());
            } else {
                PhoneModel newPhone = new PhoneModel();
                newPhone.setPhoneNumber(phoneDto.getPhoneNumber());
                newPhone.setCodCity(phoneDto.getCodCity());
                newPhone.setCodCountry(phoneDto.getCodCountry());
                newPhone.setUser(model);
                model.getPhoneList().add(newPhone);
            }
        }
    }

}
