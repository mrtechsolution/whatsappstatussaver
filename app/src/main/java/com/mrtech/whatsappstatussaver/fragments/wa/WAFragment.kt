package com.mrtech.whatsappstatussaver.fragments.wa

import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrtech.whatsappstatussaver.R
import com.mrtech.whatsappstatussaver.adapter.ViewPagerWAAdapter
import com.mrtech.whatsappstatussaver.fragments.wa.WAImageFragment
import com.mrtech.whatsappstatussaver.fragments.wa.WAVideoFragment

class WAFragment : Fragment() {
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_wa, container, false)
        viewPager = v.findViewById<View>(R.id.viewPager_wa) as ViewPager
        tabLayout = v.findViewById<View>(R.id.tab_layout_wa) as TabLayout
        viewPager!!.offscreenPageLimit = 2
        val adapter = ViewPagerWAAdapter(childFragmentManager)
        adapter.addTabs("Images", WAImageFragment())
        adapter.addTabs("Videos", WAVideoFragment())
        viewPager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)
        return v
    }
}