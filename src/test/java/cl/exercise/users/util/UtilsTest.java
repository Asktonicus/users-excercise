package cl.exercise.users.util;
import cl.exercise.users.dto.user.PhoneDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.model.PhoneModel;
import cl.exercise.users.model.UserModel;
import cl.exercise.users.validator.ValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    private UserModel user;
    private UserRequestDTO dto;
    private PasswordEncoder passwordEncoder;
    private ValidationHandler validationHandler;

    @BeforeEach
    public void setup() {
        user = new UserModel();
        user.setName("Original Name");
        user.setEmail("original@email.com");
        user.setPasswd("original-password");

        user.setPhoneList(new ArrayList<>());

        dto = new UserRequestDTO();
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        validationHandler = Mockito.mock(ValidationHandler.class);
    }

    @Test
    public void testPrivateConstructorThrowsException() throws Exception {
        Constructor<Utils> constructor = Utils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThrows(InvocationTargetException.class, constructor::newInstance);
    }

    @Test
    public void testUpdateName() {
        dto.setName("Updated Name");

        Utils.updateBasicInfo(user, dto, passwordEncoder, validationHandler);

        assertEquals("Updated Name", user.getName());
    }

    @Test
    public void testUpdateEmail_validNewEmail() {
        dto.setEmail("new@email.com");

        Utils.updateBasicInfo(user, dto, passwordEncoder, validationHandler);

        Mockito.verify(validationHandler).validateEmail("new@email.com");
        assertEquals("new@email.com", user.getEmail());
    }

    @Test
    public void testUpdateEmail_sameEmail_shouldNotUpdate() {
        dto.setEmail("original@email.com");

        Utils.updateBasicInfo(user, dto, passwordEncoder, validationHandler);

        Mockito.verify(validationHandler, Mockito.never()).validateEmail(Mockito.anyString());
        assertEquals("original@email.com", user.getEmail());
    }

    @Test
    public void testUpdatePassword() {
        dto.setPasswd("new-pass");
        Mockito.when(passwordEncoder.encode("new-pass")).thenReturn("encoded-pass");

        Utils.updateBasicInfo(user, dto, passwordEncoder, validationHandler);

        Mockito.verify(validationHandler).validatePassword("new-pass");
        Mockito.verify(passwordEncoder).encode("new-pass");
        assertEquals("encoded-pass", user.getPasswd());
    }

    @Test
    public void testUpdatePhones_withExistingPhone_updatesCodCityAndCodCountry() {
        PhoneModel existing = new PhoneModel();
        existing.setPhoneNumber("1234567");
        existing.setCodCity("1");
        existing.setCodCountry("56");
        user.getPhoneList().add(existing);

        PhoneDTO dto = PhoneDTO.builder()
                .phoneNumber("1234567")
                .codCity("9")
                .codCountry("99")
                .build();

        Utils.updatePhones(user, List.of(dto));

        assertEquals(1, user.getPhoneList().size());
        PhoneModel updated = user.getPhoneList().get(0);
        assertEquals("9", updated.getCodCity());
        assertEquals("99", updated.getCodCountry());
    }

    @Test
    public void testUpdatePhones_withNewPhone_addsNewPhone() {
        PhoneDTO dto = PhoneDTO.builder()
                .phoneNumber("7654321")
                .codCity("2")
                .codCountry("57")
                .build();

        Utils.updatePhones(user, List.of(dto));

        assertEquals(1, user.getPhoneList().size());
        PhoneModel added = user.getPhoneList().get(0);
        assertEquals("7654321", added.getPhoneNumber());
        assertEquals("2", added.getCodCity());
        assertEquals("57", added.getCodCountry());
        assertSame(user, added.getUser());
    }

    @Test
    public void testUpdatePhones_withNullList_doesNothing() {
        Utils.updatePhones(user, null);
        assertTrue(user.getPhoneList().isEmpty());
    }

    @Test
    public void testUpdatePhones_withEmptyList_doesNothing() {
        Utils.updatePhones(user, Collections.emptyList());
        assertTrue(user.getPhoneList().isEmpty());
    }

}

