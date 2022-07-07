package com.example.eatsnearme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.eatsnearme.login.LoginActivity
import com.example.eatsnearme.profile.ProfileFragment
import com.example.eatsnearme.restaurants.RestaurantsFragment
import com.example.eatsnearme.restaurants.RestaurantsViewModel
import com.example.eatsnearme.saved.SavedFragment
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private val restaurantsFragment = RestaurantsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
        val currentUser = ParseUser.getCurrentUser()
        goLoginActivity()
    }

    private fun goLoginActivity() {
        val i = Intent(this, LoginActivity::class.java)
        startActivity(i)
    }

    private fun bottomNavigation() {
        bottomNavigation.setOnItemSelectedListener {
            val fragment: Fragment = when (it.itemId) {
                // not new
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

    companion object {
        const val TAG = "MainActivity"
    }
}