package fr.isen.mouillot.sportscape

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
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
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                    //color = MaterialTheme.colorScheme.background
                ) {
                    PrintInfos("Max Verstappen", ::navigateToModifyActivity)
                }
                postdata("John Doe", 30, "Passionate about sports and outdoor activities.")
            }
        }

    }
    fun navigateToModifyActivity() {
        val intent = Intent(this, ModifyActivity::class.java)
        startActivity(intent)
    }
    private fun postdata(name: String, age: Int, bio: String) {
        val database =
            FirebaseDatabase.getInstance()
        val myRefToWrite = database.getReference("RegisterUser")

        val userBio = UserBio(name = name, age = age, bio = bio)

        myRefToWrite.push().setValue(userBio)
    }

    data class UserBio(
        val name: String,
        val age: Int,
        val bio: String
    )

    @Composable
    fun PrintInfos(username: String, navigateToModifyActivity: () -> Unit) {
        val imageUrl = remember { mutableStateOf("") }
        val userBio = remember { mutableStateOf("Chargement de la biographie...") } // Initialiser avec un texte de chargement

        // Replace "YOUR_PATH_HERE" with the actual path where the images are stored in Firebase Storage
        // For example, if your images are stored in a directory called "profile_images" in Firebase Storage
        // and the image name is the same as the username with ".jpg" extension, it would look like this: "profile_images/$username.jpg"
        val storageReference =
            FirebaseStorage.getInstance().reference.child("Max_Verstappen_(36132602681).jpg")
        // Supposer que nous avons une référence à la base de données pour récupérer la bio
        val bioReference = FirebaseDatabase.getInstance().getReference("path_to_user_bio")

        LaunchedEffect(username) {
            // Fetch the download URL for the image
            storageReference.downloadUrl.addOnSuccessListener { uri ->
                imageUrl.value = uri.toString()
            }.addOnFailureListener {
                // Handle any errors
            }
            // Supposons que vous avez une fonction pour récupérer la bio depuis Firebase
            bioReference.child(username).get().addOnSuccessListener { dataSnapshot ->
                userBio.value = dataSnapshot.getValue(String::class.java) ?: "Aucune biographie disponible."
            }.addOnFailureListener {
                userBio.value = "Erreur lors du chargement de la biographie."
            }
        }

        Column {
            Text(
                text = username,
                textAlign = TextAlign.Start,
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
            )
            Row {
    // Use AsyncImage to load the image from the URL
    if (imageUrl.value.isNotEmpty()) {
        AsyncImage(
            model = imageUrl.value,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
                // You can continue with the rest of your UI here...


                // Spacer for some space between the image and the texts
                Spacer(modifier = Modifier.width(16.dp))

                // Posts Column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(50.dp)) // Add more space here
                    Text("123", fontSize = 16.sp, color = Color.Black) // Posts count
                    Text("Posts", fontSize = 14.sp, color = Color.Gray) // Label
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Followers Column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(50.dp)) // Add more space here
                    Text("456", fontSize = 16.sp, color = Color.Black) // Followers count
                    Text("Followers", fontSize = 14.sp, color = Color.Gray) // Label
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Following Column
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(50.dp)) // Add more space here
                    Text("789", fontSize = 16.sp, color = Color.Black) // Following count
                    Text("Following", fontSize = 14.sp, color = Color.Gray) // Label
                }

            }
            // Espacer avant la bio
            Spacer(modifier = Modifier.height(16.dp))

            // Affichage de la bio
            Text(
                text = userBio.value,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    "Modify",
                    modifier = Modifier
                        .clickable { navigateToModifyActivity() }
                        .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                Text(
                    "Follow",
                    modifier = Modifier
                        // Assuming adding logic for "Follow" will be done here later
                        .clickable { /* Follow logic goes here */ }
                        .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(4.dp))
                        .padding(8.dp),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Divider(color = Color.LightGray, thickness = 1.dp)

            }
    @Composable
    fun AsyncImage(
        model: String,
        contentDescription: String,
        modifier: Modifier = Modifier,
        contentScale: ContentScale = ContentScale.Fit
    ) {
        val painter: Painter = rememberImagePainter(data = model)
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale
        )
    }
}