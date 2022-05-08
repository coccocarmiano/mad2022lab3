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
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.SnackbarContentLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val firebaseAuth = Firebase.auth
    private val userViewModel: UserViewModel by viewModels()
    val requestPhotoForProfileEdit = 1
    val requestGoogleLogin = 2

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
                R.id.nav_show_profile, R.id.nav_adv_list, R.id.loginFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestPhotoForProfileEdit -> {
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