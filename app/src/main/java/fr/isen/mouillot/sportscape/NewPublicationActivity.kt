package fr.isen.mouillot.sportscape

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import fr.isen.mouillot.sportscape.model.Post
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import java.util.Date
import java.util.UUID

class NewPublicationActivity : ComponentActivity() {
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
    var descriptionText by remember { mutableStateOf("") }
//    val database =
//        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val context = LocalContext.current // Obtenir le contexte de l'activité parente

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
        }
        // Bouton "Publier" en bas de l'écran
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
    }
}

