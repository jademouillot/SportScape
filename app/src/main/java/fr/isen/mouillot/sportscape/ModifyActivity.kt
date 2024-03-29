package fr.isen.mouillot.sportscape

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import java.util.concurrent.ExecutorService

class ModifyActivity : ComponentActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportScapeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {


                    val (userInput1, setUserInput1) = remember { mutableStateOf("user_name") }
                    val (userInput2, setUserInput2) = remember { mutableStateOf("user_description") }

                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_projet),
                            contentDescription = "",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(180.dp) // Définir la taille de l'image
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        //verticalArrangement = Arrangement.Center,
                        //horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(240.dp)) // Espacement vertical de 16 dp

                        Text(
                            text = "Username",
                            textAlign = TextAlign.Start, // Aligner le texte vers la gauche
                            fontSize = 20.sp, // Taille du texte
                            modifier = Modifier.padding(start = 16.dp) // Espacement à gauche

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(
                            value = userInput1,
                            onValueChange = { setUserInput1(it) },
                            //label = { Text("Text Field 1") }
                            modifier = Modifier.padding(horizontal = 16.dp) // Espacement à gauche et à droite

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = {
                                // Utilisez la valeur de userInput comme bon vous semble
                                Log.d("UserInput", "Texte saisi : $userInput1")
                                postdata(userInput1)
                                // Afficher un toast pour indiquer que les données ont été validées
                                Toast.makeText(
                                    applicationContext,
                                    "Validate username changes done with success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .padding(16.dp) // Ajoutez un padding de 16 dp
                                .widthIn(max = 200.dp) // Limitez la largeur maximale à 200 dp
                        ) {
                            Text("Validate username changes")
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Divider(
                            color = Color.Blue, // Couleur de la ligne
                            thickness = 2.dp, // Épaisseur de la ligne en pixels
                            modifier = Modifier
                                .padding(horizontal = 14.dp) // Espacement horizontal
                                .fillMaxWidth() // Remplit toute la largeur disponible
                                .align(Alignment.CenterHorizontally) // Alignement horizontal au centre
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "User description",
                            textAlign = TextAlign.Start, // Aligner le texte vers la gauche
                            fontSize = 20.sp, // Taille du texte
                            modifier = Modifier.padding(start = 16.dp) // Espacement à gauche

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        TextField(
                            value = userInput2,
                            onValueChange = { setUserInput2(it) },
                            //label = { Text("Text Field 1") }
                            modifier = Modifier.padding(horizontal = 16.dp) // Espacement à gauche et à droite

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Button(
                            onClick = {
                                // Utilisez la valeur de userInput comme bon vous semble
                                Log.d("UserInput", "Texte saisi : $userInput2")
                                postdata(userInput2)
                                // Afficher un toast pour indiquer que les données ont été validées
                                Toast.makeText(
                                    applicationContext,
                                    "Validate biography changes done with success",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier
                                .padding(16.dp) // Ajoutez un padding de 16 dp
                                .widthIn(max = 200.dp) // Limitez la largeur maximale à 200 dp
                        ) {
                            Text("Validate biography changes")
                        }
                    }
                    /*
                    Button(
                        onClick = {
                            // Utilisez la valeur de userInput comme bon vous semble
                            Log.d("UserInput", "Texte saisi : $userInput1")

                        }
                    ) {
                        Text("Valider")
                    }
*/
                    /*Button(
                        onClick = {
                            // Utilisez la valeur de userInput comme bon vous semble
                            //envoyer sur la database
                        }
                    ) {
                        Text("Envoyer")
                    }*/
                    // Définir un MutableState pour stocker la valeur récupérée
                    //val receivedValue = remember { mutableStateOf("") }
                    //ReceivedData(receivedValue)
                    //DisplayReceivedValue(receivedValue.value)
                }

            }


        }

    }

}


private fun postdata(userInput: String) {
    val database =
        FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
    val myRefToWrite = database.getReference("RegisterUser").push().setValue(userInput)
    //val myRefToRead = database.getReference("tmp").get()
}


/*
@Composable
fun ReceivedData(receivedValue: MutableState<String>) {
    // Créez un MutableState pour stocker la valeur reçue
    //val receivedValue = remember { mutableStateOf("") }

    // Récupérez la valeur depuis Firebase et mettez à jour le MutableState
    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance("https://sportscape-38027-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("tmp")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mettre à jour le MutableState avec la valeur récupérée
                receivedValue.value = dataSnapshot.getValue(String::class.java) ?: ""
                Log.d(TAG, "Value is: ${receivedValue.value}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "Failed to read value.", error.toException())
            }
        })
    }

    // Affichez la valeur dans votre interface utilisateur
    Text(
        text = receivedValue.value,
        //style = TextStyle(fontSize = 20.sp)
    )
}

@Composable
fun DisplayReceivedValue(receivedValue: String) {
    Text(
        text = receivedValue,
        //style = TextStyle(fontSize = 20.sp)
    )
}
*/
