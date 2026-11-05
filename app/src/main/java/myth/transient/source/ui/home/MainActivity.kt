package myth.transient.source.ui.home

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private val roleRequest = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        maybeAskForDefaultHomeOnce()
        setContent {
            var showDrawer by remember { mutableStateOf(false) }
            var showEmptyMenu by remember { mutableStateOf(false) }
            var currentTab by remember { mutableIntStateOf(0) }

            MaterialTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Transient Launcher") },
                            actions = {
                                TextButton(onClick = { showDrawer = true }) { Text("Apps") }
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        Modifier.fillMaxSize().padding(padding).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Home tab ${currentTab + 1}", style = MaterialTheme.typography.titleLarge)
                        Text("Long-press empty space for tab and wallpaper actions.")
                        Box(
                            Modifier.fillMaxWidth().weight(1f).combinedClickable(
                                onClick = { },
                                onLongClick = { showEmptyMenu = true }
                            )
                        ) {
                            Text("Your home screen is empty. Add apps from the drawer.")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { currentTab = (currentTab + 1).coerceAtMost(4) }) { Text("Next tab") }
                            TextButton(onClick = { showDrawer = true }) { Text("Open drawer") }
                        }
                    }
                }

                if (showDrawer) {
                    AlertDialog(
                        onDismissRequest = { showDrawer = false },
                        title = { Text("App drawer") },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("A–Z drawer placeholder")
                                Text("Folders tab and search will be added in the next implementation stages.")
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showDrawer = false }) { Text("Back to home") }
                        }
                    )
                }

                if (showEmptyMenu) {
                    AlertDialog(
                        onDismissRequest = { showEmptyMenu = false },
                        title = { Text("Home screen") },
                        text = { Text("Create tab (maximum 5), clear current tab, or change wallpaper will be implemented here.") },
                        confirmButton = {
                            TextButton(onClick = { showEmptyMenu = false }) { Text("Close") }
                        }
                    )
                }
            }
        }
    }

    private fun maybeAskForDefaultHomeOnce() {
        val prefs = getSharedPreferences("transient_launcher", MODE_PRIVATE)
        if (prefs.getBoolean("default_prompt_shown", false)) return
        if (isDefaultHome()) return

        prefs.edit().putBoolean("default_prompt_shown", true).apply()
        android.app.AlertDialog.Builder(this)
            .setTitle("Set Transient Launcher as default?")
            .setMessage("Would you like to use Transient Launcher as your default Home app?")
            .setPositiveButton("Yes") { _, _ -> openDefaultHomeSelection() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun isDefaultHome(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getSystemService(RoleManager::class.java).isRoleHeld(RoleManager.ROLE_HOME)
        } else {
            val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            val resolved = packageManager.resolveActivity(intent, 0)
            resolved?.activityInfo?.packageName == packageName
        }
    }

    private fun openDefaultHomeSelection() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val manager = getSystemService(RoleManager::class.java)
            if (manager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                roleRequest.launch(manager.createRequestRoleIntent(RoleManager.ROLE_HOME))
                return
            }
        }
        runCatching { startActivity(Intent(Settings.ACTION_HOME_SETTINGS)) }
            .onFailure { startActivity(Intent(Settings.ACTION_SETTINGS)) }
    }
}
