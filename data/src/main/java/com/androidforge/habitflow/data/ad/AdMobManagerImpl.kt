package com.androidforge.habitflow.data.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.androidforge.habitflow.core.ads.AdManager
import com.androidforge.habitflow.core.common.Constants
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Implementation of [AdManager] using Google AdMob SDK.
 * Handles loading and showing interstitial ads.
 */
@Singleton
class AdMobManagerImpl @Inject constructor(
    private val context: Context
) : AdManager {

    private var interstitialAd: InterstitialAd? = null
    private val TAG = "AdMobManagerImpl"

    override suspend fun loadInterstitialAd() {
        if (interstitialAd != null) {
            Log.d(TAG, "Interstitial ad already loaded.")
            return
        }

        val adRequest = AdRequest.Builder().build()
        suspendCancellableCoroutine<Unit> { continuation ->
            InterstitialAd.load(context, Constants.ADMOB_INTERSTITIAL_AD_UNIT_ID, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Interstitial ad failed to load: ${adError.message}")
                    interstitialAd = null
                    if (continuation.isActive) {
                        continuation.resume(Unit) // Resume to avoid hanging, even on failure
                    }
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Interstitial ad loaded successfully.")
                    interstitialAd = ad
                    if (continuation.isActive) {
                        continuation.resume(Unit)
                    }
                }
            })

            continuation.invokeOnCancellation { Log.d(TAG, "Interstitial ad loading cancelled.") }
        }
    }

    override suspend fun showInterstitialAd(activity: Activity) {
        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    interstitialAd = null // Clear ad reference after showing
                    // Pre-load the next ad immediately after one is dismissed.
                    // This can be done in a separate coroutine or by calling loadInterstitialAd() directly.
                    // For simplicity, we'll just log here.
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Ad failed to show: ${adError.message}")
                    interstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }
            activity.runOnUiThread { // Ads must be shown on the main thread
                interstitialAd?.show(activity)
            }
        } else {
            Log.d(TAG, "Interstitial ad not ready to be shown. Attempting to load a new one.")
            // Optionally, try to load an ad immediately if it wasn't ready
            loadInterstitialAd() // This will run in a new coroutine if called from non-main thread
        }
    }

    override fun isInterstitialAdLoaded(): Boolean {
        return interstitialAd != null
    }
}