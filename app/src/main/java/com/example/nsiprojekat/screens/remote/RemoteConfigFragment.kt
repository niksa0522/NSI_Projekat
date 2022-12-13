package com.example.nsiprojekat.screens.remote

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentChatWithFriendBinding
import com.example.nsiprojekat.databinding.FragmentRemoteConfigBinding
import com.example.nsiprojekat.screens.chatList.ChatListViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class RemoteConfigFragment : Fragment() {

    private val viewModel: RemoteConfigViewModel by viewModels()
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var _binding: FragmentRemoteConfigBinding?= null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRemoteConfigBinding.inflate(inflater,container,false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fetchButton.setOnClickListener { fetchWelcome() }
        remoteConfig = Firebase.remoteConfig
//        remoteConfig = Firebase.remoteConfig
//
//        val configSettings = remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 1
//        }
//        remoteConfig.setConfigSettingsAsync(configSettings)
//
//        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        fetchWelcome()
    }

    private fun fetchWelcome() {
        binding.welcomeTextView.text = remoteConfig[LOADING_PHRASE_CONFIG_KEY].asString()

        // [START fetch_config_with_callback]
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(context, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Fetch failed",
                        Toast.LENGTH_SHORT).show()
                }
                displayWelcomeMessage()
            }
        // [END fetch_config_with_callback]
    }

    /**
     * Display a welcome message in all caps if welcome_message_caps is set to true. Otherwise,
     * display a welcome message as fetched from welcome_message.
     */
    private fun displayWelcomeMessage() {

        // [START get_config_values]
        val welcomeMessage = remoteConfig[WELCOME_MESSAGE_KEY].asString()
        // [END get_config_values]
        binding.welcomeTextView.isAllCaps = remoteConfig[WELCOME_MESSAGE_CAPS_KEY].asBoolean()
        binding.welcomeTextView.text = welcomeMessage
    }

    companion object {
        private const val TAG = "MainActivity"
        // Remote Config keys
        private const val LOADING_PHRASE_CONFIG_KEY = "loading_phrase"
        private const val WELCOME_MESSAGE_KEY = "welcome_message"
        private const val WELCOME_MESSAGE_CAPS_KEY = "welcome_message_caps"
    }


}