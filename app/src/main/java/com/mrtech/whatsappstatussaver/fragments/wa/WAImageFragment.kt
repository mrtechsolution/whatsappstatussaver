package com.mrtech.whatsappstatussaver.fragments.wa

import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.FragmentActivity
import android.widget.ProgressBar
import com.mrtech.whatsappstatussaver.adapter.WAImageAdapter
//import com.google.android.gms.ads.interstitial.InterstitialAd
import com.mrtech.whatsappstatussaver.model.WAImageModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.mrtech.whatsappstatussaver.fragments.wa.WAImageFragment
import com.mrtech.whatsappstatussaver.R
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.recyclerview.widget.GridLayoutManager
import com.mrtech.whatsappstatussaver.recycler.RecyclerTouchListener
import com.mrtech.whatsappstatussaver.recycler.RecyclerClick_Listener
import android.content.Intent
import com.mrtech.whatsappstatussaver.viewer.ImageViewer
import androidx.appcompat.app.AppCompatActivity
import com.mrtech.whatsappstatussaver.recycler.ToolbarActionModeCallback
import com.mrtech.whatsappstatussaver.GenericAdapter
import com.mrtech.whatsappstatussaver.InstanceHandler
import android.content.DialogInterface
import android.os.Environment
import android.widget.Toast
import com.mrtech.whatsappstatussaver.HelperMethods
import android.content.DialogInterface.OnShowListener
import org.apache.commons.io.comparator.LastModifiedFileComparator
import android.util.SparseBooleanArray
import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class WAImageFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var progressBar: ProgressBar? = null
    private var mAdView: AdView? = null
    var fab: FloatingActionButton? = null
    var waImageAdapter: WAImageAdapter? = null
    private var mInterstitialAd: InterstitialAd? = null
    var arrayList = ArrayList<WAImageModel>()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mActionMode: ActionMode? = null
    var frg: Fragment? = null
    var TAG = javaClass.name
    var rewardedAd: RewardedAd ?= null

    var ft: FragmentTransaction? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_wa_image, container, false)
        mInstance = this
        swipeRefreshLayout = v!!.findViewById<View>(R.id.ref) as SwipeRefreshLayout
        recyclerView = v!!.findViewById<View>(R.id.recyclerview_wa_image) as RecyclerView
        progressBar = v!!.findViewById<View>(R.id.progressbar_wa) as ProgressBar
        populateRecyclerView()
        implementRecyclerViewClickListeners()


        mAdView = v!!.findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)


        mAdView!!.adListener = object: AdListener() {
            override fun onAdClicked() {
                Toast.makeText(requireContext(), "OnAdd Click", Toast.LENGTH_SHORT).show()
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                Toast.makeText(requireContext(), "OnAdd Click", Toast.LENGTH_SHORT).show()
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                Toast.makeText(requireContext(), "onAdLoaded", Toast.LENGTH_SHORT).show()
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                Toast.makeText(requireContext(), "onAdOpened", Toast.LENGTH_SHORT).show()
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }


        InterstitialAd.load(requireContext(),"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.toString())
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })

        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                mInterstitialAd = null
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
        swipeRefreshLayout!!.setColorSchemeResources(
            *intArrayOf(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimaryDark
            )
        )
        swipeRefreshLayout!!.setOnRefreshListener { refresh() }
