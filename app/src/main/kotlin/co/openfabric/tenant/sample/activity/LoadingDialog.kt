package co.openfabric.tenant.sample.activity

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import co.openfabric.unilateral.sample.R

class LoadingDialog(context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_loading_indicator)
        setCancelable(false)

        val loadingText = findViewById<TextView>(R.id.loadingText)
        startLoadingTextAnimation(loadingText)
    }

    private fun startLoadingTextAnimation(loadingText: TextView) {
        val screenWidth = context.resources.displayMetrics.widthPixels.toFloat()
        val loadingTextWidth = loadingText.paint.measureText(loadingText.text.toString())
        val animationDistance = screenWidth + loadingTextWidth

        val translateAnimation = TranslateAnimation(
            -loadingTextWidth, animationDistance, 0f, 0f
        ).apply {
            duration = 2000 // Set your desired animation duration
            repeatCount = Animation.INFINITE
            interpolator = LinearInterpolator()
        }

        loadingText.startAnimation(translateAnimation)
    }
}