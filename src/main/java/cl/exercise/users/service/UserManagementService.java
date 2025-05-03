package cl.exercise.users.service;

import cl.exercise.users.dto.ServiceResponseDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.dto.user.UserResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface UserManagementService {

    ServiceResponseDTO addUser(UserRequestDTO user) throws JsonProcessingException;
    UserResponseDTO getByEmail(String email);
    Page<UserResponseDTO> getAllUser(Integer page, Integer size, String sortBy, String status);
    UserResponseDTO updateUser(UUID id, UserRequestDTO user);
    void deleteUser(UUID id);
    void activateUser(UUID id);
}
