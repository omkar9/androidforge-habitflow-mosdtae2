package com.androidforge.habitflow.core.ads

import android.app.Activity

/**
 * Interface defining methods for loading and showing different types of ads.
 * This abstraction allows the domain and presentation layers to interact with ads
 * without direct dependency on a specific ad SDK (e.g., Google AdMob).
 */
interface AdManager {

    /**
     * Initiates the loading of an interstitial ad.
     * The ad should be pre-loaded before it's intended to be shown.
     */
    suspend fun loadInterstitialAd()

    /**
     * Shows a previously loaded interstitial ad.
     * If no ad is loaded or ready, this method might do nothing or log an error.
     * @param activity The [Activity] context from which the ad should be displayed.
     */
    suspend fun showInterstitialAd(activity: Activity)

    /**
     * Checks if an interstitial ad is currently loaded and ready to be shown.
     * @return `true` if an ad is ready, `false` otherwise.
     */
    fun isInterstitialAdLoaded(): Boolean
}