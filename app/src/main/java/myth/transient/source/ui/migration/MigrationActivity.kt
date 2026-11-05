package myth.transient.source.ui.migration

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import myth.transient.source.ui.home.MainActivity

class MigrationActivity : ComponentActivity() {
    private val drawerUris = mutableStateListOf<Uri>()
    private val homeUris = mutableStateListOf<Uri>()
    private var status by mutableStateOf("Choose whether to attempt a visual reconstruction.")

    private val pickDrawer = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3)
    ) { uris -> acceptUris(uris, drawerUris, homeUris, "drawer") }

    private val pickHome = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(maxItems = 3)
    ) { uris -> acceptUris(uris, homeUris, drawerUris, "home") }

    private fun acceptUris(
        incoming: List<Uri>,
        destination: MutableList<Uri>,
        otherScreen: List<Uri>,
        label: String
    ) {
        val reused = incoming.filter { it in otherScreen }
        val fresh = incoming.filter { it !in otherScreen }.take(3 - destination.size)
        fresh.forEach { uri ->
            runCatching {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            if (uri !in destination) destination.add(uri)
        }
        status = buildString {
            append("${fresh.size} $label screenshot(s) selected.")
            if (reused.isNotEmpty()) append(" ${reused.size} already belong to the other screen and were ignored.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var restoreDrawer by remember { mutableStateOf(true) }
            var restoreHome by remember { mutableStateOf(true) }

            MaterialTheme {
                Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text("Restore your previous layout?", style = MaterialTheme.typography.headlineMedium)
                    Text("This first demo supports a grid drawer and a left-to-right home grid. Unsupported layouts fall back to an empty home and an A–Z app drawer.")
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(restoreDrawer, { restoreDrawer = it })
                        Text("Try restoring app drawer positions")
                    }
                    Text("${drawerUris.size}/3 drawer screenshots")
                    OutlinedButton(
                        enabled = restoreDrawer && drawerUris.size < 3,
                        onClick = { pickDrawer.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Select drawer screenshot(s)") }

                    HorizontalDivider()

                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        Checkbox(restoreHome, { restoreHome = it })
                        Text("Try restoring home-screen positions")
                    }
                    Text("${homeUris.size}/3 home screenshots")
                    OutlinedButton(
                        enabled = restoreHome && homeUris.size < 3,
                        onClick = { pickHome.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Select home screenshot(s)") }

                    Text(status, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "OCR runs on-device. It can recognize text and approximate positions; it cannot reliably identify every icon, recover hidden pages, or reproduce widgets and launcher-specific behavior.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.weight(1f))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            // TODO: Persist selected URIs and run OCR/matching pipeline.
                            startActivity(Intent(this@MigrationActivity, MainActivity::class.java))
                            finish()
                        }
                    ) { Text("Finish and open launcher") }
                    TextButton(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            drawerUris.clear()
                            homeUris.clear()
                            startActivity(Intent(this@MigrationActivity, MainActivity::class.java))
                            finish()
                        }
                    ) { Text("Skip migration") }
                }
            }
        }
    }
}
