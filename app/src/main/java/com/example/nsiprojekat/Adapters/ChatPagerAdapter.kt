package com.example.nsiprojekat.Adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nsiprojekat.screens.chatList.ChatListFragment
import com.example.nsiprojekat.screens.chatMain.ChatMainFragment
import com.example.nsiprojekat.screens.friendsRequests.FriendsRequestsFragment

class ChatPagerAdapter(private val fa:Fragment): FragmentStateAdapter(fa) {

    private val fragments = listOf<Fragment>(ChatListFragment(),FriendsRequestsFragment())
    public val fragmentNames = listOf<String>("Ćaskanja","Zahtevi za ćaskanja")

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}