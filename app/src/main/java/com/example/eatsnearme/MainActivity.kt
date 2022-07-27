package com.example.eatsnearme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eatsnearme.login_signup.LoginActivity
import com.example.eatsnearme.profile.ProfileFragment
import com.example.eatsnearme.restaurants.RestaurantsFragment
import com.example.eatsnearme.saved.SavedFragment
import com.parse.ParseUser
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private var restaurantsFragment = RestaurantsFragment()
    private var savedFragment = SavedFragment()

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun bottomNavigation() {
        bottomNavigation.setOnItemSelectedListener {
            val fragment: Fragment = when (it.itemId) {
                R.id.action_restaurants -> restaurantsFragment
                R.id.action_saved -> savedFragment
                R.id.action_profile -> ProfileFragment()
                else -> ProfileFragment()
            }
            fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
            true
        }

        bottomNavigation.selectedItemId = R.id.action_restaurants
    }

}