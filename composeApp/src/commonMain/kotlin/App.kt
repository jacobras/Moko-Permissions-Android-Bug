import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {

    MaterialTheme {
        val scope = rememberCoroutineScope()
        val factory = rememberPermissionsControllerFactory()
        val permissionController = remember(factory) { factory.createPermissionsController() }
        var permissionState by remember { mutableStateOf<PermissionState?>(null) }

        suspend fun updatePermission() {
            permissionState = permissionController.getPermissionState(Permission.RECORD_AUDIO)
        }

        BindEffect(permissionController)
        LaunchedEffect(Unit) { updatePermission() }

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            /**
             * Bug: this will show `Denied` when first clicking [deny], but when then clicking
             * [deny and don't ask again], it updates back to `NotDetermined`.
             */
            Text("Permission state: $permissionState")

            Button(
                onClick = {
                    scope.launch {
                        try {
                            permissionController.providePermission(Permission.RECORD_AUDIO)
                            updatePermission()
                        } catch (e: Exception) {
                            updatePermission()
                        }
                    }
                }
            ) {
                Text("Request AUDIO permission")
            }
        }
    }
}