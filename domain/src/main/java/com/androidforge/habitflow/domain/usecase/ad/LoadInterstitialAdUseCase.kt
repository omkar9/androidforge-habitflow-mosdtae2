package com.androidforge.habitflow.domain.usecase.ad

import com.androidforge.habitflow.core.ads.AdManager
import com.androidforge.habitflow.core.common.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase to initiate the loading of an interstitial ad.
 * This allows pre-loading the ad in the background before it's needed for display.
 */
class LoadInterstitialAdUseCase @Inject constructor(
    private val adManager: AdManager,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Loads an interstitial ad.
     *
     * @return A [Result] indicating success or failure of the ad loading process.
     *         On success, it returns [Unit].
     */
    suspend operator fun invoke(): Result<Unit> = withContext(defaultDispatcher) {
        return@withContext try {
            adManager.loadInterstitialAd()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}