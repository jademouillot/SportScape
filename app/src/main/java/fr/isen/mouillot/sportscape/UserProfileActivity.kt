package fr.isen.mouillot.sportscape

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import fr.isen.mouillot.sportscape.model.User
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme


class UserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var currentUsername = mutableStateOf("")

        val authEmail = FirebaseAuth.getInstance().currentUser?.email
        if (authEmail != null) {
            Log.d("EMAIL", "Email: $authEmail")
            GetUserfromEmail(authEmail, currentUsername)
        }

        val uidUser = FirebaseAuth.getInstance().currentUser?.uid
        if (uidUser != null) {
            Log.d("UID", "Uid: $uidUser")
            //GetUserfromEmail(authEmail, currentUsername)
        }

        var biography = mutableStateOf("")
        getBiography(uidUser ?: "", currentUsername, authEmail ?: "", biography)

        var photoUrl = mutableStateOf("")
        getphotoUrl(uidUser ?: "", currentUsername, authEmail ?: "", photoUrl)

        setContent {
            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                    //color = MaterialTheme.colorScheme.background
                ) {
                    //PrintInfos("Max Verstappen", ::navigateToModifyActivity)
                    //LaunchedEffect(currentUsername.value) {
                    PrintInfos(
                        currentUsername.value,
                        ::navigateToModifyActivity,
                        authEmail ?: "",
                        biography,
                        photoUrl
                    )
                    //Text(text = currentUsername.value)
                    //}
                    ActionBarUserProfile(navigateFunction = ::startActivity)
                }
                postdata("John Doe", 30, "Passionate about sports and outdoor activities.")
            }
        }

    }

    fun getBiography(
        uid: String,
        username: MutableState<String>,
        email: String,
        biography: MutableState<String>
    ) {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val userRef = database.getReference("user").child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    biography.value = user.biographie
                    Log.d("UserBiography", "Biography: $biography")
                    // Utilisez la variable biography comme vous le souhaitez
                } else {
                    Log.d("UserBiography", "User not found.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserBiography", "Database error: ${databaseError.message}")
            }
        })
    }

    fun getphotoUrl(
        uid: String,
        username: MutableState<String>,
        email: String,
        photoUrl: MutableState<String>
    ) {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val userRef = database.getReference("user").child(uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    photoUrl.value = user.photoUrl
                    Log.d("UserPhotoUrl", "PhotoUrl: $photoUrl")
                    // Utilisez la variable biography comme vous le souhaitez
                } else {
                    Log.d("UserPhotoUrl", "User not found.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("UserPhotoUrl", "Database error: ${databaseError.message}")
            }
        })
    }

    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun GetUserfromEmail(email: String, returnUsername: MutableState<String>) {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("user") // Change "users" to "tmp"
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    Log.d("EMAIL- ici", "Email: $email")
//                    Log.d("EMAIL", "User: $user")
                    if (user != null) {
                        if (user.email == email) {
//                            val username = userSnapshot.key // Retrieve the user's ID
                            returnUsername.value = user.username
                            Log.d("EMAIL - GOOD", "User: $user")
//                            Log.d("EMAIL - GOOD", "User: $user")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(FirebaseRemoteConfig.TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun navigateToModifyActivity(email: String) {
        val intent = Intent(this, ModifyActivity::class.java)
        intent.putExtra("Email", email)
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
    fun PrintInfos(
        username: String,
        navigateToModifyActivity: (String) -> Unit,
        email: String,
        biography: MutableState<String>,
        photoUrl: MutableState<String>
    ) {
        //val imageUrl =
        //remember { mutableStateOf("https://firebasestorage.googleapis.com/v0/b/sportscape-38027.appspot.com/o/Max_Verstappen_(36132602681).jpg?alt=media&token=bfdbd485-ef9c-480e-8ce4-db51defa7bc4") }
        val userBio =
            remember { mutableStateOf("Chargement de la biographie...") } // Initialiser avec un texte de chargement

        // Replace "YOUR_PATH_HERE" with the actual path where the images are stored in Firebase Storage
        // For example, if your images are stored in a directory called "profile_images" in Firebase Storage
        // and the image name is the same as the username with ".jpg" extension, it would look like this: "profile_images/$username.jpg"
        val storageReference =
            FirebaseStorage.getInstance().reference.child("Max_Verstappen_(36132602681).jpg")
        // Supposer que nous avons une référence à la base de données pour récupérer la bio
        val bioReference = FirebaseDatabase.getInstance().getReference("path_to_user_bio")

        LaunchedEffect(username) {
            // Fetch the download URL for the image
            //storageReference.downloadUrl.addOnSuccessListener { uri ->
            //  imageUrl.value = uri.toString()
            //}.addOnFailureListener {
            // Handle any errors
            //}
            // Supposons que vous avez une fonction pour récupérer la bio depuis Firebase
            bioReference.child(username).get().addOnSuccessListener { dataSnapshot ->
                userBio.value =
                    dataSnapshot.getValue(String::class.java) ?: "Aucune biographie disponible."
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
                if (photoUrl.value.isNotEmpty()) {
                    /*val painter: Painter = rememberImagePainter(imageUrl)

                    CoilImage(
                        painter = painter,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )*/



                    Image(
                        painter = rememberAsyncImagePainter(photoUrl.value),
                        contentDescription = null, // Fournir une description pour l'accessibilité
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        //contentScale = ContentScale.Crop // Ajustez selon vos besoins
                    )


                }
                // You can continue with the rest of your UI here...

                //Text(text = photoUrl.value)
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

                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = null, // Fournir une description pour l'accessibilité
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    //contentScale = ContentScale.Crop // Ajustez selon vos besoins
                )

            }
            // Espacer avant la bio
            Spacer(modifier = Modifier.height(16.dp))

            // Affichage de la bio
            Text(
                text = biography.value,
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
                        .clickable { navigateToModifyActivity(email) }
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp),
                    fontSize = 16.sp,
                    color = Color.Gray
                )

                val context = LocalContext.current

                Text(
                    "Follow",
                    modifier = Modifier
                        // Assuming adding logic for "Follow" will be done here later
                        .clickable {
                            Toast.makeText(context, "Follow well received", Toast.LENGTH_SHORT).show()

                        }
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(8.dp),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Divider
            Divider(color = Color.LightGray, thickness = 1.dp)

        }
    }

}

@Composable
fun ActionBarUserProfile(navigateFunction: (Class<*>) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            onClick = {
                navigateFunction(MainActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Home",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                navigateFunction(FindActivity::class.java)
            },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.find),
                contentDescription = "Find",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                navigateFunction(NewPublicationActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.add),
                contentDescription = "Add",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                navigateFunction(MapActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.map),
                contentDescription = "Map",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                navigateFunction(UserProfileActivity::class.java)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


