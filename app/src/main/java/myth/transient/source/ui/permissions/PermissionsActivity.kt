package myth.transient.source.ui.permissions

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import myth.transient.source.ui.migration.MigrationActivity

class PermissionsActivity : ComponentActivity() {
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshVersion++ }

    private var refreshVersion by mutableIntStateOf(0)

    override fun onResume() {
        super.onResume()
        refreshVersion++
    }

    private fun openSettings(action: String, packageUri: Boolean = false) {
        val intent = Intent(action).apply {
            if (packageUri) data = Uri.parse("package:$packageName")
        }
        runCatching { startActivity(intent) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val refresh = refreshVersion
            val notificationsGranted = Build.VERSION.SDK_INT < 33 ||
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED
            val canInstall = Build.VERSION.SDK_INT < 26 || packageManager.canRequestPackageInstalls()
            val canOverlay = Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)
            val canWriteSettings = Build.VERSION.SDK_INT < 23 || Settings.System.canWrite(this)

            MaterialTheme {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Permissions & access", style = MaterialTheme.typography.headlineMedium)
                    Text("Only enable optional access if you want the related feature. You can continue without granting these.")
                    PermissionRow(
                        "Storage / screenshots",
                        "Not required: screenshots are selected through Android's Photo Picker."
                    )
                    PermissionRow(
                        "Install unknown apps",
                        if (canInstall) "Allowed" else "Optional special access; only needed for APK installation."
                    ) {
                        if (Build.VERSION.SDK_INT >= 26)
                            openSettings(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, true)
                    }
                    PermissionRow(
                        "Floating popup / display over other apps",
                        if (canOverlay) "Allowed" else "Optional overlay access; not required for the basic launcher."
                    ) {
                        if (Build.VERSION.SDK_INT >= 23)
                            openSettings(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, true)
                    }
                    PermissionRow(
                        "Notifications",
                        if (notificationsGranted) "Notification permission granted / not required on this Android version."
                        else "Optional notification posting permission."
                    ) {
                        if (Build.VERSION.SDK_INT >= 33)
                            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    PermissionRow(
                        "Modify system settings",
                        if (canWriteSettings) "Allowed" else "Optional special access; only needed for supported system-setting changes."
                    ) {
                        if (Build.VERSION.SDK_INT >= 23)
                            openSettings(Settings.ACTION_MANAGE_WRITE_SETTINGS, true)
                    }
                    Text(
                        "Note: notification-listener access is different from notification posting permission. " +
                            "This skeleton does not read or control other apps' notifications.",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            startActivity(Intent(this@PermissionsActivity, MigrationActivity::class.java))
                            finish()
                        }
                    ) { Text("Continue") }
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(title: String, status: String, action: (() -> Unit)? = null) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(status, style = MaterialTheme.typography.bodySmall)
            if (action != null) TextButton(onClick = action) { Text("Open / request access") }
        }
    }
}
