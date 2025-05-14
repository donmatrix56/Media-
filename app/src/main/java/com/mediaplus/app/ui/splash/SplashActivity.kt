package com.mediaplus.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mediaplus.app.R
import com.mediaplus.app.databinding.ActivitySplashBinding
import com.mediaplus.app.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set tagline text to "The Ultimate Media Player"
        binding.appTagline.text = "The Ultimate Media Player"

        // Set version text in x.x.x.x format (use the correct property name)
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        binding.versionText.text = "v$versionName"

        // Set powered by text just above version
        binding.poweredBy.text = "Powered by SphereTech.inc"

        // Show splash screen for 3 seconds with animation
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 3000)
    }
}
