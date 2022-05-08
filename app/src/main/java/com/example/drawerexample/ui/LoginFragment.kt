package com.example.drawerexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.drawerexample.MainActivity
import com.example.drawerexample.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val auth = Firebase.auth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

        if (auth.currentUser != null) {
            //TODO: Add navigation login -> adv list
            println(":)")
        }

        //TODO: Change bar text

        return binding.root
    }

    private fun signInWithGoogle() {
        //TODO: Move this to XML?
        val serverClientId = "1003173012370-01id4jr51kdqt07trng2dknpnk8n8c51.apps.googleusercontent.com"
        val oneTapClient = Identity.getSignInClient(requireActivity())
        val signInRequest = BeginSignInRequest
            .builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(serverClientId)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()


        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener {
                @Suppress("DEPRECATION")
                activity?.startIntentSenderForResult(
                    it.pendingIntent.intentSender,
                    (activity as MainActivity).requestGoogleLogin, null, 0, 0, 0,  null)
            }
            .addOnFailureListener {
                Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_LONG).show()
            }


    }
}