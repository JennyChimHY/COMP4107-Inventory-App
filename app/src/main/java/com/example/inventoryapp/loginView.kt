package com.example.inventoryapp


import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

var globalLoginStatus: Boolean = false
lateinit var globalLoginInfo: User

@Composable
fun LoginGreeting() {
    val padding = 3.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.size(padding))
        Text(text = "Login", fontSize = 24.sp, modifier = Modifier.padding(16.dp))
    }
}

@Serializable
data class Info(
    val email: String,
    val password: String
)

data class User(
    val first_name: String?,
    val last_name: String?,
    val token: String?,
    val error: String?,
)

@Composable
fun Login(snackbarHostState: SnackbarHostState) {
    val padding = 16.dp
    var emailLocal by remember { mutableStateOf("") } //data class
    var pwdLocal by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val navController = rememberNavController()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Email: ", fontSize = 16.sp,
                modifier = Modifier.padding(18.dp))
            TextField(
                maxLines = 1,
                value = emailLocal,
                onValueChange = { emailLocal = it }
            )
        }
        Spacer(Modifier.size(padding))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Password: ", fontSize = 16.sp)
            TextField(
                maxLines = 1,
                value = pwdLocal,
                onValueChange = { pwdLocal = it }
            )
        }
        Spacer(Modifier.size(padding))

        Button(onClick = {
            coroutineScope.launch {

                val info = Info(emailLocal, pwdLocal) //create an object based on Info data class
//              Log.d("indi", loginSuccess)

                val loginSuccess: User = KtorClient.postLogin(info) //not String message only, but User data class
                Log.d("after", loginSuccess.toString())
                var message = ""
                if (loginSuccess.token != null) {           //success
//                    loginSuccess.last_name?.let {}
                        message =
                            "Login Success. Welcome ${loginSuccess.last_name ?: ""} ${loginSuccess.first_name ?: ""}." //null safety
                    globalLoginStatus = true
                    globalLoginInfo = User(loginSuccess.first_name, loginSuccess.last_name, loginSuccess.token, loginSuccess.error)
//                    HomeNav(navController, snackbarHostState, loginSuccess) //call home page --> make in homeView

                } else if (loginSuccess.error != null)     //error
                    message =
                        "Login Failed. The email or password is incorrect, please input again."
                snackbarHostState.showSnackbar(message)

            }
        }) {
            Text(text = "Login")
        }
    }
}

@Composable
fun LoginScreen(snackbarHostState: SnackbarHostState) {

    Column(horizontalAlignment = Alignment.Start) {
        LoginGreeting()
        Login(snackbarHostState)
    }
}
