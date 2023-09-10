package com.mrtech.whatsappstatussaver.viewer

import androidx.appcompat.app.AppCompatActivity
import com.mrtech.whatsappstatussaver.easyvideoplayer.EasyVideoCallback
import com.mrtech.whatsappstatussaver.HelperMethods
import com.github.clans.fab.FloatingActionMenu
//import com.google.android.gms.ads.interstitial.InterstitialAd
import com.mrtech.whatsappstatussaver.easyvideoplayer.EasyVideoPlayer
import com.mrtech.whatsappstatussaver.viewer.VideoPlayer
import android.widget.Toast
import android.os.Bundle
import com.mrtech.whatsappstatussaver.R
import android.content.Intent
import com.google.android.gms.ads.MobileAds
import android.preference.PreferenceManager
import android.annotation.SuppressLint
import android.os.Parcelable
import android.os.Build
import androidx.core.content.FileProvider
import android.content.ActivityNotFoundException
import android.app.Activity
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.ads.AdRequest
//import com.google.android.gms.ads.InterstitialAd
import java.io.File
import java.lang.Exception

class VideoPlayer : AppCompatActivity(), EasyVideoCallback {
    var helperMethods: HelperMethods? = null
    var menu: FloatingActionMenu? = null
//    private var mInterstitialAd: InterstitialAd? = null
    private var player: EasyVideoPlayer? = null
    var position = 0
    var f: File? = null

    internal inner class SomeClass(private val videoPlayer: VideoPlayer, private val file: File) :
        View.OnClickListener {
        internal inner class SomeOtherClass(
            private val context: SomeClass,
            private val file: File
        ) : Runnable {
            override fun run() {
                try {
                    HelperMethods.transfer(this.file)
                    Toast.makeText(
                        applicationContext,
                        "Video Saved to Gallery :)",
                        Toast.LENGTH_SHORT
                    ).show()
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd!!.show()
//                    } else {
//                        Log.d("TAG", "The interstitial wasn't loaded yet.")
//                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(
                        "GridView",
                        StringBuffer().append("onClick: Error: ").append(e.message).toString()
                    )
                }
            }
        }

        override fun onClick(view: View) {
            SomeOtherClass(this, file).run()
        }
    }

    //    @Override
    //    public void onPaused(EasyVideoPlayer easyVideoPlayer) {
    //    }
    //
    //    @Override
    //    public void onStarted(EasyVideoPlayer easyVideoPlayer) {
    //    }
    override fun onSubmit(easyVideoPlayer: EasyVideoPlayer?, uri: Uri?) {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        helperMethods = HelperMethods(this)
        helperMethods = HelperMethods(this)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar!!.setIcon(R.drawable.business_notif)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.hide()
        val intent = intent
        f = File(intent.extras!!.getString("pos"))
        position = intent.extras!!.getInt("position")
//        MobileAds.initialize(applicationContext, getString(R.string.admob_app_id))
//        mInterstitialAd = InterstitialAd(applicationContext)
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen))
//        mInterstitialAd.loadAd(AdRequest.Builder().build())
        menu = findViewById<View>(R.id.menu) as FloatingActionMenu
        val floatingActionButton = findViewById<View>(R.id.save) as FloatingActionButton
//        val floatingActionButton2 = findViewById<View>(R.id.rep) as FloatingActionButton
        val floatingActionButton3 = findViewById<View>(R.id.dlt) as FloatingActionButton
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("deleteFab", true)) {
            floatingActionButton3.visibility = View.VISIBLE
        } else {
            floatingActionButton3.visibility = View.GONE
        }
        floatingActionButton.setOnClickListener(downloadMediaItem(f))
