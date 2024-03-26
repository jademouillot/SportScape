package fr.isen.mouillot.sportscape


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import com.google.firebase.storage.FirebaseStorage




class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PrintInfos("Max Verstappen")
                }
            }
        }
    }
}



@Composable
fun PrintInfos(category: String) {

    Column {
        Text(
            text = "Max_Verstappen ",
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            //fontStyle = FontStyle.,
            color = Color(0xFF000000),
            modifier = Modifier
        )
        Row {
            Box(
                modifier = Modifier
                    .size(150.dp) // Taille de l'image, ajustez selon vos besoins
                    //.border(2.dp, Color.Black)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.max_verstappen),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )
                   }

            /*Column {
                Text(
                    text = "Max_Verstappen ",
                    textAlign = TextAlign.Start,
                    fontSize = 24.sp,
                    //fontStyle = FontStyle.,
                    color = Color(0xFF000000),
                    modifier = Modifier
                )
            }*/
            Box(modifier = Modifier.padding(top = 100.dp)) {
                Column {
                    Text(
                        text = "posts ",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        //fontStyle = FontStyle.,
                        color = Color(0xFF000000),
                        modifier = Modifier
                    )
                }
            }
            Box(modifier = Modifier.padding(top = 100.dp)) {
                Column {
                    Text(
                        text = "followers ",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        //fontStyle = FontStyle.,
                        color = Color(0xFF000000),
                        modifier = Modifier
                    )
                }
            }
            Box(modifier = Modifier.padding(top = 100.dp)) {
                Column {
                    Text(
                        text = "followings ",
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        //fontStyle = FontStyle.,
                        color = Color(0xFF000000),
                        modifier = Modifier
                    )

                }
            }


        }
        //CoilImage(imageUrl = dish.imageUrl, modifier = Modifier.size(50.dp))
    }
}



// Supposons que vous passiez l'username comme argument pour récupérer l'image correspondante
/*@Composable
fun PrintInfos(username: String) {
    val storageReference = FirebaseStorage.getInstance().reference.child("profile_images/$username.jpg")

    // Obtenir l'URL de l'image
    var imageUrl = ""
    storageReference.downloadUrl.addOnSuccessListener { uri ->
        imageUrl = uri.toString()
    }

    Column {
        Text(
            text = username,
            textAlign = TextAlign.Start,
            fontSize = 24.sp,
            color = Color(0xFF000000),
            modifier = Modifier
        )
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Afficher l'image dans un cercle
            AsyncImage(
                model = imageUrl,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape), // Cela donne la forme circulaire à l'image
                contentScale = ContentScale.Crop
            )
        }

        // Votre code existant pour afficher "posts", "followers", "following"
    }
}

//}

//}
*/