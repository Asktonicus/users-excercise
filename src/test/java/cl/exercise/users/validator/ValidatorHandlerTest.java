package cl.exercise.users.validator;

import cl.exercise.users.TestUtils;
import cl.exercise.users.dto.user.PhoneDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.exception.ValidationException;
import cl.exercise.users.model.UserModel;
import cl.exercise.users.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ValidatorHandlerTest {
    private ValidationHandler validationHandler;

    @BeforeEach
    public void setup() {
        validationHandler = new ValidationHandler();

        TestUtils.setField(validationHandler, "emailRegex", "^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$");
        TestUtils.setField(validationHandler, "pswdRegex", "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{6,}$");
    }

    @Test
    public void testValidateEmail_validEmail() {
        Assertions.assertDoesNotThrow(() -> validationHandler.validateEmail("test@example.com"));
    }

    @Test
    public void testValidateEmail_invalidEmail() {
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validateEmail("invalid-email")
        );
        Assertions.assertEquals(Constants.INVALID_EMAIL_FORMAT, ex.getMessage());
    }

    @Test
    public void testValidatePassword_validPassword() {
        Assertions.assertDoesNotThrow(() -> validationHandler.validatePassword("StrongPass1"));
    }

    @Test
    public void testValidatePassword_invalidPassword() {
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePassword("weak")
        );
        Assertions.assertEquals(Constants.INVALID_PASSWORD_FORMAT, ex.getMessage());
    }

    @Test
    public void testValidatePhoneList_validPhones() {
        PhoneDTO phone = PhoneDTO.builder()
                .phoneNumber("1234567")
                .codCity("1")
                .codCountry("56")
                .build();
        List<PhoneDTO> phoneList = List.of(phone);
        Assertions.assertDoesNotThrow(() -> validationHandler.validatePhoneList(phoneList));
    }

    @Test
    public void testValidatePhoneList_emptyList() {
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(List.of())
        );
        Assertions.assertTrue(ex.getMessage().contains("no puede estar vacía"));
    }

    @Test
    public void testValidatePhoneList_nullList() {
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(null)
        );
        Assertions.assertTrue(ex.getMessage().contains("no puede estar vacía"));
    }

    @Test
    public void testValidateUserRequest_valid() {
        UserRequestDTO user = UserRequestDTO.builder()
                .email("valid@email.com")
                .passwd("Password1")
                .build();
        Assertions.assertDoesNotThrow(() -> validationHandler.validateUserRequest(user));
    }

    @Test
    public void testValidateUserIsActive_activeUser() {
        UserModel active = new UserModel();
        active.setIsActive(true);

        Assertions.assertDoesNotThrow(() -> validationHandler.validateUserIsActive(active));
    }

    @Test
    public void testValidateUserIsActive_inactiveUser() {
        UserModel inactive = new UserModel();
        inactive.setIsActive(false);

        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validateUserIsActive(inactive)
        );
        Assertions.assertEquals(Constants.INACTIVE_CANT_UPDATE, ex.getMessage());
    }

    @Test
    public void testIsNullOrEmpty_null() throws Exception {
        Method method = ValidationHandler.class.getDeclaredMethod("isNullOrEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(validationHandler, (Object) null);
        Assertions.assertTrue(result);
    }

    @Test
    public void testIsNullOrEmpty_empty() throws Exception {
        Method method = ValidationHandler.class.getDeclaredMethod("isNullOrEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(validationHandler, "");
        Assertions.assertTrue(result);
    }

    @Test
    public void testIsNullOrEmpty_whitespaceOnly() throws Exception {
        Method method = ValidationHandler.class.getDeclaredMethod("isNullOrEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(validationHandler, "   ");
        Assertions.assertTrue(result);
    }

    @Test
    public void testIsNullOrEmpty_validString() throws Exception {
        Method method = ValidationHandler.class.getDeclaredMethod("isNullOrEmpty", String.class);
        method.setAccessible(true);

        boolean result = (boolean) method.invoke(validationHandler, "valid");
        Assertions.assertFalse(result);
    }

    @Test
    public void testPhoneIsNull() {
        List<PhoneDTO> phones = new ArrayList<>();
        PhoneDTO phone2 = PhoneDTO.builder()
                .phoneNumber("1234567")
                .codCity("9")
                .codCountry("56")
                .build();
        phones.add(null);
        phones.add(phone2);
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(phones)
        );
        Assertions.assertEquals("El teléfono en la posición 1 es nulo", ex.getMessage());
    }

    @Test
    public void testPhoneNumberIsEmpty() {
        PhoneDTO phone = PhoneDTO.builder()
                .phoneNumber("")
                .codCity("1")
                .codCountry("56")
                .build();
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(List.of(phone))
        );
        Assertions.assertEquals("El número de teléfono en la posición 1 es requerido", ex.getMessage());
    }

    @Test
    public void testPhoneNumberInvalidFormat() {
        PhoneDTO phone = PhoneDTO.builder()
                .phoneNumber("1234abc")
                .codCity("1")
                .codCountry("56")
                .build();
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(List.of(phone))
        );
        Assertions.assertEquals("El número de teléfono en la posición 1 debe tener solo dígitos y entre 7 y 8 caracteres", ex.getMessage());
    }

    @Test
    public void testCodCityIsEmpty() {
        PhoneDTO phone = PhoneDTO.builder()
                .phoneNumber("1234567")
                .codCity("")
                .codCountry("56")
                .build();
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(List.of(phone))
        );
        Assertions.assertEquals("El código de ciudad en la posición 1 es requerido", ex.getMessage());
    }

    @Test
    public void testCodCityInvalidFormat() {
        PhoneDTO phone = PhoneDTO.builder()
            .phoneNumber("1234567")
            .codCity("1A")
            .codCountry("56")
            .build();
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(List.of(phone))
        );
        Assertions.assertEquals("El código de ciudad en la posición 1 debe contener solo números", ex.getMessage());
    }

    @Test
    public void testCodCountryIsEmpty() {
        PhoneDTO phone = PhoneDTO.builder()
                .phoneNumber("1234567")
                .codCity("1")
                .codCountry("")
                .build();
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(List.of(phone))
        );
        Assertions.assertEquals("El código de país en la posición 1 es requerido", ex.getMessage());
    }

    @Test
    public void testCodCountryInvalidFormat() {
        PhoneDTO phone = PhoneDTO.builder()
                .phoneNumber("1234567")
                .codCity("1")
                .codCountry("5A")
                .build();
        ValidationException ex = Assertions.assertThrows(ValidationException.class, () ->
                validationHandler.validatePhoneList(List.of(phone))
        );
        Assertions.assertEquals("El código de país en la posición 1 debe contener solo números", ex.getMessage());
    }

    @Test
    public void testValidPhonePasses() {
        PhoneDTO phone = PhoneDTO.builder()
                .phoneNumber("1234567")
                .codCity("1")
                .codCountry("56")
                .build();
        Assertions.assertDoesNotThrow(() -> validationHandler.validatePhoneList(List.of(phone)));
    }
}