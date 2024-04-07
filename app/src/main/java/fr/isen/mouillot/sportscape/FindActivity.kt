package fr.isen.mouillot.sportscape

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import kotlin.reflect.KFunction2


class FindActivity : ComponentActivity() {
    //private val database = FirebaseDatabase.getInstance().reference
    // Créer une référence à la base de données Firebase

    val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val userRef = database.getReference("user")

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var key = ""

        setContent {
            SportScapeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    //UserSearch(database)

                    var usernames by remember { mutableStateOf(emptyList<String>()) }
                    //var userUIDs by remember { mutableStateOf<List<String>>(emptyList()) }

                    // Appel de fetchUsernames avec une fonction de complétion
                    fetchUsernames { fetchedUsernames ->
                        usernames = fetchedUsernames
                    }
/*
                    LazyColumn {
                        items(usernames) { username ->
                            Text(text = username, modifier = Modifier.padding(8.dp))
                        }
                    }
*/
                    // Utilisation de la liste usernames récupérée
                    UserSearchBar(usernames, startActivityUsername = ::startActivity_username, getUidByUsername = ::getUidByUsername)
/*
                    val username = "jadetestkarin"

// Ajouter un auditeur pour récupérer les données des utilisateurs
                    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val uids = mutableListOf<String>()
                            // Parcourir les données pour extraire les UID des utilisateurs
                            for (userSnapshot in dataSnapshot.children) {
                                val uid = userSnapshot.key
                                uid?.let {
                                    uids.add(it)
                                }
                            }
                            // Mettre à jour la liste des UID dans l'état de Compose
                            userUIDs = uids
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Gérer les erreurs d'annulation
                        }
                    })

                    LazyColumn {
                        items(userUIDs) { uid ->
                            Text(text = uid, modifier = Modifier.padding(8.dp))
                        }
                    }
*/
                    val usernameToFind = "jadetestkarin"
                    var uidToDisplay by remember { mutableStateOf<String?>(null) }

// Ajouter un auditeur pour récupérer les données des utilisateurs
                    userRef.orderByChild("username").equalTo(usernameToFind)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                // Vérifier si des données ont été trouvées pour le nom d'utilisateur spécifié
                                if (dataSnapshot.exists()) {
                                    // Récupérer l'UID du premier utilisateur trouvé (supposant qu'il n'y a qu'un seul utilisateur avec ce nom d'utilisateur)
                                    val uid = dataSnapshot.children.firstOrNull()?.key
                                    // Mettre à jour l'UID à afficher
                                    uidToDisplay = uid
                                } else {
                                    // Aucun utilisateur trouvé avec ce nom d'utilisateur
                                    // Gérer le cas où aucun utilisateur n'est trouvé
                                    uidToDisplay = null
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Gérer les erreurs d'annulation
                                // Par exemple, afficher un message d'erreur à l'utilisateur
                                // ou enregistrer l'erreur dans les journaux de l'application
                            }
                        })

// Afficher l'UID dans l'interface utilisateur
                    //uidToDisplay?.let { uid ->
                      //  Text(text = "UID de l'utilisateur '$usernameToFind' : $uid")
                    //} ?: run {
                      //  Text(text = "Aucun utilisateur trouvé avec le nom d'utilisateur '$usernameToFind'")
                    //}


                    ActionBarFind(navigateFunction = ::startActivity)
                }
            }
        }
    }

    fun getUidByUsername(username: String, onUidFound: (String?) -> Unit) {
        // Ajouter un auditeur pour récupérer les données des utilisateurs
        userRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Vérifier si des données ont été trouvées pour le nom d'utilisateur spécifié
                    if (dataSnapshot.exists()) {
                        // Récupérer l'UID du premier utilisateur trouvé (supposant qu'il n'y a qu'un seul utilisateur avec ce nom d'utilisateur)
                        val uid = dataSnapshot.children.firstOrNull()?.key
                        // Appeler la lambda avec l'UID trouvé
                        onUidFound(uid)
                    } else {
                        // Aucun utilisateur trouvé avec ce nom d'utilisateur
                        // Appeler la lambda avec null pour indiquer qu'aucun utilisateur n'a été trouvé
                        onUidFound(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Gérer les erreurs d'annulation
                    // Par exemple, afficher un message d'erreur à l'utilisateur
                    // ou enregistrer l'erreur dans les journaux de l'application
                }
            })
    }

    // Fonction pour récupérer la liste des noms d'utilisateur depuis la base de données Firebase
    fun fetchUsernames(completion: (List<String>) -> Unit) {
        val usernames = mutableListOf<String>()

        // Ajouter un auditeur pour récupérer les données des utilisateurs
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Parcourir les données pour extraire les noms d'utilisateur
                for (userSnapshot in dataSnapshot.children) {
                    val username = userSnapshot.child("username").getValue(String::class.java)
                    if (username != null) {
                        usernames.add(username)
                    }
                }
                // Appeler la fonction de complétion avec la liste des noms d'utilisateur
                completion(usernames)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Gérer les erreurs d'annulation
                // Par exemple, en affichant un message d'erreur à l'utilisateur
            }
        })
    }
