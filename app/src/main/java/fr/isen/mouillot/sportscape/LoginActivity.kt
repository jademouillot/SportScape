package fr.isen.mouillot.sportscape

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme

class EmailPasswordActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Auth
        auth = Firebase.auth


        setContent {

            var currentUser = remember {
                mutableStateOf(auth.currentUser)
            }

            //logIn("doudo@gmail.com", "doudou", currentUser)


            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    // Greeting2(currentUser.value?.email ?: "No user")
                    LoginScreen(currentUser, this::logIn, this::startActivity)
                }
            }
        }
    }

    private fun startActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }

    private fun logIn(email: String, password: String, currentUser: MutableState<FirebaseUser?>) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success")
                currentUser.value = auth.currentUser
                startActivity(MainActivity::class.java)
                finish()
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.exception)
                Toast.makeText(
                    baseContext, "Authentication failed.", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

//    private fun Register(
//        email: String,
//        password: String,
//        currentUser: MutableState<FirebaseUser?>
//    ) {
//        auth.signInWithEmailAndPassword(email, password)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithEmail:success")
//                    currentUser.value = auth.currentUser
//                    startActivity(RegisterActivity::class.java)
//                    finish()
//
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(
//                        baseContext,
//                        "Authentication failed.",
//                        Toast.LENGTH_SHORT,
//                    ).show()
//
//                }
//            }
//    }
}

@Composable
fun LoginScreen(
    currentUser: MutableState<FirebaseUser?>,
    logIn: (String, String, MutableState<FirebaseUser?>) -> Unit,
    startActivity: (Class<*>) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
                    Text(
                "Welcome",
                fontSize = 24.sp,
                color = Color.Green,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        Card {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },

            )


        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },

        )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { logIn(email, password, currentUser) }) {
            Text("Log in")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { startActivity(RegisterActivity::class.java) }) {
            Text("Create account")
        }
    }
}