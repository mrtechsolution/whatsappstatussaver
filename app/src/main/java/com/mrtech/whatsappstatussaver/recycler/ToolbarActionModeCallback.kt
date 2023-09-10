package com.mrtech.whatsappstatussaver.recycler

import android.content.Context
import com.mrtech.whatsappstatussaver.GenericAdapter
import com.mrtech.whatsappstatussaver.model.WAImageModel
import com.mrtech.whatsappstatussaver.InstanceHandler
import com.mrtech.whatsappstatussaver.adapter.WAVideoAdapter
import com.mrtech.whatsappstatussaver.adapter.WAImageAdapter
import com.mrtech.whatsappstatussaver.fragments.wa.WAImageFragment
import com.mrtech.whatsappstatussaver.fragments.wa.WAVideoFragment
import com.mrtech.whatsappstatussaver.fragments.bwa.BWAImageFragment
import com.mrtech.whatsappstatussaver.fragments.bwa.BWAVideoFragment
import com.mrtech.whatsappstatussaver.R
import android.os.Build
import androidx.core.view.MenuItemCompat
import android.util.SparseBooleanArray
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import com.mrtech.whatsappstatussaver.HelperMethods
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.io.File
import java.lang.Exception
import java.util.ArrayList

/**
 * Created by SONU on 22/03/16.
 */
