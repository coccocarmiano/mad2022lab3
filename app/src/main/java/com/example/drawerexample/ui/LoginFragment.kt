package com.example.drawerexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.drawerexample.MainActivity
import com.example.drawerexample.R
import com.example.drawerexample.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.googleLoginButton.setOnClickListener {
            signInWithGoogle()
        }

        return binding.root
    }

    private fun signInWithGoogle() {
        val serverClientId = getString(R.string.google_server_client_id)
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
                when (it) {
                    is ApiException -> {
                        Snackbar.make(binding.root, R.string.no_google_account_on_device, Snackbar.LENGTH_LONG).show()
                    }
                    else -> {
                        Snackbar.make(binding.root, it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                }
            }


    }
}