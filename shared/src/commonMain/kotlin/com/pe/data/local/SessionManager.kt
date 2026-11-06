package pe.edu.upeu.data.local

import com.pe.domain.model.Usuario
import com.russhwolf.settings.Settings
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

class SessionManager(private val settings: Settings ) {
    companion object{
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER ="user_data"
    }
    fun saveToken(token: String){
        print("DEBUG_SESSION: Saving token: $token")
        settings.putString(KEY_TOKEN,token)
    }
    fun getToken(): String?{
        val token = settings.getStringOrNull(KEY_TOKEN)
        print("DEBUG_SESSION: retrieved token: $token")
        return token
    }
    fun saveUser(user: Usuario){
        print("DEBUG_SESSION: Saving user:${user.nombres}")
        val json = Json.encodeToString(user)
        settings.putString(KEY_USER,json)
    }
    fun getUser(): Usuario?{
        val json = settings.getStringOrNull(KEY_USER)
        print("DEBUG_SESSION: retrieved USER: $json")
        return try {
            Json.decodeFromString<Usuario>(json ?: return null)
        }catch (e: Exception){
            println("DEBUG_SESSION: Error decoding user: ${e.message}")
            null
        }
    }
    fun clear(){
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER)
    }
    fun isLoggedIn(): Boolean= getToken()!=null
}