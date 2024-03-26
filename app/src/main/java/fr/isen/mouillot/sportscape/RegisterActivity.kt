package fr.isen.mouillot.sportscape

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var takePicture: ActivityResultLauncher<Void?>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        takePicture = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            // Handle the bitmap
        }
        setContent {
            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var username by remember { mutableStateOf("") }
                    var photoUrl by remember { mutableStateOf("") }
                    val errorMessage = remember { mutableStateOf("") }

                    RegisterScreen(currentUser = remember { mutableStateOf(null) }, register = { email, password, currentUser ->
                        Register(email, password, username, photoUrl, currentUser, errorMessage)
                    }, takePicture = takePicture)
                }
            }
        }
    }
    private fun startActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
    private fun Register(
        email: String,
        password: String,
        username: String,
        photoUrl: String,
        currentUser: MutableState<FirebaseUser?>,
        errorMessage: MutableState<String>
    ) {
        if (password.isEmpty() || username.isEmpty() || photoUrl.isEmpty()) {
            errorMessage.value = "All fields must be filled."
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .setPhotoUri(Uri.parse(photoUrl))
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "User profile updated.")
                            currentUser.value = user
                            startActivity(MainActivity::class.java)
                            errorMessage.value = "Vous êtes bien enregistré."
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        errorMessage.value = "Account already exists."
                    } else {
                        errorMessage.value = "Authentication failed."
                    }
                }
            }
    }
}


@Composable
fun RegisterScreen(currentUser: MutableState<FirebaseUser?>, register: (String, String, MutableState<FirebaseUser?>) -> Unit, takePicture: ActivityResultLauncher<Void?>){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = photoUrl,
            onValueChange = { photoUrl = it },
            label = { Text("Photo URL") },
            modifier = Modifier.fillMaxWidth().clickable { takePicture.launch(null) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { register(email, password, currentUser) }) {
            Text("Create account")
        }
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }
    }
}

@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    SportScapeTheme {
        Greeting3("Android")
    }
}