package com.example.nsiprojekat.`screens`.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentHomeBinding
import com.example.nsiprojekat.screens.remote.RemoteConfigFragment
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.get
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var remoteConfig: FirebaseRemoteConfig

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        remoteConfig = Firebase.remoteConfig

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        fetchWelcome()

        return root
    }

    private fun fetchWelcome() {


        // [START fetch_config_with_callback]
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                } else {
                }
                val banner = remoteConfig[HomeFragment.BANNER_KEY].asString()
                // [END get_config_values]
                Glide.with(requireContext()).load(banner).into(binding.bannerHome)
            }
        // [END fetch_config_with_callback]
    }

    companion object {
        private const val TAG = "MainActivity"
        // Remote Config keys
        private const val BANNER_KEY = "banner_image"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}