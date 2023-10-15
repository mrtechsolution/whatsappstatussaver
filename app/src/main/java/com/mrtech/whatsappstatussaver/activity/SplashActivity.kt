package com.mrtech.whatsappstatussaver.activity

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mrtech.whatsappstatussaver.R
import android.os.Build
import android.view.WindowManager
import android.content.pm.PackageManager
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.mrtech.whatsappstatussaver.MainActivity
import java.lang.Exception

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //        getSupportActionBar().hide();

            val decorView = window.decorView
            // Hide the status bar.
            val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
            decorView.systemUiVisibility = uiOptions
        if (checkInstallation("com.whatsapp")) {
            Handler().postDelayed({
                checkPerm() }, 2000)
        }
    }

    fun checkPerm() {
        Log.i(javaClass.name, "checkPerm==>"+Build.VERSION.SDK_INT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(javaClass.name, "checkPerm==>"+Build.VERSION.SDK_INT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Log.i(javaClass.name, "checkPerm==>"+Environment.isExternalStorageManager())
                if(!Environment.isExternalStorageManager()){
                    try {
                        var intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.setData(Uri.parse(String.format("package:%s", this.packageName)))
                        startActivityForResult(intent, 2296)
                    } catch (e:Exception) {
                        var intent = Intent()
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        startActivityForResult(intent, 2296)
                    }

                    requestPermissions(
                        arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                        ), 1
                    )
                } else {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()

                }
            } else if (applicationContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && applicationContext.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
                )
            } else {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        } else {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                } else {
                    checkPerm()
                }
                return
            }
        }
    }

    private fun checkInstallation(uri: String): Boolean {
        val pm = this.packageManager
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
        Log.i(javaClass.name, "appInstalled==> $app_installed")
        return app_installed
    }
}