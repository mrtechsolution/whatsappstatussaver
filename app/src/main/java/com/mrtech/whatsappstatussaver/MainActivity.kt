package com.mrtech.whatsappstatussaver

import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import android.os.Bundle
import com.mrtech.whatsappstatussaver.R
import hotchemi.android.rate.AppRate
import hotchemi.android.rate.OnClickButtonListener
import com.mrtech.whatsappstatussaver.MainActivity
import android.content.Intent
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import com.mrtech.whatsappstatussaver.fragments.wa.WAFragment
import androidx.core.view.GravityCompat
import android.content.DialogInterface
import com.mrtech.whatsappstatussaver.fragments.bwa.BWAFragment
import android.widget.Toast
import android.os.Environment
import android.content.pm.PackageManager
import android.app.ActivityManager
import android.app.AlertDialog
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import java.io.File

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppRate.with(this)
            .setInstallDays(0) // default 10, 0 means install day.
            .setLaunchTimes(5) // default 10
            .setRemindInterval(10) // default 1
            .setShowLaterButton(true) // default true
            .setDebug(false) // default false
            .setOnClickButtonListener { which ->
                // callback listener.
                Log.d(MainActivity::class.java.name, Integer.toString(which))
            }
            .monitor()

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this)
        try {
            if (!isMyServiceRunning(Class.forName("com.mrtech.whatsappstatussaver.service.NotificationService"))) {
                try {
                    startService(
                        Intent(
                            this,
                            Class.forName("com.mrtech.whatsappstatussaver.service.NotificationService")
                        )
                    )
                } catch (e: Throwable) {
                    throw NoClassDefFoundError(e.message)
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        stash()
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)
        if (savedInstanceState == null) {
            navigationView.menu.getItem(0).isChecked = true
            val fragment: Fragment = WAFragment()
            val fm = supportFragmentManager
            fm.beginTransaction().replace(R.id.framelayout, fragment).commit()
        }
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
//        if (id == R.id.action_share) {
//            val intent = Intent("android.intent.action.SEND")
//            intent.type = "text/plain"
//            intent.putExtra("android.intent.extra.SUBJECT", "Share WApp Status Saver App")
//            intent.putExtra(
//                "android.intent.extra.TEXT",
//                "Try this Awesome App 'WhatsApp Status Saver' which helps you in Saving all the WhatsApp Statuses ..! \nhttps://play.google.com/store/apps/details?id=com.mrtech.whatsappstatussaver"
//            )
//            startActivity(Intent.createChooser(intent, "Share Via"))
//            return true
//        }
        if (id == R.id.action_help) {
            val inflate = LayoutInflater.from(this).inflate(R.layout.tut, null as ViewGroup?)
            val builder = AlertDialog.Builder(this)
            builder.setView(inflate).setTitle("How To Use This app?")
                .setPositiveButton("Ok!") { dialogInterface, i -> dialogInterface.dismiss() }
            builder.create().show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        if (id == R.id.nav_whatsapp) {
            val fragment: Fragment = WAFragment()
            val fm = supportFragmentManager
            fm.beginTransaction().replace(R.id.framelayout, fragment).commit()
        } else if (id == R.id.nav_business) {
            if (checkInstallation("com.whatsapp.w4b")) {
                val fragment: Fragment = BWAFragment()
                val fm = supportFragmentManager
                fm.beginTransaction().replace(R.id.framelayout, fragment).commit()
            } else {
                Toast.makeText(this, "Business Whatsapp Not Installed", Toast.LENGTH_SHORT).show()
            }
        } else if(id == R.id.menu_share) {
            val intent = Intent("android.intent.action.SEND")
            intent.type = "text/plain"
            intent.putExtra("android.intent.extra.SUBJECT", "Share Whatsapp Status Saver App")
            intent.putExtra(
                "android.intent.extra.TEXT",
                "Try this Awesome App 'WhatsApp Status Saver' which helps you in Saving all the WhatsApp Statuses ..! \nhttps://play.google.com/store/apps/details?id=com.mrtech.whatsappstatussaver"
            )
            startActivity(Intent.createChooser(intent, "Share Via"))
        }
        else {
            AppRate.with(this).showRateDialog(this)
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun stash() {
        var file = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
        )

//        File file = new File(new StringBuffer().append(new StringBuffer().append(Environment.getExternalStorageDirectory()).append(File.separator).toString()).append("WhatsApp/Media/.Statuses").toString());
        if (!file.isDirectory) {
            file.mkdirs()
        }
        file = File(
            StringBuffer().append(
                StringBuffer().append(Environment.getExternalStorageDirectory()).append(
                    File.separator
                ).toString()
            ).append("WhatsApp Business/Media/.Statuses").toString()
        )
        if (!file.isDirectory) {
            file.mkdirs()
        }
        file = File(
            StringBuffer().append(
                StringBuffer().append(Environment.getExternalStorageDirectory()).append(
                    File.separator
                ).toString()
            ).append("GBWhatsApp/Media/.Statuses").toString()
        )
        if (!file.isDirectory) {
            file.mkdirs()
        }
        Log.i("isDirectory", "==>" + file.mkdirs())
        Log.i("isFile", "==>" + file.isFile)
        Log.i("isAbsolute", "==>" + file.isAbsolute)
        Log.i("isHidden", "==>" + file.isHidden)
        val newFile = File(
            StringBuffer().append(
                StringBuffer().append(Environment.getExternalStorageDirectory()).append(
                    File.separator
                ).toString()
            ).append("StorySaver/").toString()
        ).mkdirs()
        Log.i("newFile", "==>$newFile")
    }

    private fun checkInstallation(uri: String): Boolean {
        val pm = packageManager
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    private fun isMyServiceRunning(cls: Class<*>): Boolean {
        for (runningServiceInfo in (getSystemService(ACTIVITY_SERVICE) as ActivityManager).getRunningServices(
            Int.MAX_VALUE
        )) {
            if (cls.name == runningServiceInfo.service.className) {
                return true
            }
        }
        return false
    }
}