package com.example.eatsnearme

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eatsnearme.login.LoginActivity
import com.example.eatsnearme.profile.ProfileFragment
import com.example.eatsnearme.restaurants.RestaurantsFragment
import com.example.eatsnearme.saved.SavedFragment
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private var restaurantsFragment = RestaurantsFragment()

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        if (ParseUser.getCurrentUser() == null){
//            Log.i(TAG, "current user is null")
//            goLoginActivity()
//            return
//        }

        bottomNavigation()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnLogout) {
            logoutUser()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {
        Log.i(TAG, "attempting to log out")
        ParseUser.logOutInBackground()
        goLoginActivity()
    }

    private fun goLoginActivity() {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
    }

    private fun bottomNavigation() {
        bottomNavigation.setOnItemSelectedListener {
            val fragment: Fragment = when (it.itemId) {
                R.id.action_restaurants -> restaurantsFragment
                R.id.action_saved -> SavedFragment()
                R.id.action_profile -> ProfileFragment()
                else -> ProfileFragment()
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
            true
        }
        // Set default selection
        bottomNavigation.selectedItemId = R.id.action_restaurants
    }

}