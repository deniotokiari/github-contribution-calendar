package pl.deniotokiari.githubcontributioncalendar.ui.compose

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import pl.deniotokiari.githubcontributioncalendar.BuildConfig

class AdInterstitial {
    companion object {
        fun show(adUnitId: String, activity: Activity) {
            InterstitialAd.load(
                activity,
                if (BuildConfig.DEBUG) {
                    "ca-app-pub-3940256099942544/1033173712"
                } else {
                    adUnitId
                },
                AdRequest.Builder().build(),
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        ad.show(activity)
                    }
                }
            )
        }
    }
}