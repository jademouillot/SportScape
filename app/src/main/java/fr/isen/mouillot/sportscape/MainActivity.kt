package fr.isen.mouillot.sportscape

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG
import fr.isen.mouillot.sportscape.model.Post
import fr.isen.mouillot.sportscape.model.User
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction3

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var currentUsername = mutableStateOf("")

// Dans l'activité de destination
        val extras = intent.extras
        if (extras != null) {
            val email = extras.getString("Email") // Remplacez "UserUuid" par votre propre clé
            if (email != null) {
                GetUserfromEmail(email, currentUsername)
            }
        }

        val authEmail = FirebaseAuth.getInstance().currentUser?.email
        if (authEmail != null) {
            Log.d("EMAIL", "Email: $authEmail")
            GetUserfromEmail(authEmail, currentUsername)
        }

        FirebaseApp.initializeApp(this)


        setContent {

            SportScapeTheme {
                val allPosts = remember { mutableStateOf<List<Post>>(emptyList()) }
                val userData = remember { mutableStateOf(User()) }


                displayData(allPosts, userData)



                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    MainScreenContent(
                        this,
                        ::startActivity,
                        ::signOut,
                        currentUsername.value,
                        allPosts,
                        ::GetUserfromEmail,
                        ::addLike,
                        ::addComment
                    )
                }
            }
        }
    }

    private fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    private fun displayData(allPosts: MutableState<List<Post>>, userData: MutableState<User>?) {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val postsRef = database.getReference("posts").orderByChild("date")
        val posts = mutableListOf<Post>()

        postsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    if (post != null) {
                        Log.d("POST", "Post: $post")

                        post.let { posts.add(it) }
                    }
                }
                allPosts.value = posts.reversed()

                // Add a listener for each post to update likes in real time
                for (post in allPosts.value) {
                    val likesRef = database.getReference("posts/${post.id}/likes")
                    likesRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val likes = snapshot.getValue(Int::class.java) ?: 0
                            post.likes = likes
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.w(TAG, "Failed to read likes.", error.toException())
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })
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
                Log.d(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    fun addLike(postId: String, like: Int) { // Update this line
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("posts/$postId/likes")
        myRef.setValue(like)
        Log.d("LIKE", "Like added to post $postId")


    }

    fun addComment(postId: String, comment: String, username: String) { // Add username parameter
    val database =
        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val commentsRef = database.getReference("posts/$postId/comments")

    // Récupérer les commentaires existants
    commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val comments =
                dataSnapshot.getValue<List<Map<String, String>>>() ?: emptyList() // Change to Map
            val newComments = comments.toMutableList()
            newComments.add(mapOf("username" to username, "comment" to comment)) // Add username and comment as a map

            // Mettre à jour les commentaires
            commentsRef.setValue(newComments)
            Log.d("COMMENT", "Comment added to post $postId")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            Log.w("COMMENT", "Failed to read comments.", databaseError.toException())
        }
    })
}


}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreenContent(
    context: Context,
    startActivity: (Class<*>) -> Unit,
    signOut: () -> Unit,
    username: String,
    posts: MutableState<List<Post>>,
    getUserfromuuid: (String, MutableState<String>) -> Unit,
    addLike: (String, Int) -> Unit,
    addComment: KFunction3<String, String, String, Unit>
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 48.dp, vertical = 24.dp)
                .border(1.dp, Color.Black), contentAlignment = Alignment.Center
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Welcome to SportScape, $username!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold

            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.LightGray,
            modifier = Modifier.padding(8.dp)
        ) {
            // List of Posts
            LazyColumn {

                items(posts.value) { post ->
                    val userName = remember { mutableStateOf("") }
                    var currentComment by remember { mutableStateOf("") } // Add this line

//                    val imageUri = remember { mutableStateOf("") }
//                    if (post.images != null && post.images.isNotEmpty()) {
//                        imageUri.value = post.images[0]
//                    }
                    if (post.images != null) {
                        for (image in post.images) {
                            val imagePainter = rememberImagePainter(data = image)
                            Image(
                                painter = imagePainter,
                                contentDescription = "Post image",
                                modifier = Modifier
                                    .height(200.dp)
                                    .fillMaxWidth()
                            )
                        }

                    }



                    getUserfromuuid(post.userEmail, userName)




                        if (userName.value.isNotEmpty()) { // Check if userName is not empty

                            Box(modifier = Modifier.clickable {
                                // Create an Intent to start DetailPostActivity
                                val intent = Intent(context, DetailPostActivity::class.java)
                                // Pass the post ID or any other data to DetailPostActivity
                                intent.putExtra("postId", post.id)
                                // Start DetailPostActivity

                                startActivity(DetailPostActivity::class.java)
                            }) {
                                Text(
                                text = "Post by: ${userName.value}: \nDescription : ${post.description}\nPosted ${
                                    getTimeAgo(
                                        post.date
                                    )
                                }", modifier = Modifier.padding(8.dp)
                            )
                        }

                        Row {
                            IconButton(onClick = {
                                addLike(post.id, post.likes + 1) // Update this line
                            }) { // Update likes properly
                                Icon(
                                    Icons.Filled.Favorite,
                                    contentDescription = "like",
                                    tint = Color.Red
                                )
                            }
                            Text(text = "${post.likes} likes")

                            IconButton(onClick = {
                                addComment(
                                    post.id,
                                    currentComment,
                                    username
                                )
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.comment),
                                    contentDescription = "Comment",
                                    tint = Color.Blue
                                )
                            }
                            Text(text = currentComment) // Add this line
                        }
                        Text(text = "Comments:")
                        post.comments.forEach { comment ->
                            Text(text = "${comment.username} replied: ${comment.comment}")
                        }



                        CommentInput(post.id) { postId, comment ->
                            addComment(
                                postId,
                                comment,
                                username
                            )
                        }
                    }
                }
            }
        }
    }
    LogoutButton(context, signOut, startActivity)
    ActionBar(context, startActivity)
}


