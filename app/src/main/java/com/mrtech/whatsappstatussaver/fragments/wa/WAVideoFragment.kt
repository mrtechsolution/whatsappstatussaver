package com.mrtech.whatsappstatussaver.fragments.wa

import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.FragmentActivity
import android.widget.ProgressBar
import com.mrtech.whatsappstatussaver.adapter.WAVideoAdapter
import com.mrtech.whatsappstatussaver.model.WAImageModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
//import com.google.android.gms.ads.interstitial.InterstitialAd
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.mrtech.whatsappstatussaver.fragments.wa.WAVideoFragment
import com.mrtech.whatsappstatussaver.R
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.recyclerview.widget.GridLayoutManager
import com.mrtech.whatsappstatussaver.recycler.RecyclerTouchListener
import com.mrtech.whatsappstatussaver.recycler.RecyclerClick_Listener
import android.content.Intent
import com.mrtech.whatsappstatussaver.viewer.VideoPlayer
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
import android.os.Build
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.ads.*
import java.io.File
import java.lang.Exception
import java.util.*

//import com.mrtech.whatsappstatussaver.viewer.VideoPlayer;
/**
 * A simple [Fragment] subclass.
 */
class WAVideoFragment : Fragment() {
    var recyclerView: RecyclerView? = null
    var progressBar: ProgressBar? = null
    private var mAdView: AdView? = null
    var fab: FloatingActionButton? = null
    var waVideoAdapter: WAVideoAdapter? = null
    var arrayList = ArrayList<WAImageModel>()
    var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mActionMode: ActionMode? = null
//    private var mInterstitialAd: InterstitialAd? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_wa_video, container, false)
        mInstance = this
        swipeRefreshLayout = v!!.findViewById<View>(R.id.ref_wa_video) as SwipeRefreshLayout
        recyclerView = v!!.findViewById<View>(R.id.recyclerview_wa_video) as RecyclerView
        progressBar = v!!.findViewById<View>(R.id.progressbar_wa_video) as ProgressBar
        populateRecyclerView()
        implementRecyclerViewClickListeners()
//        MobileAds.initialize(activity!!, getString(R.string.admob_app_id))
//        mInterstitialAd = InterstitialAd(activity)
//        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen))
//        mInterstitialAd.loadAd(AdRequest.Builder().build())
//        val adView = AdView(requireActivity())
//        adView.setAdSize(AdSize.BANNER)
//        adView.adUnitId = "ca-app-pub-2268220113540400/2547549045"
//        mAdView = v!!.findViewById(R.id.adView)
//        val adRequest = AdRequest.Builder().build()
//        mAdView!!.loadAd(adRequest)

    mAdView =  v!!.findViewById(R.id.adView)
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
        swipeRefreshLayout!!.setColorSchemeResources(
            *intArrayOf(
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimaryDark
            )
        )
        swipeRefreshLayout!!.setOnRefreshListener { refresh() }
        fab = v?.findViewById<View>(R.id.wa_video_fab_save_all) as FloatingActionButton
        fab!!.setOnClickListener {
            saveAll()
//            if (mInterstitialAd.isLoaded()) {
//                mInterstitialAd!!.show()
//            } else {
//                Log.d("TAG", "The interstitial wasn't loaded yet.")
//            }
        }
        return v
    }

    private fun populateRecyclerView() {
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = GridLayoutManager(activity, 2)
        status
        waVideoAdapter = WAVideoAdapter(requireContext(), arrayList)
        recyclerView!!.adapter = waVideoAdapter
        waVideoAdapter!!.notifyDataSetChanged()
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
                            val str = waVideoAdapter!!.getItem(position).path
                            try {
                                val intent = Intent(getActivity(), VideoPlayer::class.java)
                                intent.putExtra("pos", str)
                                intent.putExtra("position", position)
                                startActivityForResult(intent, 101)
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
        waVideoAdapter!!.toggleSelection(position) //Toggle the selection
        var fragments: List<Fragment?>
        val hasCheckedItems =
            waVideoAdapter!!.selectedCount > 0 //Check if any items are already selected or not
        if (hasCheckedItems && mActionMode == null) {
            // there are some selected items, start the actionMode
            mActionMode = (getActivity() as AppCompatActivity?)!!.startSupportActionMode(
                (ToolbarActionModeCallback(
                    requireContext(), GenericAdapter(waVideoAdapter), arrayList, InstanceHandler(
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
            mActionMode!!.title = waVideoAdapter!!
                .selectedCount.toString() + " selected"
    }

    //Set action mode null after use
    fun setNullToActionMode() {
        if (mActionMode != null) mActionMode = null
    }

    private fun saveAll() {
        val alertDialogBuilder = AlertDialog.Builder(
            requireContext()
        )

        // set title
        alertDialogBuilder.setTitle("Save All Status")

        // set dialog message
        alertDialogBuilder
            .setMessage("This Action will Save all the available Video Statuses... \nDo you want to Continue?")
            .setCancelable(false)
            .setPositiveButton(
                "Yes",
                DialogInterface.OnClickListener { dialog, id -> // if this button is clicked, close
                    // current activity
                    val listFiles = File(
                        StringBuffer().append(Environment.getExternalStorageDirectory().absolutePath)
                            .append("/WhatsApp/Media/.Statuses/").toString()
                    ).listFiles()
                    if (waVideoAdapter!!.itemCount == 0) {
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
                                if (str.endsWith(".mp4") || str.endsWith(".avi") || str.endsWith(".mkv") || str.endsWith(
                                        ".gif"
                                    )
                                ) {
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

            var targetPath =
                Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/.Statuses"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                targetPath =
                    Environment.getExternalStorageDirectory().absolutePath + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
            }

            val listFiles = File(targetPath).listFiles()
//            val listFiles = File(
//                StringBuffer().append(Environment.getExternalStorageDirectory().absolutePath)
//                    .append("/WhatsApp/Media/.Statuses/").toString()
//            ).listFiles()
            if (listFiles != null && listFiles.size >= 1) {
                Arrays.sort(listFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE)
            }
            if (listFiles != null) {
                for (file in listFiles) {
                    if (file.name.endsWith(".mp4") || file.name.endsWith(".avi") || file.name.endsWith(
                            ".mkv"
                        ) || file.name.endsWith(".gif")
                    ) {
                        val model = WAImageModel(file.absolutePath)
                        arrayList.add(model)
                    }
                }
            }

            Log.i(javaClass.name, "arrayList==>"+arrayList.size)
        }

    fun deleteRows() {
        val selected = waVideoAdapter!!
            .selectedIds //Get selected ids

        //Loop all selected ids
        for (i in selected.size() - 1 downTo 0) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
                arrayList.removeAt(selected.keyAt(i))
                waVideoAdapter!!.notifyDataSetChanged() //notify adapter
            }
        }
        Toast.makeText(
            getActivity(),
            selected.size().toString() + " item deleted.",
            Toast.LENGTH_SHORT
        ).show() //Show Toast
        mActionMode!!.finish() //Finish action mode after use
    }

    fun refresh() {
        if (mActionMode != null) {
            mActionMode!!.finish()
        }
        waVideoAdapter!!.updateData(ArrayList())
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
        private var mInstance: WAVideoFragment? = null
        private var v: View? = null
    }
}