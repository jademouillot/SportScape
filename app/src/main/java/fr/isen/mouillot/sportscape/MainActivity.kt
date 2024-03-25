package fr.isen.mouillot.sportscape

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import fr.isen.mouillot.sportscape.ui.theme.SportScapeTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SportScapeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android") {
                        navigateToMapActivity()
                    }
                }
            }
        }
    }

    private fun navigateToMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier, onNavigate: () -> Unit) {
    Column(modifier = modifier) {
        Text(text = "Hello $name!")
        Button(onClick = onNavigate) {
            Text("Go to Map")
        }
    }
}
