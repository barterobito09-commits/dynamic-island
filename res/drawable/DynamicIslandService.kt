package com.example.dynamicisland

import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout

class DynamicIslandService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var islandView: View
    private lateinit var params: WindowManager.LayoutParams

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Inflate layout pil hitam
        islandView = LayoutInflater.from(this).inflate(R.layout.layout_dynamic_island, null)

        // Konfigurasi WindowManager Layout Params
        val LAYOUT_FLAG = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            LAYOUT_FLAG,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            // Posisi awal di tengah atas (menyesuaikan punch hole)
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 20 // Sesuaikan jarak dari atas layar
        }

        windowManager.addView(islandView, params)
    }

    // Fungsi penting agar saat HP diputar ke Landscape, posisi island tetap aman / menyesuaikan
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (::windowManager.isInitialized && ::islandView.isInitialized) {
            // Update layout saat rotasi berubah (Portrait / Landscape)
            windowManager.updateViewLayout(islandView, params)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::windowManager.isInitialized && ::islandView.isInitialized) {
            windowManager.removeView(islandView)
        }
    }
}
