package fr.isen.mouillot.sportscape

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme

private const val REQUEST_CODE_PICK_IMAGES = 123
class NewPublicationActivity : ComponentActivity() {
    private var selectedPhotos: MutableList<Uri> = mutableListOf() // Déclarer une variable globale pour stocker les photos sélectionnées

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportScapeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        TopBar(title = "Nouvelle Publication")

                        DescriptionSection { description, user ->
                            postPost(description, user)
                        }

                        DescriptionSection(selectedPhotos)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGES && resultCode == RESULT_OK) {
            // Récupère les Uri des photos sélectionnées depuis l'intention
            val clipData = data?.clipData
            if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    selectedPhotos.add(uri) // Mettre à jour la variable globale avec les photos sélectionnées
                }
            } else {
                val uri = data?.data
                if (uri != null) {
                    selectedPhotos.add(uri) // Mettre à jour la variable globale avec les photos sélectionnées
                }
            }
        }
    }
}



   private fun postPost(description: String, user: String) {
    val database =
        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRef = database.getReference("posts")
    val myPost = Post(description = description, userId = user, date = Date().time) // Corrected the fields

    val uuid = UUID.randomUUID().toString()
    myRef.child(uuid).setValue(myPost)
    Log.d(ContentValues.TAG, "Post saved successfully.")
}



}


@Composable
fun TopBar(title: String) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(), color = Color(51, 104, 188)
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
fun DescriptionSection(postPost: (String, String) -> Unit) {
fun DescriptionSection(selectedPhotos: List<Uri>) {
    var descriptionText by remember { mutableStateOf("") }
//    val database =
//        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val database =
        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val context = LocalContext.current // Obtenir le contexte de l'activité parente

    // État pour stocker les photos sélectionnées
    var selectedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                        .padding(8.dp)
                ) {
                    BasicTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (descriptionText.isEmpty()) {
                        Text(
                            text = "Entrez votre texte ici",
                            color = Color.Gray,
                            fontSize = 16.sp,
                            modifier = Modifier.align(Alignment.CenterStart)
                        )
                    }
                }
            }

            // Bouton "Ouvrir la galerie"
            Button(
                onClick = {
                    openGallery(activity = context as Activity)
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Ouvrir la galerie")
            }

            // Affiche les photos sélectionnées
            selectedPhotos.forEach { photo ->
                Image(
                    painter = rememberImagePainter(photo),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        // Bouton "Publier"
        val publishSnackbarVisible = remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            color = Color(51, 104, 188),
            shape = RoundedCornerShape(8.dp)
        ) {
            Box(
                modifier = Modifier.clickable {
//                    val myRefToWrite =
//                        database.getReference("RegisterUser").push().setValue(descriptionText)
                    postPost(descriptionText, "Georgia")


                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                }, contentAlignment = Alignment.Center
                    if (descriptionText.isNotEmpty()) {
                        // Publication de la description dans la base de données Firebase
                        val ref = database.getReference("publications").push()
                        ref.child("description").setValue(descriptionText)

                        // Publier les photos dans le stockage Firebase
                        selectedPhotos.forEachIndexed { index, uri ->
                            val storageRef = FirebaseStorage.getInstance().reference.child("images/${ref.key}/photo$index")
                            val uploadTask = storageRef.putFile(uri)

                            // Gérer le succès ou l'échec du téléchargement de chaque photo
                            uploadTask.addOnSuccessListener { taskSnapshot ->
                                // Photo téléchargée avec succès
                                // Vous pouvez ajouter ici des actions supplémentaires si nécessaire
                            }.addOnFailureListener { exception ->
                                // Erreur lors du téléchargement de la photo
                                Log.e("FirebaseStorage", "Erreur lors du téléchargement de la photo: ${exception.message}")
                            }
                        }

                        publishSnackbarVisible.value = true
                    }
                },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Publier",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)

                )
//                AddPostSection()
            }
        }

        if (publishSnackbarVisible.value) {
            PublishConfirmationSnackbar(context = context, message = "Message publié avec succès")
        }
    }
}

fun openGallery(activity: Activity) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }
    activity.startActivityForResult(intent, REQUEST_CODE_PICK_IMAGES)
}

fun publish(description: String, photos: List<Uri>) {
    // Publie la description dans la base de données Firebase
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("publications").push()
    ref.child("description").setValue(description)

    // Publie les photos dans le stockage Firebase
    photos.forEachIndexed { index, uri ->
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${ref.key}/photo$index")
        storageRef.putFile(uri)
    }
}

@Composable
fun PublishConfirmationSnackbar(
    context: Context,
    message: String,
    duration: Int = 1000
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
