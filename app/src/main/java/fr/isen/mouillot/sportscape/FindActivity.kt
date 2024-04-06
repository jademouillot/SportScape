package fr.isen.mouillot.sportscape

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme

class FindActivity : ComponentActivity() {
    private val database = FirebaseDatabase.getInstance().reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportScapeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    UserSearch(database)
                    ActionBarFind(navigateFunction = ::startActivity)
                }
            }
        }
    }
    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}

@Composable
fun UserSearch(database: DatabaseReference) {
    var searchText by remember { mutableStateOf("") }
    var user: User? by remember { mutableStateOf(null) }

    Column {
        // Champ de texte pour la recherche
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search for users") },
            modifier = Modifier.padding(16.dp)
        )

        // Affiche l'utilisateur trouvé si existant
        user?.let { foundUser ->
            Text(text = "Utilisateur trouvé : ${foundUser.username}", modifier = Modifier.padding(16.dp))
        }

        // Observer les changements dans la base de données
        LaunchedEffect(searchText) {
            if (searchText.isNotBlank()) {
                searchUserInDatabase(searchText, database) { foundUser ->
                    user = foundUser
                }
            }
        }
    }
}

fun searchUserInDatabase(query: String, database: DatabaseReference, callback: (User?) -> Unit) {
    val queryRef = database.child("user").orderByChild("username").equalTo(query)

    queryRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val userSnapshot = snapshot.children.firstOrNull()
            val user = userSnapshot?.getValue(User::class.java)
            callback(user)
        }

        override fun onCancelled(error: DatabaseError) {
            // Gérer les erreurs
            callback(null)
        }
    })
}

@Composable
fun ActionBarFind(navigateFunction: (Class<*>) -> Unit) {
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

        IconButton(onClick = {},
            modifier = Modifier
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