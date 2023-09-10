package com.mrtech.whatsappstatussaver.adapter

import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import java.util.ArrayList
import java.util.HashMap

/**
 * Created by umer on 19-Apr-18.
 */
class ViewPagerWAAdapter(private val mFragmentManager: FragmentManager) : FragmentPagerAdapter(
    mFragmentManager
) {
    var arrayListText = ArrayList<String>()
    var fragmentArrayList = ArrayList<Fragment>()
    private val mFragmentTags: MutableMap<Int, String?>
    override fun getItem(position: Int): Fragment {
        return fragmentArrayList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return arrayListText[position]
    }

    override fun getCount(): Int {
        return arrayListText.size
    }

    fun addTabs(text: String, fragment: Fragment) {
        arrayListText.add(text)
        fragmentArrayList.add(fragment)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val obj = super.instantiateItem(container, position)
        if (obj is Fragment) {
            // record the fragment tag here.
            val tag = obj.tag
            mFragmentTags[position] = tag
        }
        return obj
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun getFragment(position: Int): Fragment? {
        val tag = mFragmentTags[position] ?: return null
        return mFragmentManager.findFragmentByTag(tag)
    }

    init {
        mFragmentTags = HashMap()
    }
}