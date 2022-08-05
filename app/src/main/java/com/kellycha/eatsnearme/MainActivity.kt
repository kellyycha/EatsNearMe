package com.kellycha.eatsnearme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kellycha.eatsnearme.R
import com.kellycha.eatsnearme.profile.ProfileFragment
import com.kellycha.eatsnearme.restaurants.RestaurantsFragment
import com.kellycha.eatsnearme.saved.SavedFragment
import kotlinx.android.synthetic.main.activity_main.*

open class MainActivity : AppCompatActivity() {
    private val fragmentManager = supportFragmentManager
    private var restaurantsFragment = RestaurantsFragment()
    private var savedFragment = SavedFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigation()
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