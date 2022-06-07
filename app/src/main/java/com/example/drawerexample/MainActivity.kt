package com.example.drawerexample

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.navOptions
import com.example.drawerexample.databinding.ActivityMainBinding
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth = Firebase.auth
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var drawerLayout : DrawerLayout
    val requestPhotoForProfileEdit = 1
    val requestGoogleLogin = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_show_profile, R.id.show_skills_list, R.id.nav_adv_myList, R.id.nav_adv_pending, R.id.nav_adv_accepted, R.id.loginFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val smoothAnimations = navOptions {
            anim {
                enter = R.anim.slide_in_left
                exit = R.anim.slide_out_right
                popEnter = R.anim.slide_in_left
                popExit = R.anim.slide_out_right
            }
        }


        firebaseAuth.addAuthStateListener {
            if ( it.currentUser == null ) {
                binding.navView.menu.findItem(R.id.nav_logout).isVisible = false
                binding.navView.menu.findItem(R.id.show_skills_list).isVisible = false
                binding.navView.menu.findItem(R.id.nav_adv_myList).isVisible = false
                binding.navView.menu.findItem(R.id.nav_show_profile).isVisible = false
                binding.navView.menu.findItem(R.id.nav_adv_accepted).isVisible = false
                binding.navView.menu.findItem(R.id.nav_adv_pending).isVisible = false
                binding.navView.menu.findItem(R.id.nav_login).isVisible = true
                navController.navigate(R.id.loginFragment, null, smoothAnimations)
                drawerLayout.close()
            } else {
                binding.navView.menu.findItem(R.id.nav_logout).isVisible = true
                binding.navView.menu.findItem(R.id.nav_adv_myList).isVisible = true
                binding.navView.menu.findItem(R.id.show_skills_list).isVisible = true
                binding.navView.menu.findItem(R.id.nav_adv_accepted).isVisible = true
                binding.navView.menu.findItem(R.id.nav_adv_pending).isVisible = true
                binding.navView.menu.findItem(R.id.nav_show_profile).isVisible = true
                binding.navView.menu.findItem(R.id.nav_login).isVisible = false
            }
        }


        binding.navView.menu
            .findItem(R.id.nav_logout)
            .setOnMenuItemClickListener {
                firebaseAuth.signOut()
                navController.navigate(R.id.loginFragment, null, smoothAnimations)
                drawerLayout.close()
                true
            }


        binding.navView.menu
            .findItem(R.id.nav_adv_myList)
            .setOnMenuItemClickListener {
                val bundle = Bundle().apply { putString("type", "my") }
                navController.navigate(R.id.nav_adv_list, bundle, smoothAnimations)
                drawerLayout.close()
                true
            }

        binding.navView.menu
            .findItem(R.id.nav_adv_pending)
            .setOnMenuItemClickListener {
                val bundle = Bundle().apply { putString("type", "pending") }
                navController.navigate(R.id.nav_adv_list, bundle, smoothAnimations)
                drawerLayout.close()
                true
            }

        binding.navView.menu
            .findItem(R.id.nav_adv_accepted)
            .setOnMenuItemClickListener {
                val bundle = Bundle().apply { putString("type", "accepted") }
                navController.navigate(R.id.nav_adv_list, bundle, smoothAnimations)
                drawerLayout.close()
                true
            }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestPhotoForProfileEdit -> {
                when (resultCode) {
                    RESULT_OK -> {
                        val bmp = when {
                            data?.extras?.get("data") != null -> data.extras?.get("data") as Bitmap                  // If from Camera
                            data?.data != null -> MediaStore.Images.Media.getBitmap(this.contentResolver, data.data) // If from File
                            else -> null
                        }

                        bmp?.also { userViewModel.uploadPhoto(it) }.also { userViewModel.propic.value = it}
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
            requestGoogleLogin -> {
                if (resultCode == RESULT_OK) {
                    val oneTapCredential = Identity.getSignInClient(this)
                    val googleCredential = oneTapCredential.getSignInCredentialFromIntent(data)
                    val googleIDToken = googleCredential.googleIdToken
                    googleIDToken?.run {
                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIDToken, null)
                        firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnSuccessListener {
                                Snackbar.make(binding.root, "User authenticated", Snackbar.LENGTH_SHORT).show()
                                userViewModel.applyChangesToFirebase()
                                findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.show_skills_list)
                            }
                            .addOnFailureListener {
                                Snackbar.make(binding.root, "User authentication failed", Snackbar.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            else -> {
                Snackbar
                    .make(binding.root, "Remember that when calling activities from fragment the random is randomly set sometimes", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }
}