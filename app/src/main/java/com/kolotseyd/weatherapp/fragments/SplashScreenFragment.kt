package com.kolotseyd.weatherapp.fragments

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.kolotseyd.weatherapp.R

class SplashScreenFragment : Fragment() {

    private lateinit var ivAnimatedIcon: ImageView
    private val images = arrayOf(R.drawable.sun, R.drawable.moon, R.drawable.cloud)

    val animatorSet = AnimatorSet()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_splash_screen, container, false)
        ivAnimatedIcon = view.findViewById(R.id.ivAnimatedIcon)
        startAnimation()
        return view
    }

    private fun startAnimation() {
        val fadeIn = ObjectAnimator.ofFloat(ivAnimatedIcon, "alpha", 0f, 1f)
        fadeIn.duration = 1000

        val fadeOut = ObjectAnimator.ofFloat(ivAnimatedIcon, "alpha", 1f, 0f)
        fadeOut.duration = 1000

        val scaleX = ObjectAnimator.ofFloat(ivAnimatedIcon, "scaleX", 1.2f, 0.8f)
        scaleX.duration = 2000
        scaleX.repeatCount = ValueAnimator.INFINITE
        scaleX.repeatMode = ObjectAnimator.REVERSE

        val scaleY = ObjectAnimator.ofFloat(ivAnimatedIcon, "scaleY", 1.2f, 0.8f)
        scaleY.duration = 2000
        scaleY.repeatCount = ValueAnimator.INFINITE
        scaleY.repeatMode = ObjectAnimator.REVERSE

        animatorSet.play(fadeIn).with(scaleX).with(scaleY)
        animatorSet.play(fadeOut).after(scaleX).after(scaleY)


        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                ivAnimatedIcon.setImageResource(images.random())
                handler.postDelayed(this, 1000)
            }
        }

        animatorSet.start()
        handler.postDelayed(runnable, 1000)
    }

    override fun onStop() {
        super.onStop()
        animatorSet.cancel()
    }
}