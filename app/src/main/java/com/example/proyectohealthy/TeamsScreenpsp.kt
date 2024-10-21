import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.proyectohealthy.R

@Composable
fun TeamsScreen() {
    val context = LocalContext.current
    AndroidView(
        factory = { ctx ->
            // Inflar el layout XML del fragmento de Home
            val inflater = LayoutInflater.from(ctx)
            inflater.inflate(R.layout.fragment_home, null) as FrameLayout
        }
    )
}
