package fr.isen.mouillot.sportscape

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.mouillot.sportscape.model.Post
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DetailPostActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val postId = intent.getStringExtra("postId")

        setContent {
            SportScapeTheme {
                DetailPostScreen(postId ?: "")
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun DetailPostScreen(postId: String) {
    val post = remember { mutableStateOf<Post?>(null) }

    LaunchedEffect(postId) {
        post.value = getPostDetails(postId)
    }

    post.value?.let {
        Text(text = "Post by: ${it.userEmail}")
        Text(text = "Description: ${it.description}")
        Text(text = "Likes: ${it.likes}")
        // Affichez d'autres détails du post ici
    }
}

suspend fun getPostDetails(postId: String): Post? {
    // Get an instance of the Firebase Database
    val database = FirebaseDatabase.getInstance()

    // Create a reference to the location in the database where the post details are stored
    val postRef = database.getReference("posts/$postId")

    // Use a coroutine to wait for the data to be fetched
    return withContext(Dispatchers.IO) {
        val snapshot = suspendCoroutine<DataSnapshot> { continuation ->
            // Attach a listener to the reference to retrieve the data
            postRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Pass the data to the continuation when it's fetched
                    continuation.resume(dataSnapshot)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Pass an exception to the continuation if the fetch is cancelled
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }

        // Convert the snapshot to a Post object and return it
        snapshot.getValue(Post::class.java)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
