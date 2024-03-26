package fr.isen.mouillot.sportscape


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import com.google.firebase.storage.FirebaseStorage
import android.content.Intent
import androidx.compose.foundation.clickable
//import fr.isen.mouillot.sportscape.ModifyActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.google.firebase.database.FirebaseDatabase


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
                    /*val intent = Intent(this@ProfileActivity, ModifyActivity::class.java)
                    startActivity(intent)*/
                }
                postdata("John Doe", 30, "Passionate about sports and outdoor activities.")
            }
        }
    }

    private fun postdata(name: String, age: Int, bio: String) {
        val database =
            FirebaseDatabase.getInstance("https://console.firebase.google.com/u/0/project/sportscape-38027/database/sportscape-38027-default-rtdb/data/~2F")
        val myRefToWrite = database.getReference("RegisterUser")

        val userBio = UserBio(name = name, age = age, bio = bio)

        myRefToWrite.push().setValue(userBio)
    }

    data class UserBio(
        val name: String,
        val age: Int,
        val bio: String
    )


    // Supposons que vous passiez l'username comme argument pour récupérer l'image correspondante
    @Composable
    fun PrintInfos(username: String) {
        /*val storageReference =
            FirebaseStorage.getInstance().reference.child("profile_images/$username.jpg")

        // Obtenir l'URL de l'image
        var imageUrl = ""
        storageReference.downloadUrl.addOnSuccessListener { uri ->
            imageUrl = uri.toString()
        }*/
        val firestore = FirebaseFirestore.getInstance()
        var username = remember { mutableStateOf("") }
        var bio = remember { mutableStateOf("") }
        var imageUrl = remember { mutableStateOf("") }
        val userId = ""

        LaunchedEffect(userId) {
            firestore.collection("Users").document(userId).get().addOnSuccessListener { document ->
                //username = document.getString("username") ?: ""
                //bio = document.getString("bio") ?: ""
                //imageUrl = document.getString("imageUrl") ?: "" // Assurez-vous d'avoir une URL d'image
            }
        }


        Column {
            Text(
                text = username.value,
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
                // Votre code existant pour afficher "posts", "followers", "following"
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
            Text(
                text = bio.value,
                textAlign = TextAlign.Start,
                fontSize = 16.sp,
                color = Color(0xFF000000),
                modifier = Modifier.padding(top = 8.dp)
            )
            /*Text(
                text = "Modifier",
                modifier = modifier
                    .clickable { navigateToModifyActivity() }
                    .padding(16.dp), // Ajoutez un padding pour un meilleur aspect et facilité de clic
                color = Color.Blue // Couleur du texte pour le distinguer comme cliquable
            )*/
        }

    }
}


/*@Composable
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
}*/
