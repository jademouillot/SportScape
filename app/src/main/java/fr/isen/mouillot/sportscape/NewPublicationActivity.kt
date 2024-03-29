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
import androidx.compose.ui.semantics.Role.Companion.Button
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
                        if (currentUser != null && currentUser.email != null) {
                            DescriptionSection(
                                postPost = ::postPost,
//                                selectedPhotos = selectedPhotos, GROS DOUTE VOIR AVCE LES FILLE
                                user = currentUser.email ?: "NO EMAIL"
                            )
                        } else {
                            Text("Vous devez être connecté pour publier un post.")
                        }
                    }

//                        DescriptionSection(selectedPhotos)
                }
            }
        }
    }


    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK) {
//            // Récupère les Uri des photos sélectionnées depuis l'intention
//            val clipData = data?.clipData
//            if (clipData != null) {
//                for (i in 0 until clipData.itemCount) {
//                    val uri = clipData.getItemAt(i).uri
//                    this.selectedPhotos.add(uri) // Mettre à jour la variable globale avec les photos sélectionnées
//                }
//            } else {
//                val uri = data?.data
//                if (uri != null) {
//                    selectedPhotos.add(uri) // Mettre à jour la variable globale avec les photos sélectionnées
//                }
//            }
//        }
//    }
    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    private fun postPost(
        description: String,
        images: List<String>?,
        userEmail: String,
        returnValue: MutableState<Boolean>
    ) {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
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


        // Vérifiez si des images sont présentes
        if (!images.isNullOrEmpty()) {
            val imageUrls = mutableListOf<String>()
            val storageRef = FirebaseStorage.getInstance().reference.child("images")

            // Parcourez chaque image pour les télécharger dans Firebase Storage
            images.forEachIndexed { index, uriString ->
//                val imageRef = storageRef.child("$uuid/image$index.jpg")
                val imageRef = storageRef.child("$uuid.jpg")
                val imageUri = Uri.parse(uriString)
                val uploadTask = imageRef.putFile(imageUri)
                uploadTask.addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        // Utilisez downloadUri.toString() pour obtenir l'URL de téléchargement de la photo

                        val imageUrl = downloadUri.toString()
                        imageUrls.add(imageUrl)

                        // Vérifiez si toutes les images ont été téléchargées
                        if (imageUrls.size == images.size) {
                            // Ajoutez les URLs d'images à l'objet Post
                            myPost.images = imageUrls

                            imageUrls.forEachIndexed { index, imageUrl ->
                                Log.d("Image URL", "Image $index URL: $imageUrl")
                            }

                            // Enregistrez l'objet Post dans la base de données Firebase
                            myRef.child(uuid).setValue(myPost).addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Post saved successfully.")
                                    returnValue.value = true
                                }.addOnFailureListener { exception ->
                                    Log.e(ContentValues.TAG, "Failed to save post: $exception")
                                    // Gérer l'échec de l'enregistrement du message si nécessaire
                                }
                        }
                    }
                }
            }
        } else {
            // Si aucune image n'est présente, enregistrez uniquement le texte
            myRef.child(uuid).setValue(myPost).addOnSuccessListener {
                    Log.d(ContentValues.TAG, "Post saved successfully.")
                    returnValue.value = true
                }.addOnFailureListener { exception ->
                    Log.e(ContentValues.TAG, "Failed to save post: $exception")
                    // Gérer l'échec de l'enregistrement du message si nécessaire
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
    postPost: (String, List<String>?, String, MutableState<Boolean>) -> Unit, user: String
) {
    var descriptionText by remember { mutableStateOf("") }
    val publishSnackbarVisible = remember { mutableStateOf(false) }

    val selectedImages = remember { mutableStateOf<List<String>?>(null) }
    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImages.value = listOf(it.toString())
            }
        }

    val database =
        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")

    Column(
        modifier = Modifier.fillMaxHeight(), // Occupies the entire height of the parent
        verticalArrangement = Arrangement.SpaceBetween // Aligns children vertically with space between them
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
        ) {
            Button(
                onClick = {
                    pickImageLauncher.launch("image/*")
                }, modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
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
                    val imageUrls = selectedImages.value?.map { it.toString() } ?: emptyList()
                    Log.d(
                        "Description Section",
                        "Description: $descriptionText, Images: $imageUrls"
                    )
                    postPost(descriptionText, imageUrls, user, publishSnackbarVisible)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
            ) {
                Text(
                    text = "Publier", color = Color.White
                )
            }
        }
    }

    if (publishSnackbarVisible.value) {
        PublishConfirmationSnackbar(
            context = LocalContext.current, message = "Message publié avec succès"
        )
    }
}


fun openGallery(activity: Activity) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }
    val REQUEST_CODE_PICK_IMAGES = 1
    activity.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGES)
}

//fun publish(description: String, photos: List<Uri>) {
//    // Publie la description dans la base de données Firebase
//    val database = FirebaseDatabase.getInstance()
//    val ref = database.getReference("publications").push()
//    ref.child("description").setValue(description)
//
//    // Publie les photos dans le stockage Firebase
//    photos.forEachIndexed { index, uri ->
//        val storageRef =
//            FirebaseStorage.getInstance().reference.child("images/${ref.key}/photo$index")
//        storageRef.putFile(uri)
//    }
//}

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
