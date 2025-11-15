package com.matmop.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Keep splash very short to avoid ANR risk and meet Play guidance
        Handler(Looper.getMainLooper()).postDelayed({
            val accepted = getSharedPreferences("matmop_prefs", MODE_PRIVATE)
                .getBoolean("eula_accepted", false)
            val next = if (accepted) MainActivity::class.java else AgreementActivity::class.java
            val intent = Intent(this, next)
            startActivity(intent)
            finish()
        }, 1200) // ~1.2s
    }
}
