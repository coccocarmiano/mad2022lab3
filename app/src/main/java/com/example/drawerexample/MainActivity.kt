package com.example.drawerexample

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.drawerexample.databinding.ActivityMainBinding
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val userViewModel: UserViewModel by viewModels()
    private val advertisementViewModel: AdvertisementViewModel by viewModels()
    val requestPhotoForProfileEdit = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_show_profile, R.id.nav_adv_list
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestPhotoForProfileEdit) {
            when (resultCode) {
                RESULT_OK -> {
                    // If Bitmap
                    data?.extras?.get("data")?.let {
                        it as Bitmap
                    }?.run {
                        userViewModel.storeProfilePicture(this)
                    }

                    // If URI
                    data?.data?.let {
                        userViewModel.updateProfilePictureFromURI(it)
                    }
                }
                RESULT_CANCELED -> {
                    Snackbar
                        .make(findViewById(R.id.editProfileFragment), "Operation canceled", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Snackbar
                        .make(findViewById(R.id.editProfileFragment), "An unknown error occurred", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}