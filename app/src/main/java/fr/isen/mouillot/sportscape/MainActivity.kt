package fr.isen.mouillot.sportscape

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button



class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        // Pour écrire des données
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")

        // Pour lire des données
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value = dataSnapshot.getValue(String::class.java)
                Log.d(TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failed to read value.", error.toException())
            }
        })

        setContent {
            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreenContent { destinationActivity ->
                        // Appeler la fonction navigateFunction avec la classe de l'activité de destination
                        navigateToNextScreen(destinationActivity)
                    }
                }
            }
        }
    }

    private fun navigateToNextScreen(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
        finish()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenContent(navigateFunction: (Class<*>) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top, // Alignement des éléments en haut
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .padding(
                    horizontal = 48.dp,
                    vertical = 24.dp
                ) // Augmente l'espace autour du contour
                .border(1.dp, Color.Black), // Contour noir
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.app_name),
                textAlign = TextAlign.Center,
                fontSize = 24.sp // Taille du texte
            )
        }
        // Espace entre le texte et les boutons
        Spacer(modifier = Modifier.height(600.dp))


        // Column pour les boutons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom // Aligner les éléments en bas de la rangée
        ) {
            ClickableText(
                text = AnnotatedString("Home"),
                onClick = {
                },
                style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp,
                    color = Color.Blue),
                modifier = Modifier
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Espacement autour du texte
            )

            ClickableText(
                text = AnnotatedString("Find"),
                onClick = { navigateFunction(LoginActivity::class.java) },
                style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp,
                    color = Color.Blue
                ),
                modifier = Modifier
                    .clickable {  }
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Espacement autour du texte
            )

            ClickableText(
                text = AnnotatedString("Add"),
                onClick = { /* Action à exécuter lors du clic */ },
                style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp,
                    color = Color.Blue),
                modifier = Modifier
                    .clickable { /* Action à exécuter lors du clic */ }
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Espacement autour du texte
            )

            ClickableText(
                text = AnnotatedString("Map"),
                onClick = { navigateFunction(MapActivity::class.java) },
                style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp,
                    color = Color.Blue),
                modifier = Modifier
                    .clickable { /* Action à exécuter lors du clic */ }
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Espacement autour du texte
            )

            ClickableText(
                text = AnnotatedString("Profile"),
                onClick = { /* Action à exécuter lors du clic */ },
                style = androidx.compose.ui.text.TextStyle(fontSize = 20.sp,
                    color = Color.Blue),
                modifier = Modifier
                    .clickable { /* Action à exécuter lors du clic */ }
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Espacement autour du texte
            )
        }
    }
}

// Déclaration du modèle de données pour une photo
data class Photo(
    val imageUrl: String,
    var likesCount: Int = 0,
    val comments: MutableList<Comment> = mutableListOf()
)

// Déclaration du modèle de données pour un commentaire
data class Comment(
    val userId: String,
    val text: String
)