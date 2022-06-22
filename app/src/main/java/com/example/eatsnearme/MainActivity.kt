package com.example.eatsnearme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eatsnearme.login.LoginActivity
import com.example.eatsnearme.profile.ProfileFragment
import com.example.eatsnearme.restaurants.RestaurantsFragment
import com.example.eatsnearme.saves.SavesFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.ParseUser

class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu: this adds items to the action bar if it is present
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.btnLogout) {
            // compose icon has been selected
            // navigate to the compose activity
            logoutUser()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {
        Log.i(TAG, "attempting to log out")
        ParseUser.logOutInBackground()
        val currentUser = ParseUser.getCurrentUser() // this will now be null
        goLoginActivity()
    }

    private fun goLoginActivity() {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
    }

    private fun bottomNavigation() {
        bottomNavView = findViewById(R.id.bottomNavigation)
        bottomNavView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            val fragment: Fragment
            when (item.itemId) {
                R.id.action_restaurants -> fragment = RestaurantsFragment()
                R.id.action_saves -> fragment = SavesFragment()
                R.id.action_profile -> fragment = ProfileFragment()
                else -> fragment = ProfileFragment()
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
            true
        })
        // Set default selection
        bottomNavView.setSelectedItemId(R.id.action_restaurants)
    }

    companion object {
        const val TAG = "MainActivity"
    }
}