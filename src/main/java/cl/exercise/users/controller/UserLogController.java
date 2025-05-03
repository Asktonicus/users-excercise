package cl.exercise.users.controller;

import cl.exercise.users.dto.log.UserLogDTO;
import cl.exercise.users.service.UserLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/logs")
@Tag(
        name = "Monitoreo de cambios de Usuarios",
        description = "Servicio para revisar el log de actividad de cambios en usuarios")
public class UserLogController {

    private final UserLogService userLogService;

    @Operation(summary = "Obtiene el historial de acciones filtrado por usuario")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Historial obtenido correctamente"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "{\"mensaje\": \"Error al obtener el historial de usuarios\"}")))
    })
    @GetMapping
    public ResponseEntity<Page<UserLogDTO>> getAllLog(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "userId", required = false) UUID userId) {

        Page<UserLogDTO> logList = userLogService.getAllLog(page, size, userId);
        return ResponseEntity.ok(logList);
    }
}
