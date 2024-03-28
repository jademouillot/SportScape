package fr.isen.mouillot.sportscape

import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG
import fr.isen.mouillot.sportscape.model.Post
import fr.isen.mouillot.sportscape.model.User
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import java.util.concurrent.TimeUnit

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

        FirebaseApp.initializeApp(this)


        setContent {

            SportScapeTheme {
                val allPosts = remember { mutableStateOf(listOf<Post>()) }
                val userData = remember { mutableStateOf(User()) }

                LaunchedEffect(key1 = Unit) {
                    displayData(allPosts, userData)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    MainScreenContent(
                        this,
                        ::startActivity,
                        ::signOut,
                        userData.value.username,
                        allPosts.value,
                        ::GetUserfromEmail
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

                        post.let { posts.add(it) }
                    }
                }
                allPosts.value = posts.reversed()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

    }

    fun GetUserfromEmail(email: String, returnUsername: MutableState<String>) {
        val database =
            FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("tmp") // Change "users" to "tmp"
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (userSnapshot in dataSnapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    Log.d("EMAIL", "User: $user")
                    if (user != null) {
                        if (user.email == email) {
//                            val username = userSnapshot.key // Retrieve the user's ID
                            returnUsername.value = user.username
                            Log.d("EMAIL - GOOD", "User: $user")
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failed to read value.", error.toException())
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
    posts: List<Post>,
    getUserfromuuid: (String, MutableState<String>) -> Unit
) {
    Card {
        LazyColumn(
            modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 48.dp, vertical = 24.dp)
                        .border(1.dp, Color.Black), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        textAlign = TextAlign.Center,
                        fontSize = 24.sp
                    )
                }
            }

            item {
                Text(
                    text = "Recently:", modifier = Modifier.padding(16.dp)
                )
            }

            items(posts) { post ->
                val userName = remember { mutableStateOf("") }
                getUserfromuuid(post.userId, userName)
                if (userName.value != "") {
                    Text(
                        text = "Post by ${userName.value}: \nDescription : ${post.description}\nPosted ${getTimeAgo(post.date)}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp, end = 10.dp)
        ) {
            IconButton(
                onClick = {
                    signOut()
                    startActivity(LoginActivity::class.java)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Logout",
                    tint = Color.Blue,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
    ActionBar(context, startActivity)
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

