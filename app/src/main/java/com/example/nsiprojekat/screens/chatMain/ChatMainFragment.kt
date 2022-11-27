package com.example.nsiprojekat.screens.chatMain

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nsiprojekat.Adapters.ChatPagerAdapter
import com.example.nsiprojekat.R
import com.example.nsiprojekat.databinding.FragmentChatMainBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ChatMainFragment : Fragment() {

    private var _binding: FragmentChatMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChatMainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatMainBinding.inflate(inflater,container,false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = binding.viewPager
        val pagerAdapter = ChatPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        viewPager.isUserInputEnabled=false

        val tabLayout = binding.tabs
        TabLayoutMediator(tabLayout,viewPager){ tab:TabLayout.Tab, i:Int->
            tab.text=pagerAdapter.fragmentNames[i]
        }.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}