@Composable
fun LogoutButton(context: Context, signOut: () -> Unit, startActivity: (Class<*>) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, end = 10.dp)
    ) {
        IconButton(onClick = {
            signOut()
            startActivity(LoginActivity::class.java)
        }) {
            Icon(
                painter = painterResource(id = R.drawable.logout),
                contentDescription = "Logout",
                tint = Color.Blue,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}


@Composable
fun CommentInput(postId: String, addComment: (String, String) -> Unit) {
    var commentText by remember { mutableStateOf("") }

    Row {
        TextField(value = commentText,
            onValueChange = { commentText = it },
            label = { Text("Add a comment") })
        Button(onClick = {
            addComment(postId, commentText)
            commentText = ""
        }) {
            Text("Submit")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun getTimeAgo(time: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - time

    return when {
        diff < TimeUnit.MINUTES.toMillis(1) -> "Il y a quelques secondes"
        diff < TimeUnit.HOURS.toMillis(1) -> "Il y a ${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes"
        diff < TimeUnit.DAYS.toMillis(1) -> "Il y a ${TimeUnit.MILLISECONDS.toHours(diff)} heures"
        diff < TimeUnit.DAYS.toMillis(2) -> "Hier"
        else -> "il y a ${TimeUnit.MILLISECONDS.toDays(diff)} jours"
    }
}

@Composable
fun ActionBar(context: Context, navigateFunction: (Class<*>) -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }, modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.home),
                contentDescription = "Home",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(onClick = {},
            modifier = Modifier
                .clickable { /*ajouter direction*/ }
                .padding(horizontal = 10.dp, vertical = 12.dp)) {
            Icon(
                painter = painterResource(id = R.drawable.find),
                contentDescription = "Find",
                tint = Color.Blue,
                modifier = Modifier.size(24.dp)
            )
        }

        IconButton(
            onClick = {
                val intent = Intent(context, NewPublicationActivity::class.java)
                context.startActivity(intent)
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
                val intent = Intent(context, MapActivity::class.java)
                context.startActivity(intent)
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
                val intent = Intent(context, ProfileActivity::class.java)
                context.startActivity(intent)
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