class ToolbarActionModeCallback(
    private val context: Context,
    adapter: GenericAdapter<*>,
    message_models: ArrayList<WAImageModel>,
    instance: InstanceHandler<*>
) : ActionMode.Callback {
    private lateinit var waVideoAdapter: WAVideoAdapter
    private var waImageAdapter: WAImageAdapter? = null
    private val message_models: ArrayList<WAImageModel>

    //    private InterstitialAd mInterstitialAd;
    var waImageFragment: WAImageFragment? = null
    var waVideoFragment: WAVideoFragment? = null
    var bwaImageFragment: BWAImageFragment? = null
    var bwaVideoFragment: BWAVideoFragment? = null
    var s = ""
    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(
            R.menu.selection_menu,
            menu
        ) //Inflate the menu_main over action mode
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {

        //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
        //So here show action menu_main according to SDK Levels
        if (Build.VERSION.SDK_INT < 11) {
            MenuItemCompat.setShowAsAction(
                menu.findItem(R.id.action_delete),
                MenuItemCompat.SHOW_AS_ACTION_NEVER
            )
            MenuItemCompat.setShowAsAction(
                menu.findItem(R.id.action_save),
                MenuItemCompat.SHOW_AS_ACTION_NEVER
            )
        } else {
            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
            menu.findItem(R.id.action_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val selectedIds: SparseBooleanArray
        var size: Int
        when (item.itemId) {
            R.id.action_delete -> {
                when (s) {
                    "WAVideoFragment" -> {
                        selectedIds = waVideoAdapter.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                val file =
                                    File(waVideoAdapter.getItem(selectedIds.keyAt(size)).path)
                                try {
                                    if (file.exists() && file.isFile) {
                                        file.delete()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            size--
                        }
                        waVideoFragment!!.deleteRows()
                        waVideoFragment!!.refresh()
                        mode.finish()
                        return true
                    }
                    "BWAVideoFragment" -> {
                        selectedIds = waVideoAdapter.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                val file =
                                    File(waVideoAdapter.getItem(selectedIds.keyAt(size)).path)
                                try {
                                    if (file.exists() && file.isFile) {
                                        file.delete()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            size--
                        }
                        bwaVideoFragment!!.deleteRows()
                        bwaVideoFragment!!.refresh()
                        mode.finish()
                        return true
                    }
                    "WAImageFragment" -> {
                        selectedIds = waImageAdapter!!.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                val file =
                                    File(waImageAdapter!!.getItem(selectedIds.keyAt(size)).path)
                                try {
                                    if (file.exists() && file.isFile) {
                                        file.delete()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            size--
                        }
                        waImageFragment!!.deleteRows()
                        waImageFragment!!.refresh()
                        mode.finish()
                        return true
                    }
                    "BWAImageFragment" -> {
                        selectedIds = waImageAdapter!!.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                val file =
                                    File(waImageAdapter!!.getItem(selectedIds.keyAt(size)).path)
                                try {
                                    if (file.exists() && file.isFile) {
                                        file.delete()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            size--
                        }
                        bwaImageFragment!!.deleteRows()
                        bwaImageFragment!!.refresh()
                        mode.finish()
                        return true
                    }
                }
                when (s) {
                    "WAVideoFragment" -> {
                        selectedIds = waVideoAdapter.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waVideoAdapter.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish() //Finish action mode
                        //                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
//                        } else {
//                            Log.d("TAG", "The interstitial wasn't loaded yet.");
//                        }
                        return true
                    }
                    "BWAVideoFragment" -> {
                        selectedIds = waVideoAdapter.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waVideoAdapter.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish() //Finish action mode
                        //                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
//                        } else {
//                            Log.d("TAG", "The interstitial wasn't loaded yet.");
//                        }
                        return true
                    }
                    "WAImageFragment" -> {
                        selectedIds = waImageAdapter!!.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waImageAdapter!!.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish() //Finish action mode
                        //                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
//                        } else {
//                            Log.d("TAG", "The interstitial wasn't loaded yet.");
//                        }
                        return true
                    }
                    "BWAImageFragment" -> {
                        selectedIds = waImageAdapter!!.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waImageAdapter!!.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish() //Finish action mode
                        //                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
//                        } else {
//                            Log.d("TAG", "The interstitial wasn't loaded yet.");
//                        }
                        return true
                    }
                }
                return false
            }
            R.id.action_save -> {
                when (s) {
                    "WAVideoFragment" -> {
                        selectedIds = waVideoAdapter.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waVideoAdapter.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish()
                        return true
                    }
                    "BWAVideoFragment" -> {
                        selectedIds = waVideoAdapter.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waVideoAdapter.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish()
                        return true
                    }
                    "WAImageFragment" -> {
                        selectedIds = waImageAdapter!!.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waImageAdapter!!.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish()
                        return true
                    }
                    "BWAImageFragment" -> {
                        selectedIds = waImageAdapter!!.selectedIds
                        size = selectedIds.size() - 1
                        while (size >= 0) {
                            if (selectedIds.valueAt(size)) {
                                HelperMethods.transfer(
                                    File(
                                        waImageAdapter!!.getItem(
                                            selectedIds.keyAt(
                                                size
                                            )
                                        ).path
                                    )
                                )
                            }
                            size--
                        }
                        Toast.makeText(context, "Done! :)", Toast.LENGTH_SHORT).show()
                        mode.finish()
                        return true
                    }
                }
                return false
            }
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {

        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode
        val recyclerFragment: Fragment?
        when (s) {
            "WAVideoFragment" -> {
                waVideoAdapter.removeSelection() // remove selection
                recyclerFragment =
                    (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.fragment_wa_video) //Get recycler fragment
                if (recyclerFragment != null) (recyclerFragment as WAVideoFragment).setNullToActionMode() //Set action mode null
            }
            "BWAVideoFragment" -> {
                waVideoAdapter.removeSelection() // remove selection
                recyclerFragment =
                    (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.fragment_wa_video) //Get recycler fragment
                if (recyclerFragment != null) (recyclerFragment as BWAVideoFragment).setNullToActionMode() //Set action mode null
            }
            "WAImageFragment" -> {
                waImageAdapter!!.removeSelection() // remove selection
                recyclerFragment =
                    (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.fragment_wa_image) //Get recycler fragment
                if (recyclerFragment != null) (recyclerFragment as WAImageFragment).setNullToActionMode() //Set action mode null
            }
            "BWAImageFragment" -> {
                waImageAdapter!!.removeSelection() // remove selection
                recyclerFragment =
                    (context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.fragment_wa_image) //Get recycler fragment
                if (recyclerFragment != null) (recyclerFragment as BWAImageFragment).setNullToActionMode() //Set action mode null
            }
        }
    }

    init {
//        waVideoAdapter = waVideoAdapter!!
        this.message_models = message_models
        s = instance.value!!.javaClass.simpleName
        when (s) {
            "WAVideoFragment" -> {
                waVideoFragment = instance.value as WAVideoFragment
                waVideoAdapter = adapter.value as WAVideoAdapter
            }
            "BWAImageFragment" -> {
                bwaImageFragment = instance.value as BWAImageFragment
                waImageAdapter = adapter.value as WAImageAdapter
            }
            "WAImageFragment" -> {
                waImageFragment = instance.value as WAImageFragment
                //                mInterstitialAd= waImageFragment.getmInterstitialAd();
                waImageAdapter = adapter.value as WAImageAdapter
            }
            "BWAVideoFragment" -> {
                bwaVideoFragment = instance.value as BWAVideoFragment
                waVideoAdapter = adapter.value as WAVideoAdapter
            }
        }
    }
}