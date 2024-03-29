package fr.isen.mouillot.sportscape

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import fr.isen.mouillot.sportscape.model.Post
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import kotlinx.coroutines.delay
import java.util.Date
import java.util.UUID

class NewPublicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        setContent {
            SportScapeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        TopBar(title = "Nouvelle Publication")
                        if (currentUser != null && currentUser.email != null) {
                            val publishText: (String, String) -> Unit = { description, userEmail ->
                                postPost(description, userEmail)
                            }
                            val publishImage: (List<String>, String) -> Unit = { images, postId ->
                                postPhoto(images, postId)
                            }
                            DescriptionSection(
                                postText = publishText,
                                postImage = publishImage,
                                user = currentUser.email ?: "NO EMAIL",
                                context = this@NewPublicationActivity
                            )
                        } else {
                            Text("Vous devez être connecté pour publier un post.")
                        }
                    }
                }
            }
        }
    }

    private fun postPost(description: String, userEmail: String) {
        val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("posts")
        val uuid = UUID.randomUUID().toString()
        val myPost = Post(
            description = description,
            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
            date = Date().time,
            userEmail = userEmail,
            id = uuid,
            likes = 0
        )

        myRef.child(uuid).setValue(myPost)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "Post saved successfully.")
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Failed to save post: $exception")
                // Handle post saving failure if necessary
            }
    }

    private fun postPhoto(images: List<String>, postId: String) {
        //val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        //val myRef = database.getReference("posts").child(postId).child("images")

        images.forEachIndexed { index, uriString ->
            val imageRef = FirebaseStorage.getInstance().reference.child("images")
                .child("$postId/image$index.jpg")
            val imageUri = Uri.parse(uriString)
            imageRef.putFile(imageUri)
                .addOnSuccessListener { _ ->
                    // Handle photo upload success if necessary
                }
                .addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "Failed to upload photo: $exception")
                    // Handle photo upload failure if necessary
                }
        }
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
    postText: (String, String) -> Unit,
    postImage: (List<String>, String) -> Unit,
    user: String,
    context: Context
) {
    var descriptionText by remember { mutableStateOf("") }
    val selectedImages = remember { mutableStateOf<List<String>?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImages.value = listOf(it.toString())
        }
    }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Button(
                onClick = {
                    pickImageLauncher.launch("image/*")
                },
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            ) {
                Text("Ajouter des images")
            }

            Text("Description : ", fontWeight = FontWeight.Bold)
            Surface(
                modifier = Modifier.fillMaxWidth(),
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
        Column {
            selectedImages.value?.let { images ->
                images.forEach { imageUri ->
                    val painter = rememberImagePainter(imageUri)
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val imageUrls = selectedImages.value ?: emptyList()
                    val postId = UUID.randomUUID().toString()
                    postText(descriptionText, user)
                    postImage(imageUrls, postId)
                    Toast.makeText(context, "Post ajouté !", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }, 1500)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text(text = "Publier", color = Color.White)
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
