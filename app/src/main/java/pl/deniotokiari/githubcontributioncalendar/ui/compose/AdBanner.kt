package pl.deniotokiari.githubcontributioncalendar.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import pl.deniotokiari.githubcontributioncalendar.BuildConfig

@Composable
fun AddBanner(
    modifier: Modifier = Modifier,
    adUnitId: String
) {
    AndroidView(
        modifier = modifier,
        factory = {
            AdView(it).apply {
                setAdSize(AdSize.BANNER)
                setAdUnitId(
                    if (BuildConfig.DEBUG) {
                        "ca-app-pub-3940256099942544/6300978111"
                    } else {
                        adUnitId
                    }
                )
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}