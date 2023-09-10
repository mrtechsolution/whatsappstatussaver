package com.mrtech.whatsappstatussaver.viewer

//import com.google.android.gms.ads.interstitial.InterstitialAd
//import com.google.android.gms.ads.InterstitialAd

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mrtech.whatsappstatussaver.HelperMethods
import com.mrtech.whatsappstatussaver.R
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean


const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
class ImageViewer : AppCompatActivity() {
    var helperMethods: HelperMethods? = null
    private var mInterstitialAd: InterstitialAd? = null
    var floatingMenu: FloatingActionMenu? = null
    var position = 0
    var f: File? = null
    var TAG = javaClass.name
    var rewardedAd: RewardedAd ?= null
    private var isLoading = false

    internal inner class SomeClass(private val imageViewer: ImageViewer, private val file: File) :
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
                        "Image Saved to Gallery :)",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (mInterstitialAd != null) {
                        mInterstitialAd!!.show(this@ImageViewer)
                    } else {
                        Log.d("TAG", "The interstitial wasn't loaded yet.")
                    }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)
        helperMethods = HelperMethods(this)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        //        getSupportActionBar().setIcon(R.drawable.business_notif);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        supportActionBar!!.hide()
        val intent = intent
        val string = intent.extras!!.getString("pos")
        position = intent.extras!!.getInt("position")
//        loadRewardedAd()



        // This sample attempts to load ads using consent obtained in the previous session.
        MobileAds.initialize(this)
//        mInterstitialAd = InterstitialAd(applicationContext)
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen))
//        mInterstitialAd.loadAd(AdRequest.Builder().build())
//        mAdView = WAImageFragment.v!!.findViewById(R.id.adView)
//        val adRequest = AdRequest.Builder().build()
//        mAdView!!.loadAd(adRequest)
//
//
//        mAdView!!.adListener = object: AdListener() {
//            override fun onAdClicked() {
//                Toast.makeText(requireContext(), "OnAdd Click", Toast.LENGTH_SHORT).show()
//                // Code to be executed when the user clicks on an ad.
//            }
//
//            override fun onAdClosed() {
//                Toast.makeText(requireContext(), "OnAdd Click", Toast.LENGTH_SHORT).show()
//                // Code to be executed when the user is about to return
//                // to the app after tapping on an ad.
//            }
//
//            override fun onAdFailedToLoad(adError : LoadAdError) {
//                // Code to be executed when an ad request fails.
//            }
//
//            override fun onAdImpression() {
//                // Code to be executed when an impression is recorded
//                // for an ad.
//            }
//
//            override fun onAdLoaded() {
//                Toast.makeText(requireContext(), "onAdLoaded", Toast.LENGTH_SHORT).show()
//                // Code to be executed when an ad finishes loading.
//            }
//
//            override fun onAdOpened() {
//                Toast.makeText(requireContext(), "onAdOpened", Toast.LENGTH_SHORT).show()
//                // Code to be executed when an ad opens an overlay that
//                // covers the screen.
//            }
//        }


        var adRequest: AdRequest = AdRequest.Builder().build()
        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
            adRequest, object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error.
                    Log.d(TAG, loadAdError.toString())
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    Log.d(TAG, "Ad was loaded.")
                }
            })
        rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
            }



            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

//        rewardedAd?.let { ad ->
//            ad.show(this, OnUserEarnedRewardListener { rewardItem ->
//                // Handle the reward.
//                val rewardAmount = rewardItem.amount
//                val rewardType = rewardItem.type
//                Log.d(TAG, "User earned the reward."+rewardAmount+" "+rewardType)
//            })
//        } ?: run {
//            Log.d(TAG, "The rewarded ad wasn't ready yet.")
//        }
        f = File(string)
        val photoView = findViewById<View>(R.id.photo) as ImageView
        floatingMenu = findViewById<View>(R.id.menu) as FloatingActionMenu
        val floatingActionButton = findViewById<View>(R.id.save) as FloatingActionButton
