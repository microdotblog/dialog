package com.dialogapp.dialog.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentProfileBinding
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.ui.util.autoCleared

class ProfileFragment : Fragment() {

    private var binding by autoCleared<FragmentProfileBinding>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.timeline_dest, R.id.mentions_dest,
                R.id.discover_dest, R.id.more_dest))
        binding.toolbarProfile.setupWithNavController(findNavController(), appBarConfiguration)
        val username = ProfileFragmentArgs.fromBundle(arguments).username
        val isSelf = ProfileFragmentArgs.fromBundle(arguments).isSelf
        binding.toolbarProfile.title = username
        setupViewpager(username, isSelf)
    }

    private fun setupViewpager(username: String, isSelf: Boolean) {
        val adapter = if (isSelf) {
            ProfileSelfFragmentPagerAdapter(childFragmentManager, username)
        } else {
            ProfileFragmentPagerAdapter(childFragmentManager, username)
        }
        binding.viewPagerProfile.adapter = adapter
        binding.tabLayoutProfile.setupWithViewPager(binding.viewPagerProfile)
    }

    fun setEndpointData(endpointData: EndpointData?) {
        if (endpointData != null) {
            binding.includePartialProfile.progressBar.visibility = View.GONE
            binding.includePartialProfile.textProfileFullname.visibility = View.VISIBLE

            GlideApp.with(this)
                    .load(endpointData.author?.avatar)
                    .dontAnimate()
                    .into(binding.includePartialProfile.imageAvatar)
            binding.includePartialProfile.textProfileFullname.text = endpointData.author?.name
            endpointData.author?.url.let {
                if (it != null && !it.isEmpty()) {
                    binding.includePartialProfile.textProfileWebsite.visibility = View.VISIBLE
                    binding.includePartialProfile.textProfileWebsite.text = it
                } else {
                    binding.includePartialProfile.textProfileWebsite.visibility = View.GONE
                }
            }
            endpointData.microblog?.bio.let {
                if (it != null && !it.isEmpty()) {
                    binding.includePartialProfile.textProfileAbout.visibility = View.VISIBLE
                    binding.includePartialProfile.textProfileAbout.text = it
                } else {
                    binding.includePartialProfile.textProfileAbout.visibility = View.GONE
                }
            }
        }
    }
}