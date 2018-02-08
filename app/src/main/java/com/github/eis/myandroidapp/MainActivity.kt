package com.github.eis.myandroidapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.github.eis.myandroidapp.home.HomeFragment
import com.github.eis.myandroidapp.map.MapsActivity
import com.github.eis.myandroidapp.slide.ScreenSlidePagerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private var currentShownFragment: Fragment? = null
    private val homeFragment = HomeFragment.newInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")

        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        Log.d(TAG, "onCreate done")
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                showFragment(homeFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {

                startActivity(Intent(this, MapsActivity::class.java))

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {

                startActivity(Intent(this, ScreenSlidePagerActivity::class.java))

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showFragment(frag: Fragment) {
        Log.d(TAG, "showFragment")
        if (frag.equals(currentShownFragment)) return
        val ft = supportFragmentManager.beginTransaction()
        currentShownFragment?.let { ft.remove(currentShownFragment)}

        Log.d(TAG, "showFragment: add " + frag.tag)
        ft.add(R.id.container, frag, frag.tag)
        ft.commit()
        currentShownFragment = frag
        Log.d(TAG, "showFragment done")
    }


}
