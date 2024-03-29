package fr.isen.mouillot.sportscape

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme

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
                    //mettre la valeur de base
                    val userInput = intent.getStringExtra("userInput")

                    ProfileScreen(
                        navigateFunction = { destinationActivity ->
                            navigateToNextScreen(destinationActivity)
                        },
                        text = userInput ?: ""
                    )

                }
            }
        }
    }

    private fun navigateToNextScreen(destinationActivity: Class<*>) {
        val intent = Intent(this, destinationActivity)
        startActivity(intent)
        finish()
    }
}

@Composable
fun ProfileScreen(navigateFunction: (Class<*>) -> Unit, text: String) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Text(
                text = text,
                modifier = Modifier.padding(16.dp)
            )
            ClickableText(
                text = AnnotatedString("Edit Profile"),
                onClick = {
                    navigateFunction(ModifyActivity::class.java)
                },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun Greeting4(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview4() {
    SportScapeTheme {
        Greeting4("Android")
    }
}