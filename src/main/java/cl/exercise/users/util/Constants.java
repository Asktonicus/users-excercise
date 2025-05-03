package cl.exercise.users.util;

public class Constants {

    private Constants() {
        throw new UnsupportedOperationException("Constants class cannot be instantiated");
    }

    /**
     * Constantes globales
     */
    public static final String MSG                      = "mensaje";

    /**
     * Constantes de negocio
     */
    public static final String EMAIL_REGISTERED         = "El correo ya está registrado";
    public static final String USER_NOT_FOUND           = "Usuario no encontrado";
    public static final String USER_NOT_FOUND_W_EMAIL   = "Usuario no encontrado con email: ";
    public static final String USER_NOT_FOUND_W_ID      = "Usuario no encontrado con ID: ";
    public static final String ACTIVE                   = "activos";
    public static final String INACTIVE                 = "inactivos";
    public static final String ALREADY_ACTIVATED        = "El usuario ya está activo";
    public static final String ALREADY_DEACTIVATED      = "El usuario ya esta desactivado";
    public static final String INACTIVE_CANT_UPDATE     = "El usuario está desactivado y no puede ser actualizado";

    /**
     * Constantes de Validación
     */
    public static final String INVALID_PASSWORD_FORMAT  = "Formato de contraseña inválido";
    public static final String INVALID_EMAIL_FORMAT     = "Formato de correo inválido";
    public static final String VALIDATION_ERROR         = "Error de validación";
    public static final String BODY_CANT_EMPTY          = "El cuerpo de la solicitud no puede estar vacío";
    public static final String INTERNAL_SERVER          = "Error interno en el servidor";
    public static final String INVALID_UUID             = "El ID proporcionado no tiene el formato UUID válido.";
    public static final String INVALID_PARAMETER        = "Parámetro inválido: ";

    /**
     * Constantes de Loggeo
     */
    public static final String CREATED                  = "Nuevo usuario creado";
    public static final String UPDATED                  = "Usuario actualizado";
    public static final String REACTIVATED              = "Usuario activado";
    public static final String DEACTIVATED              = "Usuario desactivado";

}
