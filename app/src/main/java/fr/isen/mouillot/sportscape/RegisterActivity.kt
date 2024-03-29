package fr.isen.mouillot.sportscape

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG
import com.google.firebase.storage.FirebaseStorage
import fr.isen.mouillot.sportscape.model.User
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import java.util.UUID

class RegisterActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    //private lateinit var selectImage: ActivityResultLauncher<String>
    //private lateinit var pickImageLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

//        pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//            // Handle the Uri
//            uri?.let {
//                uploadImageToFirebaseStorage(it, this) { imageUrl ->
//                    // Store the image URL in the database
//                    postdataUser(imageUrl)
//                    // Now you have the imageUrl which is the URL of the uploaded image
//                    Log.d(TAG, "Image URL: $imageUrl")
//                }
//            }
//        }
        setContent {
            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    var username by remember { mutableStateOf("") }
                    var photoUrl by remember { mutableStateOf("") }
                    val errorMessage = remember { mutableStateOf("") }

                    RegisterScreen(
                        currentUser = remember { mutableStateOf(null) },
                        register = { email, password, username, photoUrl ->
                            Register(email, password, username, photoUrl)
                            //postdataUser("email", "password", "username", "photoUrl")
                            startActivity(MainActivity::class.java)
                        },
                        context = LocalContext.current,
                        startActivity = this::startActivity

                    )
                }

            }
        }
    }
    fun uploadImageToFirebaseStorage(
        imageUri: Uri, context: Context, onImageUploaded: (String) -> Unit
    ) {
        val storageRef =
            FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")
        storageRef.putFile(imageUri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.d("Upload", "Image Upload Failed: ${it.message}")
                    throw it
                }
            }
            storageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                Log.d("Upload", "Image Upload Successful: $downloadUri")
                onImageUploaded(downloadUri)
            } else {
                Toast.makeText(
                    context, "Upload failed: ${task.exception?.message}", Toast.LENGTH_SHORT
                ).show()
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
    ) {
        if (password.isEmpty() || username.isEmpty()) {
            Log.d("Baptiste", "Veuillez remplir les champs.")
            return
        }
        Log.d("Baptiste", "createUserWithEmail:$email")
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("tmp").push()

        val user = User(username, email, photoUrl)

        myRef.setValue(user)

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("Baptiste", "createUserWithEmail:success")
            } else {
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                if (task.exception is FirebaseAuthUserCollisionException) {
                    Log.d("Baptiste", "Account already exists.")
                } else {
                    Log.d("Baptiste", "Authentication failed.")
                }
            }
        }
    }


    fun postdata() {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("tmp").push().setValue("Hello, Worldddd!")

        //myRef.setValue("Hello, World!")

        // Pour lire des données

    }

fun postdataUser(imageUrl: String) {
    val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("users").push()

    val user = hashMapOf(
        "imageUrl" to imageUrl
    )

    myRef.setValue(user)
        .addOnSuccessListener {
            Log.d(TAG, "User saved successfully.")
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Failed to save user.", e)
        }
}



    @Composable
    fun RegisterScreen(
        currentUser: MutableState<FirebaseUser?>,
        register: (String, String, String, String) -> Unit,

        startActivity: (Class<*>) -> Unit,
        context: Context
    ) {
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

//            Button(onClick = { pickImageLauncher.launch("image/*") }) {
//                Text("Pick Image")
//            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                    errorMessage = "Veuillez remplir les champs."
                    Toast.makeText(
                        context, errorMessage, Toast.LENGTH_SHORT
                    ).show()
                } else {
                    register(email, password, username, photoUrl)
                }
            }) {
                Text("Create account")
            }

        }

    }
}


@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

