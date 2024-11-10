package com.ftthreign.storyapp.views.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ftthreign.storyapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {

        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val pageTitle = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val nameTv = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val passwordTv = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val nameEt = ObjectAnimator.ofFloat(binding.nameEditText, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val emailEt = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val passwordEt = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)

        AnimatorSet().apply {
            playSequentially(
                pageTitle,
                nameTv,
                nameEt,
                emailTv,
                emailEt,
                passwordTv,
                passwordEt
            )
            startDelay = 300L
        }.start()
    }

    companion object {
        const val APPEAR_ANIMATION_DURATION = 400L
    }
}