/*
    fun getUidFromUsername(username: String, completion: (String?) -> Unit) {
        val userRef = FirebaseDatabase.getInstance().getReference("user")

        // Créer une requête pour rechercher l'utilisateur par son nom d'utilisateur
        userRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Parcourir les résultats de la requête
                    for (userSnapshot in dataSnapshot.children) {
                        // Récupérer la clé de l'utilisateur trouvé
                        val uid = userSnapshot.key
                        completion(uid) // Appeler la fonction de complétion avec l'UID
                        return // Sortir de la boucle après avoir trouvé la première correspondance
                    }
                    // Si aucun utilisateur correspondant n'est trouvé
                    completion(null)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Gérer les erreurs d'annulation
                    completion(null)
                }
            })
    }
*/
    private fun startActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun startActivity_username(activity: Class<*>, username: String, uid: String) {
        val intent = Intent(this, activity)
        intent.putExtra("username", username)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun UserSearchBar(users: List<String>, startActivityUsername: (Class<*>, String, String) -> Unit, getUidByUsername: KFunction2<String, (String?) -> Unit, Unit>) {
    val searchQuery = remember { mutableStateOf("") }
    //var isFocused by remember { mutableStateOf(false) }
    var filteredUsers by remember { mutableStateOf(emptyList<String>()) }

    Column {
        Text(
            text = "Enter a letter to find the usernames which begins by this letter : ",
            textAlign = TextAlign.Center, // Centrer le texte
            fontSize = 18.sp, // Taille de police un peu plus grande
            fontWeight = FontWeight.Bold, // Police en gras
            modifier = Modifier.padding(vertical = 8.dp) // Ajouter un espace vertical
        )
        Spacer(modifier = Modifier.height(16.dp)) // Créer un espace vertical de 16dp
        TextField(
            value = searchQuery.value,
            onValueChange = { searchQuery.value = it },
            label = { Text("Search") },
            modifier = Modifier.padding(16.dp)
            //keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            //keyboardActions = KeyboardActions(onDone = { filteredUsers = filterUsers(users, searchQuery.value) })
        )
        Button(
            onClick = {
                filteredUsers = filterUsers(users, searchQuery.value)
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Find")
        }

        val context = LocalContext.current

        LazyColumn {
            items(filteredUsers) { username ->
                ClickableText(
                    text = buildAnnotatedString {
                        append(username) // Afficher le nom d'utilisateur et l'UID
                    },
                    onClick = {
                        Toast.makeText(context, "$username clicked", Toast.LENGTH_SHORT).show()
                        getUidByUsername(username) { uid ->
                            if (uid != null) {
                                // UID trouvé, passer à une autre activité en utilisant l'UID
                                startActivityUsername(UserProfile_other_Activity::class.java, username, uid)
                            } else {
                                // Aucun utilisateur trouvé avec cet username
                                // Gérer le cas où aucun utilisateur n'est trouvé
                                Toast.makeText(context, "Aucun utilisateur trouvé avec l'username $username", Toast.LENGTH_SHORT).show()
                            }
                        }
                        // Rediriger vers une autre activité en passant l'UID en extra
                        //startActivityUsername(UserProfile_other_Activity::class.java, username)
                    },
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


    }
}

// Fonction pour filtrer les utilisateurs en fonction de la requête de recherche
private fun filterUsers(users: List<String>, query: String): List<String> {
    return if (query.isBlank()) {
        // Si la requête est vide, retourner la liste complète des utilisateurs
        users
    } else {
        // Filtrer les utilisateurs dont le nom commence par la requête de recherche
        users.filter { it.startsWith(query, ignoreCase = true) }
    }
}

/*
private suspend fun filterUsers(usernames: List<String>, query: String): List<Pair<String, String>> {
    val filteredUsers = mutableListOf<Pair<String, String>>()
    for (username in usernames) {
        val uid = username.uid
        if (uid != null) {
            filteredUsers.add(username to uid)
        }
    }
    return if (query.isBlank()) {
        emptyList()
    } else {
        filteredUsers.filter { (username, _) -> username.startsWith(query, ignoreCase = true) }
    }
}
*/
/*
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
*/

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

        IconButton(onClick = {FindActivity::class.java},
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