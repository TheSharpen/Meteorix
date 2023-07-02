package com.example.meteorix.ui.splash

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.meteorix.R
import com.example.meteorix.ui.main.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }

        setContentView(R.layout.splash_screen)

        supportActionBar?.hide()

        lifecycleScope.launch {
            delay(2500L)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

    }
}