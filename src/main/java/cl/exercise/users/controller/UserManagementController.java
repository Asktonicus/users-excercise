package cl.exercise.users.controller;

import cl.exercise.users.dto.ServiceResponseDTO;
import cl.exercise.users.dto.user.UserRequestDTO;
import cl.exercise.users.dto.user.UserResponseDTO;
import cl.exercise.users.exception.EmailExistException;
import cl.exercise.users.exception.ValidationException;
import cl.exercise.users.service.UserManagementService;
import cl.exercise.users.util.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "Control de Usuarios", description = "Servicio encargado del control y manejo de usuarios de la plataforma")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @Operation(summary = "Agrega usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"mensaje\": \"El correo ya está registrado\"}"))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"mensaje\": \"Error interno en el servidor\"}")))
    })
    @PostMapping
    public ResponseEntity<?> addUser(@Valid @RequestBody UserRequestDTO dto) {
        try {
            ServiceResponseDTO response = userManagementService.addUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailExistException | ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(Constants.MSG, e.getMessage()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Operation(summary = "Busca todos los usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"mensaje\": \"Error al obtener los usuarios\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAllUser(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "orden", required = false) String sortBy,
            @RequestParam(value = "estado", required = false) String status) {

        Page<UserResponseDTO> userList = userManagementService.getAllUser(page, size, sortBy, status);
        return ResponseEntity.ok(userList);
    }

    @Operation(summary = "Busca a un usuario por su correo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"mensaje\": \"Correo inválido\"}")))
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getByEmail(@PathVariable String email){
        try {
            return ResponseEntity.ok(userManagementService.getByEmail(email));
        } catch (EmailExistException | ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(Constants.MSG, e.getMessage()));
        }
    }

    @Operation(summary = "Actualiza usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"mensaje\": \"Usuario no encontrado con ID:\"}")))
    })
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable UUID id,
            @RequestBody UserRequestDTO requestDTO) {
        return userManagementService.updateUser(id, requestDTO);
    }

    @Operation(summary = "Borra usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"mensaje\": \"Usuario no encontrado\"}")))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deactivateUser(@PathVariable UUID id) {
        try{
            userManagementService.deleteUser(id);
            return ResponseEntity.ok(Map.of(Constants.MSG, Constants.DEACTIVATED));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(Constants.MSG, e.getMessage()));
        }
    }

    @Operation(summary = "Activa un usuario desactivado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario activado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json",
                    examples = @ExampleObject(value = "{\"mensaje\": \"Usuario no encontrado\"}")))
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> reactivateUser(@PathVariable UUID id) {
        try {
            userManagementService.activateUser(id);
            return ResponseEntity.ok(Map.of(Constants.MSG, Constants.REACTIVATED));
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(Constants.MSG, e.getMessage()));
        }
    }
}