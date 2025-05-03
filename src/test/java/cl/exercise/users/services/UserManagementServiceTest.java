package cl.exercise.users.services;

import cl.exercise.users.dto.user.PhoneDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.dto.user.UserResponseDTO;
import cl.exercise.users.dto.ServiceResponseDTO;
import cl.exercise.users.exception.EmailExistException;
import cl.exercise.users.exception.ValidationException;
import cl.exercise.users.mapper.MapperHelper;
import cl.exercise.users.model.UserLogModel;
import cl.exercise.users.model.UserModel;
import cl.exercise.users.repository.UserLogRepository;
import cl.exercise.users.repository.UserManagementRepository;
import cl.exercise.users.service.impl.UserManagementServiceImpl;
import cl.exercise.users.util.Constants;
import cl.exercise.users.util.JwtUtil;
import cl.exercise.users.util.JwtUtilTest;
import cl.exercise.users.validator.ValidationHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock private UserManagementRepository userManagementRepository;
    @Mock private UserLogRepository userLogRepository;
    @Mock private MapperHelper mapperHelper;
    @Mock private ValidationHandler validationHandler;
    @Mock private JwtUtil jwtUtil;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserManagementServiceImpl service;

    private UserRequestDTO userRequestDTO;
    private UserModel userModel;

    @BeforeEach
    void setUp() {

        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setName("Nuevo Nombre");
        userRequestDTO.setEmail("nuevo@email.com");
        userRequestDTO.setPasswd("newpass123");

        PhoneDTO phone = new PhoneDTO();
        phone.setPhoneNumber("12345678");
        phone.setCodCity("1");
        phone.setCodCountry("56");
        userRequestDTO.setPhoneList(List.of(phone));

        userModel = new UserModel();
        userModel.setId(UUID.randomUUID());
        userModel.setName("Original");
        userModel.setEmail("test@example.com");
        userModel.setPasswd("1234");
        userModel.setIsActive(true);
        userModel.setPhoneList(new ArrayList<>());

    }

    @Test
    void addUser_whenEmailAlreadyExists_shouldThrowEmailExistException() {
        when(userManagementRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(true);

        assertThrows(EmailExistException.class, () -> service.addUser(userRequestDTO));
    }

    @Test
    void addUser_success_shouldReturnResponse() {
        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));
        when(mapperHelper.toEntity(any())).thenReturn(userModel);
        when(jwtUtil.generateToken(any())).thenReturn("fake-token");
        when(userManagementRepository.save(any())).thenReturn(userModel);
        when(mapperHelper.mapToGenericResponse(any())).thenReturn(new ServiceResponseDTO());

        ServiceResponseDTO response = service.addUser(userRequestDTO);

        assertNotNull(response);
        verify(userManagementRepository, times(2)).save(any(UserModel.class));
    }

    @Test
    void getByEmail_existingEmail_shouldReturnUser() {
        when(userManagementRepository.existsByEmail(userModel.getEmail())).thenReturn(true);
        when(userManagementRepository.findByEmailIgnoreCase(userModel.getEmail())).thenReturn(userModel);
        when(mapperHelper.mapToUserResponse(any())).thenReturn(new UserResponseDTO());

        UserResponseDTO response = service.getByEmail(userModel.getEmail());

        assertNotNull(response);
    }

    @Test
    void getByEmail_nonExistingEmail_shouldThrowException() {
        when(userManagementRepository.existsByEmail(userModel.getEmail())).thenReturn(false);

        assertThrows(EmailExistException.class, () -> service.getByEmail(userModel.getEmail()));
    }

    @Test
    void getAllUser_returnsActiveUsers() {
        userModel.setIsActive(true);

        Page<UserModel> userPage = new PageImpl<>(List.of(userModel));
        when(userManagementRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(mapperHelper.mapToUserResponse(any(UserModel.class))).thenReturn(new UserResponseDTO());

        Page<UserResponseDTO> response = service.getAllUser(1, 10, "creationDate", "activos");

        assertFalse(response.isEmpty());
        verify(mapperHelper, times(1)).mapToUserResponse(any(UserModel.class));
    }

    @Test
    void getAllUser_whenStatusIsInactivos_shouldReturnOnlyInactiveUsers() {
        UserModel activeUser = new UserModel();
        activeUser.setIsActive(true);

        UserModel inactiveUser = new UserModel();
        inactiveUser.setIsActive(false);

        List<UserModel> users = List.of(activeUser, inactiveUser);
        Page<UserModel> userPage = new PageImpl<>(users);

        when(userManagementRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(mapperHelper.mapToUserResponse(any(UserModel.class))).thenReturn(new UserResponseDTO());

        Page<UserResponseDTO> result = service.getAllUser(1, 10, "email", "inactivos");

        // Solo debería quedar 1 usuario, el inactivo
        assertEquals(1, result.getContent().size());
        verify(userManagementRepository).findAll(any(Pageable.class));
        verify(mapperHelper, times(1)).mapToUserResponse(inactiveUser);
    }


    @Test
    void getAllUser_whenPageIsNull_shouldDefaultToPageZero() {
        when(userManagementRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<UserResponseDTO> result = service.getAllUser(null, 10, "email", null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userManagementRepository).findAll(captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
    }

    @Test
    void getAllUser_whenPageIsLessThanOne_shouldDefaultToPageZero() {
        when(userManagementRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        Page<UserResponseDTO> result = service.getAllUser(0, 10, "email", null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userManagementRepository).findAll(captor.capture());
        assertEquals(0, captor.getValue().getPageNumber());
    }


    @Test
    void getAllUser_whenSizeIsNull_shouldDefaultToTen() {
        when(userManagementRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.getAllUser(1, null, "email", null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userManagementRepository).findAll(captor.capture());
        assertEquals(10, captor.getValue().getPageSize());
    }


    @Test
    void getAllUser_whenSizeIsLessThanOne_shouldDefaultToTen() {
        when(userManagementRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.getAllUser(1, 0, "email", null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userManagementRepository).findAll(captor.capture());
        assertEquals(10, captor.getValue().getPageSize());
    }


    @Test
    void getAllUser_whenSortByIsNull_shouldSortByCreationDate() {
        when(userManagementRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.getAllUser(1, 10, null, null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userManagementRepository).findAll(captor.capture());
        assertEquals(Sort.by("creationDate"), captor.getValue().getSort());
    }


    @Test
    void getAllUser_whenSortByIsBlank_shouldSortByCreationDate() {
        when(userManagementRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        service.getAllUser(1, 10, "   ", null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userManagementRepository).findAll(captor.capture());
        assertEquals(Sort.by("creationDate"), captor.getValue().getSort());
    }

    @Test
    void getAllUser_returnsAllUsersWhenNoStatusProvided() {

        Page<UserModel> userPage = new PageImpl<>(List.of(userModel));
        when(userManagementRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(mapperHelper.mapToUserResponse(any(UserModel.class))).thenReturn(new UserResponseDTO());

        Page<UserResponseDTO> response = service.getAllUser(null, null, null, null);

        assertFalse(response.isEmpty());
    }

    @Test
    void activateUser_inactiveUser_logsReactivation() {
        userModel.setIsActive(false);
        userModel.setEmail("user@email.com");

        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));
        when(userManagementRepository.save(any(UserModel.class))).thenReturn(userModel);

        service.activateUser(userModel.getId());

        verify(userManagementRepository).save(userModel);
        verify(userLogRepository).save(any(UserLogModel.class));
    }

    @Test
    void activateUser_alreadyActive_shouldThrowValidationException() {
        when(userManagementRepository.findById(any())).thenReturn(Optional.of(userModel));

        assertThrows(ValidationException.class, () -> service.activateUser(UUID.randomUUID()));
    }

    @Test
    void activateUser_success_shouldUpdateStatusAndLog() {
        userModel.setIsActive(false);
        when(userManagementRepository.findById(any())).thenReturn(Optional.of(userModel));

        service.activateUser(UUID.randomUUID());

        verify(userManagementRepository).save(any());
        verify(userLogRepository).save(any());
    }

    @Test
    void activateUser_userNotFound_shouldThrowValidationException() {
        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            service.activateUser(userModel.getId());
        });

        assertEquals(Constants.USER_NOT_FOUND, exception.getMessage());
    }


    @Test
    void deleteUser_activeUser_logsDeactivation() {
        userModel.setEmail("user@email.com");

        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));
        when(userManagementRepository.save(any(UserModel.class))).thenReturn(userModel);

        service.deleteUser(userModel.getId());

        verify(userManagementRepository).save(userModel);
        verify(userLogRepository).save(any(UserLogModel.class));
    }

    @Test
    void deleteUser_userNotFound_shouldThrowValidationException() {
        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            service.deleteUser(userModel.getId());
        });

        assertEquals(Constants.USER_NOT_FOUND, exception.getMessage());
    }

    @Test
    void deleteUser_inactiveUser_shouldThrowValidationException() {
        userModel.setIsActive(false);
        when(userManagementRepository.findById(any())).thenReturn(Optional.of(userModel));

        assertThrows(ValidationException.class, () -> service.deleteUser(UUID.randomUUID()));
    }

    @Test
    void deleteUser_success_shouldUpdateStatusAndLog() {
        when(userManagementRepository.findById(any())).thenReturn(Optional.of(userModel));

        service.deleteUser(UUID.randomUUID());

        verify(userManagementRepository).save(any());
        verify(userLogRepository).save(any());
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.empty());

        assertThrows(
                ValidationException.class, () -> service.updateUser(userModel.getId(), userRequestDTO));
    }

    @Test
    void shouldThrowWhenUserIsInactive() {
        userModel.setIsActive(false);

        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));

        doThrow(new ValidationException(Constants.INACTIVE_CANT_UPDATE))
                .when(validationHandler).validateUserIsActive(userModel);

        UserRequestDTO dummyRequest = new UserRequestDTO();
        dummyRequest.setName("Test");

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            service.updateUser(userModel.getId(), dummyRequest);
        });

        assertEquals(Constants.INACTIVE_CANT_UPDATE, exception.getMessage());
    }

    @Test
    void shouldUpdateUserDetailsSuccessfully() {

        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));
        when(userManagementRepository.save(any(UserModel.class))).thenAnswer(i -> i.getArgument(0));
        when(mapperHelper.mapToUserResponse(any(UserModel.class))).thenReturn(new UserResponseDTO());

        UserResponseDTO response = service.updateUser(userModel.getId(), userRequestDTO);

        assertNotNull(response);
        verify(userManagementRepository).save(userModel);
        verify(validationHandler).validateUserIsActive(userModel);
    }

    @Test
    void shouldIgnoreEmptyFieldsInUpdate() {
        UserRequestDTO request = new UserRequestDTO(); // campos nulos

        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));
        when(userManagementRepository.save(any(UserModel.class))).thenReturn(userModel);
        when(mapperHelper.mapToUserResponse(any(UserModel.class))).thenReturn(new UserResponseDTO());

        UserResponseDTO response = service.updateUser(userModel.getId(), request);

        assertNotNull(response);
        verify(userManagementRepository).save(userModel);
    }

    @Test
    void generarToken_whenUserFound_shouldReturnToken() {
        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.of(userModel));

        String expectedToken = "dummy-token";

        when(jwtUtil.generateToken(userModel)).thenReturn(expectedToken);

        String token = service.generateToken(userModel);

        assertEquals(expectedToken, token);
    }

    @Test
    void generarToken_whenUserNotFound_shouldThrowValidationException() {
        when(userManagementRepository.findById(userModel.getId())).thenReturn(Optional.empty());

        ValidationException ex = assertThrows(ValidationException.class,
                () -> service.generateToken(userModel));

        assertTrue(ex.getMessage().contains(Constants.USER_NOT_FOUND));
    }
}
