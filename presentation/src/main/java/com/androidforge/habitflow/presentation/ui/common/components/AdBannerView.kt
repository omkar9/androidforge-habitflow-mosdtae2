package com.androidforge.habitflow.presentation.ui.common.components

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/**
 * A Composable function that integrates a Google AdMob banner ad into the UI.
 * The banner ad will automatically resize to fit its container width and a standard height.
 *
 * @param adUnitId The AdMob ad unit ID for the banner ad. This should typically come from `strings.xml`.
 * @param modifier The modifier to be applied to the banner ad container.
 */
@Composable
fun AdBannerView(
    adUnitId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder background for ad space
            .height(50.dp) // Standard banner height, AdView will adjust
    ) {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = {
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { adView ->
                // Optional: Update logic if adUnitId or other properties change
                // For now, we assume adUnitId is static once composed.
            }
        )
    }
}