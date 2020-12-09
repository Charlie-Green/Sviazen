package by.zenkevich_churun.findcell.prisoner.ui.auth.model


sealed class AuthorizationState {
    object Idle: AuthorizationState()
    object Loading: AuthorizationState()
    object InvalidUsername: AuthorizationState()
    object InvalidPassword: AuthorizationState()
    object NoInternet: AuthorizationState()
    class UsernameTaken(val username: String): AuthorizationState()
    object UsernameNotExist: AuthorizationState()
    object PasswordNotMatch: AuthorizationState()
    class NetworkError(val wasLoggingIn: Boolean): AuthorizationState()
    object Success: AuthorizationState()
}