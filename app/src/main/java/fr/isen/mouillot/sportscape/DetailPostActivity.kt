package fr.isen.mouillot.sportscape

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import fr.isen.mouillot.sportscape.model.Post
import fr.isen.mouillot.sportscape.model.User
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme

class DetailPostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val postId = intent.getStringExtra("postId")
        var currentUsername = mutableStateOf("")

        val extras = intent.extras
        if (extras != null) {
            val email = extras.getString("Email")
            if (email != null) {
                //GetUserfromEmail(email, currentUsername)
            }
        }

        setContent {
            SportScapeTheme {
                val allPosts = remember { mutableStateOf<List<Post>>(emptyList()) }
                val userData = remember { mutableStateOf(User()) }


                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    DetailPostScreen(postId ?: "", currentUsername.value)
                    //displayData(allPosts, userData)
                }
            }
        }
        fun GetUserfromEmail(email: String, returnUsername: MutableState<String>) {
            val database =
                FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
            val myRef = database.getReference("users") // Change "users" to "tmp"
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val user = userSnapshot.getValue(User::class.java)
//                    Log.d("EMAIL", "User: $user")
                        if (user != null) {
                            if (user.email == email) {
//                            val username = userSnapshot.key // Retrieve the user's ID
                                returnUsername.value = user.username
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


         fun displayData(allPosts: MutableState<List<Post>>, userData: MutableState<User>?) {
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
                                Log.w(
                                    FirebaseRemoteConfig.TAG,
                                    "Failed to read likes.",
                                    error.toException()
                                )
                            }
                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(FirebaseRemoteConfig.TAG, "Failed to read value.", error.toException())
                }
            })
        }

    }
}

@Composable
fun DetailPostScreen(
    postId: String, currentUsername: String
) {
    val post = remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(postId) {
        //post.value = getPostDetails(postId)
    }

    Column {
        post.value?.let {
            Text(text = "Post by: ${it.userEmail}")
            Text(text = "Description: ${it.description}")
            Text(text = "Likes: ${it.likes}")
            // Display other post details here
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )

}