//        val floatingActionButton2 = findViewById<View>(R.id.wall) as FloatingActionButton
//        val floatingActionButton3 = findViewById<View>(R.id.rep) as FloatingActionButton
        val floatingActionButton4 = findViewById<View>(R.id.dlt) as FloatingActionButton
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("deleteFab", true)) {
            floatingActionButton4.visibility = View.VISIBLE
        } else {
            floatingActionButton4.visibility = View.GONE
        }
        floatingActionButton.setOnClickListener(downloadMediaItem(f))
        Glide.with(this).load(f).into(photoView)
//        floatingActionButton2.setOnClickListener(View.OnClickListener {
//            val intent: Intent
//            val uriForFile: Uri
//            if (Build.VERSION.SDK_INT >= 24) {
//                uriForFile = FileProvider.getUriForFile(
//                    applicationContext, StringBuffer().append(
//                        applicationContext.packageName
//                    ).append(".provider").toString(), f!!
//                )
//                intent = Intent("android.intent.action.ATTACH_DATA")
//                intent.setDataAndType(uriForFile, "image/*")
//                intent.putExtra("mimeType", "image/*")
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                startActivity(Intent.createChooser(intent, "Set as: "))
//                return@OnClickListener
//            }
//            uriForFile = Uri.parse(
//                StringBuffer().append("file://").append(
//                    f!!.absolutePath
//                ).toString()
//            )
//            intent = Intent("android.intent.action.ATTACH_DATA")
//            intent.setDataAndType(uriForFile, "image/*")
//            intent.putExtra("mimeType", "image/*")
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            startActivity(Intent.createChooser(intent, "Set as: "))
//        })
//        floatingActionButton3.setOnClickListener(View.OnClickListener {
//            val intent: Intent
//            val uriForFile: Parcelable
//            if (Build.VERSION.SDK_INT >= 24) {
//                uriForFile = FileProvider.getUriForFile(
//                    applicationContext, StringBuffer().append(
//                        packageName
//                    ).append(".provider").toString(), f!!
//                )
//                try {
//                    intent = Intent("android.intent.action.SEND")
//                    intent.type = "image/*"
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
//                intent.type = "image/*"
//                intent.setPackage("com.whatsapp")
//                intent.putExtra("android.intent.extra.STREAM", uriForFile)
//                startActivity(intent)
//            } catch (e2: ActivityNotFoundException) {
//                Toast.makeText(
//                    applicationContext,
//                    "WhatsApp Not Found on this Phone :(",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        })
        floatingActionButton4.setOnClickListener { //                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
//                builder.setMessage("Sure to Delete this Image?").setNegativeButton("Nope", new DialogInterface.OnClickListener() {
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
//                       digStash();
//                        Toast.makeText(getApplicationContext(), "Image Deleted", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.create().show();
            digStash()
        }
    }

    fun digStash() {
        if (f!!.exists()) {
            f!!.delete()
            Toast.makeText(applicationContext, "Image Deleted", Toast.LENGTH_SHORT).show()
        }
        val intent = Intent()
        intent.putExtra("pos", position)
        setResult(-1, intent)
        finish()
    }

    fun downloadMediaItem(file: File?): View.OnClickListener {
        return SomeClass(this, file!!)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntent = Intent()
        setResult(RESULT_CANCELED, returnIntent)
        finish()
    }

//    fun getmInterstitialAd(): InterstitialAd? {
//        return mInterstitialAd
//    }

    private fun loadRewardedAd() {
        if (rewardedAd == null) {
            isLoading = true
            var adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                this,
                AD_UNIT_ID,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        Log.d(TAG, adError?.message)
                        isLoading = false
                        rewardedAd = null
                    }

                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "Ad was loaded.")
                        rewardedAd = ad
                        isLoading = false
                    }
                }
            )
        }
    }



    private fun initializeMobileAdsSdk() {


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}
        // Load an ad.
        loadRewardedAd()
    }
}