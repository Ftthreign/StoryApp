package com.ftthreign.storyapp.views.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.ftthreign.storyapp.R
import com.ftthreign.storyapp.databinding.ActivityRegisterBinding
import com.ftthreign.storyapp.helpers.Result
import com.ftthreign.storyapp.helpers.showMaterialDialog
import com.ftthreign.storyapp.views.viewmodels.AuthenticationViewModel
import com.ftthreign.storyapp.views.viewmodels.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private val viewModel by viewModels<AuthenticationViewModel> {
        ViewModelFactory.getInstance(this)
    }

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
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            viewModel.register(name, email, password).observe(this) {user ->
                when(user) {
                    is Result.Loading -> {
                        binding.registerLoading.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.registerLoading.visibility = View.GONE
                        showMaterialDialog(this@RegisterActivity, "Register Failed", user.error, "OK")
                    }
                    is Result.Success -> {
                        binding.registerLoading.visibility = View.GONE
                        if (user.data.error!!) {
                            showMaterialDialog(this@RegisterActivity, "Register Failed", user.data.message!!, "OK")
                        } else {
                            showMaterialDialog(this@RegisterActivity, "Login Success", getString(R.string.register_success), "Ok")
                        }
                    }
                }
            }
        }
        binding.gotoLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
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
        val nameEt = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val emailEt = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val passwordEt = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION)
        val registerBtn = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(
            APPEAR_ANIMATION_DURATION
        )

        AnimatorSet().apply {
            playSequentially(
                pageTitle,
                nameTv,
                nameEt,
                emailTv,
                emailEt,
                passwordTv,
                passwordEt,
                registerBtn
            )
            startDelay = 300L
        }.start()
    }

    companion object {
        const val APPEAR_ANIMATION_DURATION = 200L
    }
}