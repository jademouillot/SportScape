package fr.isen.mouillot.sportscape

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.mouillot.sportscape.model.Post
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import kotlinx.coroutines.delay
import java.util.Date
import java.util.UUID

class NewPublicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var auth = FirebaseAuth.getInstance()
        var currentUser = auth.currentUser
        setContent {
            SportScapeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        TopBar(title = "Nouvelle Publication")
                        if (currentUser != null && currentUser.email != null){
                            DescriptionSection(
                                postPost = ::postPost,
                                user = currentUser.email ?: "NO EMAIL"
                            )
                        } else {
                            Text("Vous devez être connecté pour publier un post.")
                        }
                    }
                }
            }
        }
    }
    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    private fun postPost(description: String, user: String, returnValue: MutableState<Boolean>) {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("posts")
        val myPost = Post(
            description = description, userId = user, date = Date().time
        ) // Corrected the fields

        val uuid = UUID.randomUUID().toString()
        myRef.child(uuid).setValue(myPost)
        Log.d(ContentValues.TAG, "Post saved successfully.")
        returnValue.value = true
    }


}


@Composable
fun TopBar(title: String) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(), color = Color.Blue
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }, modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.croix),
                    contentDescription = "Retour page principale",
                    tint = Color.White
                )
            }
            Text(
                text = title, color = Color.White, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(48.dp))
        }
    }
}

@Composable
fun DescriptionSection(
    postPost: (String, String, MutableState<Boolean>) -> Unit,
    user: String
) {
    var descriptionText by remember { mutableStateOf("") }
    val publishSnackbarVisible = remember { mutableStateOf(false) }
    val database =
        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")

    Column(
        modifier = Modifier.fillMaxHeight(), // Occupies the entire height of the parent
        verticalArrangement = Arrangement.SpaceBetween // Aligns children vertically with space between them
    ) {
        Column(
            modifier = Modifier.weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Text("Description : ", fontWeight = FontWeight.Bold)
            Surface(
                modifier =
                Modifier.fillMaxWidth(),
                color = Color(232, 232, 232),
                border = BorderStroke(2.dp, Color.Black),
                shape = MaterialTheme.shapes.medium,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                ) {
                    BasicTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (descriptionText.isEmpty()) {
                        Text(
                            text = "  Entrez votre texte ici",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Add padding to the bottom for space
        ) {
            Button(
                onClick = {
                    postPost(descriptionText, user, publishSnackbarVisible)
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text(
                    text = "Publier", color = Color.White
                )
            }
        }
    }
}

@Composable
fun PublishConfirmationSnackbar(
    context: Context, message: String, duration: Int = 1000
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        snackbarHostState.showSnackbar(message)
        delay(duration.toLong())
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }

    Box {
        SnackbarHost(snackbarHostState)
    }
}

