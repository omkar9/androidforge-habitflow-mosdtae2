package com.androidforge.habitflow.domain.usecase.ad

import android.app.Activity
import com.androidforge.habitflow.core.ads.AdManager
import com.androidforge.habitflow.core.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase to display a previously loaded interstitial ad.
 * Requires an [Activity] context to show the ad.
 */
class ShowInterstitialAdUseCase @Inject constructor(
    private val adManager: AdManager,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Shows a loaded interstitial ad.
     *
     * @param activity The [Activity] context required by the AdMob SDK to display the ad.
     * @return A [Result] indicating success or failure of the ad display process.
     *         On success, it returns [Unit].
     */
    suspend operator fun invoke(activity: Activity): Result<Unit> = withContext(defaultDispatcher) {
        return@withContext try {
            adManager.showInterstitialAd(activity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}