//        mInterstitialAd!!.show()
        fab = v!!.findViewById<View>(R.id.wa_image_fab_save_all) as FloatingActionButton
        fab!!.setOnClickListener { //                Toast.makeText(activity, "prob", Toast.LENGTH_SHORT).show();
            saveAll()
            if (mInterstitialAd != null) {
                mInterstitialAd!!.show(requireActivity())
            } else {
                Log.d("TAG", "The interstitial wasn't loaded yet.")
            }
        }
        return v
    }

    private fun populateRecyclerView() {
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = GridLayoutManager(requireContext(), 2)
        status
        waImageAdapter = WAImageAdapter(requireActivity(), arrayList)
        recyclerView!!.adapter = waImageAdapter
        waImageAdapter!!.notifyDataSetChanged()
        progressBar!!.visibility = View.GONE
    }

    //Implement item click and long click over recycler view
    private fun implementRecyclerViewClickListeners() {
        recyclerView!!.addOnItemTouchListener(
            RecyclerTouchListener(
                getActivity(),
                recyclerView!!,
                object : RecyclerClick_Listener {
                    override fun onClick(view: View?, position: Int) {
                        //If ActionMode not null select item
//                        if (mActionMode != null) onListItemSelect(position) else {

                        var adRequest: AdRequest = AdRequest.Builder().build()
                        RewardedAd.load(requireContext(), "ca-app-pub-3940256099942544/5224354917",
                            adRequest, object : RewardedAdLoadCallback() {
                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                                    // Handle the error.
                                    Log.d(TAG, loadAdError.toString())
                                    rewardedAd = null
                                }

                                override fun onAdLoaded(ad: RewardedAd) {
                                    rewardedAd = ad
                                    rewardedAd!!.show(requireActivity(), object : OnUserEarnedRewardListener{
                                        override fun onUserEarnedReward(p0: RewardItem) {
                                            Log.d(TAG, "onUserEarnedReward."+p0.amount)

                                        }

                                    })
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
                            val str = waImageAdapter!!.getItem(position).path
                            try {
                                val intent = Intent(getActivity(), ImageViewer::class.java)
                                intent.putExtra("pos", str)
                                intent.putExtra("position", position)
                                startActivityForResult(intent, 1)
                            } catch (e: Throwable) {
                                throw NoClassDefFoundError(e.message)
                            }
//                        }
                    }

                    override fun onLongClick(view: View?, position: Int) {
                        //Select item on long click
//                        mActionMode = null
//                        onListItemSelect(position)
                    }
                })
        )
    }

    //List item select method
    private fun onListItemSelect(position: Int) {
        waImageAdapter!!.toggleSelection(position) //Toggle the selection
        var fragments: List<Fragment?>
        val hasCheckedItems =
            waImageAdapter!!.selectedCount > 0 //Check if any items are already selected or not
        if (hasCheckedItems && mActionMode == null) {
            // there are some selected items, start the actionMode
            mActionMode = (getActivity() as AppCompatActivity?)!!.startSupportActionMode(
                (ToolbarActionModeCallback(
                    requireActivity(), GenericAdapter(waImageAdapter), arrayList, InstanceHandler(
                        mInstance
                    )
                ) as ActionMode.Callback)
            )
        } else if (!hasCheckedItems && mActionMode != null) // there no selected items, finish the actionMode
        {
            mActionMode!!.finish()
            mActionMode = null
        }
        if (mActionMode != null) //set action mode title on item selection
            mActionMode!!.title = waImageAdapter!!
                .selectedCount.toString() + " selected"
    }

    //Set action mode null after use
    fun setNullToActionMode() {
        if (mActionMode != null) mActionMode = null
    }

    private fun saveAll() {
        val alertDialogBuilder = AlertDialog.Builder(
            activity
        )

        // set title
        alertDialogBuilder.setTitle("Save All Status")

        // set dialog message
        alertDialogBuilder
            .setMessage("This Action will Save all the available Image Statuses... \nDo you want to Continue?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialog, id -> // if this button is clicked, close
                    // current activity
                    val listFiles = File(
                        StringBuffer().append(Environment.getExternalStorageDirectory().absolutePath)
                            .append("/WhatsApp/Media/.Statuses/").toString()
                    ).listFiles()
                    if (waImageAdapter!!.itemCount == 0) {
                        Toast.makeText(
                            activity,
                            "No Status available to Save...",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        var i = 0
                        while (i < listFiles.size) {
                            try {
                                val file = listFiles[i]
                                val str = file.name.toString()
                                if (str.endsWith(".jpg") || str.endsWith(".jpeg") || str.endsWith(".png")) {
                                    val helperMethods = HelperMethods(requireContext())
                                    HelperMethods.transfer(file)
                                }
                                i++
                            } catch (e: Exception) {
                                e.printStackTrace()
                                return@OnClickListener
                            }
                        }
                        Toast.makeText(activity, "Done :)", Toast.LENGTH_SHORT).show()
                    }
                })
            .setNegativeButton("No") { dialog, id -> // if this button is clicked, just close
                // the dialog box and do nothing
                dialog.cancel()
            }


        // create alert dialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                resources.getColor(R.color.black_overlay)
            )
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                resources.getColor(R.color.black_overlay)
            )
        }

        // show it
        alertDialog.show()
    }

    val status: Unit
        get() {
            Log.i(javaClass.name, "AbsolutePath==>"+Environment.getExternalStorageDirectory().absolutePath)
//            val listFiles = File(
//                StringBuffer().append(Environment.getExternalStorageDirectory().absolutePath)
//                    .append("/WhatsApp/Media/.Statuses/").toString()
//            ).listFiles()

            var targetPath =
                Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                targetPath =
                    Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
            }

            val listFiles = File(targetPath).listFiles()
            if (listFiles != null && listFiles.size >= 1) {
                Arrays.sort(listFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE)
            }
            if (listFiles != null) {
                for (file in listFiles) {
                    if (file.name.endsWith(".jpg") || file.name.endsWith(".jpeg") || file.name.endsWith(
                            ".png"
                        )
                    ) {
                        val model = WAImageModel(file.absolutePath)
                        arrayList.add(model)
                    }
                }
            }
        }

    fun deleteRows() {
        val selected = waImageAdapter!!
            .selectedIds //Get selected ids

        //Loop all selected ids
        for (i in selected.size() - 1 downTo 0) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                arrayList.removeAt(selected.keyAt(i))
                waImageAdapter!!.notifyDataSetChanged() //notify adapter
            }
        }
        Toast.makeText(activity, selected.size().toString() + " item deleted.", Toast.LENGTH_SHORT)
            .show() //Show Toast
        mActionMode!!.finish() //Finish action mode after use
    }

    fun refresh() {
        if (mActionMode != null) {
            mActionMode!!.finish()
        }
        //        waImageAdapter.notifyDataSetChanged();
        waImageAdapter!!.updateData(ArrayList())
        populateRecyclerView()
        swipeRefreshLayout!!.isRefreshing = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && data != null) {
            if (resultCode == -1) {
                refresh()
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                refresh()
            }
        }
    }

//    fun getmInterstitialAd(): InterstitialAd? {
//        return mInterstitialAd
//    }

    companion object {
        private var mInstance: WAImageFragment? = null
        private var v: View? = null
    }
}