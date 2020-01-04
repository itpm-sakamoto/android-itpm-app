package com.example.kanpelibrary.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.kanpelibrary.R

class SplashActivity : AppCompatActivity() {

    private val mHandler = Handler()
    private val mRunnable = Runnable {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mHandler.postDelayed(mRunnable, 2000L)
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mRunnable)
        finish()
    }
}
