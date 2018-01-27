package com.github.eis.myandroidapp

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.github.eis.myandroidapp.home.HomeFragment
import com.github.eis.myandroidapp.map.MapsFragment
import kotlinx.android.synthetic.main.activity_bottom_nav.*

class BottomNavActivity : AppCompatActivity() {

    var currentShownFragment: Fragment? = null
    val mapFragment = MapsFragment.newInstance()
    val homeFragment = HomeFragment.newInstance()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                showHomeFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                showMapFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                showHomeFragment()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showMapFragment() {
        showFragment(mapFragment)
    }

    private fun showHomeFragment() {
        showFragment(homeFragment)
    }
    private fun showFragment(frag: Fragment) {
        if (frag.equals(currentShownFragment)) return
        val ft = supportFragmentManager.beginTransaction()
        currentShownFragment?.let { ft.remove(currentShownFragment)}
        ft.add(R.id.container, frag, frag.getTag())
        ft.commit()
        currentShownFragment = frag
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_nav)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
