package myth.transient.source.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import myth.transient.source.ui.features.FeaturesActivity

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Transient Launcher", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(12.dp))
                    Text("A fresh home screen, with an optional attempt to recreate your previous layout.")
                    Spacer(Modifier.height(32.dp))
                    Button(onClick = {
                        startActivity(Intent(this@WelcomeActivity, FeaturesActivity::class.java))
                    }) { Text("Get started") }
                }
            }
        }
    }
}
