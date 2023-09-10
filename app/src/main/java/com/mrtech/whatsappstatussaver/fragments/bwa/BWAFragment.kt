package com.mrtech.whatsappstatussaver.fragments.bwa

import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mrtech.whatsappstatussaver.R
import com.mrtech.whatsappstatussaver.adapter.ViewPagerWAAdapter
import com.mrtech.whatsappstatussaver.fragments.bwa.BWAImageFragment
import com.mrtech.whatsappstatussaver.fragments.wa.WAVideoFragment

/**
 * A simple [Fragment] subclass.
 */
class BWAFragment : Fragment() {
    var viewPager: ViewPager? = null
    var tabLayout: TabLayout? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_bwa, container, false)
        viewPager = v.findViewById<View>(R.id.viewPager_bwa) as ViewPager
        tabLayout = v.findViewById<View>(R.id.tab_layout_bwa) as TabLayout
        viewPager!!.offscreenPageLimit = 2
        val adapter = ViewPagerWAAdapter(childFragmentManager)
        adapter.addTabs("Images", BWAImageFragment())
        adapter.addTabs("Videos", WAVideoFragment())
        viewPager!!.adapter = adapter
        tabLayout!!.setupWithViewPager(viewPager)
        return v
    }
}