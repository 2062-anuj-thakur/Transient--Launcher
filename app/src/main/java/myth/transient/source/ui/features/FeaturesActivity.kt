package myth.transient.source.ui.features

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import myth.transient.source.ui.permissions.PermissionsActivity

class FeaturesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
                    Text("Version 0.1.0", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("What's included")
                    Spacer(Modifier.height(8.dp))
                    Text("• Home screen and app drawer foundation")
                    Text("• Optional screenshot-based layout reconstruction")
                    Text("• On-device OCR; screenshots are not uploaded")
                    Text("• App folders and widgets are planned capabilities")
                    Text("• Custom icon packs and advanced gestures are planned")
                    Spacer(Modifier.height(28.dp))
                    Button(onClick = {
                        startActivity(Intent(this@FeaturesActivity, PermissionsActivity::class.java))
                    }) { Text("Continue") }
                }
            }
        }
    }
}