//        floatingActionButton2.setOnClickListener(View.OnClickListener {
//            val uriForFile: Parcelable
//            val intent: Intent
//            if (Build.VERSION.SDK_INT >= 24) {
//                uriForFile = FileProvider.getUriForFile(
//                    applicationContext, StringBuffer().append(
//                        applicationContext.packageName
//                    ).append(".provider").toString(), f!!
//                )
//                try {
//                    intent = Intent("android.intent.action.SEND")
//                    intent.type = "*/*"
//                    intent.setPackage("com.whatsapp")
//                    intent.putExtra("android.intent.extra.STREAM", uriForFile)
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    startActivity(intent)
//                    startActivity(intent)
//                    return@OnClickListener
//                } catch (e: ActivityNotFoundException) {
//                    Toast.makeText(
//                        applicationContext,
//                        "WhatsApp Not Found on this Phone :(",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    return@OnClickListener
//                }
//            }
//            uriForFile = Uri.parse(
//                StringBuffer().append("file://").append(
//                    f!!.absolutePath
//                ).toString()
//            )
//            try {
//                intent = Intent("android.intent.action.SEND")
//                intent.type = "*/*"
//                intent.setPackage("com.whatsapp")
//                intent.putExtra("android.intent.extra.STREAM", uriForFile)
//                startActivity(intent)
//            } catch (e2: ActivityNotFoundException) {
//                Toast.makeText(applicationContext, "WhatsApp Not Found on this Phone :(", Toast.LENGTH_SHORT).show()
//            }
//        })
        floatingActionButton3.setOnClickListener { //                AlertDialog.Builder builder = new AlertDialog.Builder(this.this$0);
//                builder.setMessage("Sure to Delete this Video?").setNegativeButton("Nope", new DialogInterface.OnClickListener(this) {
//
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//
//
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        sendBackData();
//                        Toast.makeText(getApplicationContext(), "Video Deleted", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.create().show();
            sendBackData()
            Toast.makeText(applicationContext, "Video Deleted", Toast.LENGTH_SHORT).show()
        }
        player = findViewById<View>(R.id.player) as EasyVideoPlayer
        player!!.setCallback(this)
        player!!.setSource(Uri.fromFile(f))
    }

    fun sendBackData() {
        if (f!!.exists()) {
            f!!.delete()
        }
        val intent = Intent()
        intent.putExtra("pos", position)
        setResult(-1, intent)
    }

    fun downloadMediaItem(file: File?): View.OnClickListener {
        return SomeClass(this, file!!)
    }

    override fun onPause() {
        super.onPause()
        player!!.pause()
    }

    override fun onPreparing(easyVideoPlayer: EasyVideoPlayer?) {
        Log.d("EVP-Sample", "onPreparing()")
    }

    override fun onPrepared(easyVideoPlayer: EasyVideoPlayer?) {
        Log.d("EVP-Sample", "onPrepared()")
    }

    override fun onBuffering(i: Int) {
        Log.d(
            "EVP-Sample",
            StringBuffer().append(StringBuffer().append("onBuffering(): ").append(i).toString())
                .append("%").toString()
        )
    }

    override fun onError(easyVideoPlayer: EasyVideoPlayer?, exception: Exception?) {
        Log.d(
            "EVP-Sample", StringBuffer().append("onError(): ").append(
                exception!!.message
            ).toString()
        )
    }

    override fun onCompletion(easyVideoPlayer: EasyVideoPlayer?) {
        Log.d("EVP-Sample", "onCompletion()")
    }

    override fun onRetry(easyVideoPlayer: EasyVideoPlayer?, uri: Uri?) {
        Toast.makeText(this, "Retry", Toast.LENGTH_SHORT).show()
    }

    //    @Override
    //    public void onClickVideoFrame(EasyVideoPlayer easyVideoPlayer) {
    //        if (this.menu.isMenuButtonHidden()) {
    //            this.menu.showMenuButton(true);
    //            easyVideoPlayer.hideControls();
    //            return;
    //        }
    //        this.menu.hideMenuButton(true);
    //        easyVideoPlayer.showControls();
    //    }
    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }
}