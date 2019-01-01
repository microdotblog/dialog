package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.databinding.FragmentProfileBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.ui.common.BottomSheetProfile
import com.dialogapp.dialog.ui.common.RequestViewModel
import com.dialogapp.dialog.ui.util.autoCleared

class ProfileFragment : Fragment() {

    lateinit var sessionManager: SessionManager

    private var binding by autoCleared<FragmentProfileBinding>()
    private var isSelf: Boolean = false
    private lateinit var username: String

    override fun onAttach(context: Context?) {
        sessionManager = Injector.get().sessionManager()
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Remove workaround after proper support by navigation component
        if (arguments != null) {
            // We came from a post
            username = ProfileFragmentArgs.fromBundle(arguments!!).username
        } else {
            // We came from bottom nav
            username = sessionManager.user?.username!!
            binding.toolbarProfile.inflateMenu(R.menu.profile_toolbar_options_menu)
            binding.toolbarProfile.menu.getItem(0).setOnMenuItemClickListener {
                val navOptions = NavOptions.Builder()
                        .setEnterAnim(R.anim.slide_in_bottom)
                        .setExitAnim(R.anim.nav_default_exit_anim)
                        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
                        .setPopExitAnim(R.anim.slide_out_bottom)
                        .build()
                val mainNavController = activity?.findNavController(R.id.nav_host_main)
                mainNavController?.navigate(R.id.new_post_dest, bundleOf("isReply" to false), navOptions)
                true
            }
            binding.toolbarProfile.menu.getItem(1).setOnMenuItemClickListener {
                val bottomSheetProfile = BottomSheetProfile()
                bottomSheetProfile.show(childFragmentManager, "bottom_sheet_profile")
                true
            }
        }
        isSelf = username.equals(sessionManager.user?.username, ignoreCase = true)

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.timeline_dest, R.id.mentions_dest,
                R.id.discover_dest, R.id.profile_self_dest))
        binding.toolbarProfile.setupWithNavController(findNavController(), appBarConfiguration)
        binding.toolbarProfile.title = username
        setupViewpager(username, isSelf)

        val profileSharedViewModel = ViewModelProviders.of(this).get(ProfileSharedViewModel::class.java)
        profileSharedViewModel.currentProfile = username
        profileSharedViewModel.getEndpointData().observe(viewLifecycleOwner, Observer {
            setEndpointData(it)
        })
        profileSharedViewModel.getEndpointData().observe(viewLifecycleOwner, Observer {
            setEndpointData(it)
        })
        profileSharedViewModel.getLogout().observe(viewLifecycleOwner, Observer {
            if (it) {
                sessionManager.logout()
            }
        })
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

    private fun setEndpointData(endpointData: EndpointData?) {
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
                    val text = it
                    binding.includePartialProfile.textProfileAbout.setOnClickListener {
                        MaterialDialog(this.requireContext()).message(text = text).show {
                            positiveButton(text = "Dismiss")
                        }
                    }
                } else {
                    binding.includePartialProfile.textProfileAbout.visibility = View.GONE
                }
            }

            if (isSelf) {
                binding.includePartialProfile.buttonFollowing.visibility = View.GONE
                binding.includePartialProfile.buttonFollow.visibility = View.GONE
            } else {
                endpointData.microblog?.is_following.let {
                    when (it) {
                        true -> {
                            hideFollowButton()
                        }
                        false -> {
                            hideFollowingButton()
                        }
                    }
                }

                binding.includePartialProfile.buttonFollow.setOnClickListener {
                    enqueueFollowWorker(endpointData.microblog?.is_following!!)
                }

                binding.includePartialProfile.buttonFollowing.setOnClickListener {
                    enqueueFollowWorker(endpointData.microblog?.is_following!!)
                }
            }
        }
    }

    private fun enqueueFollowWorker(isFollowing: Boolean) {
        val requestViewModel = activity?.run {
            ViewModelProviders.of(this).get(RequestViewModel::class.java)
        }
        requestViewModel?.sendFollowRequest(username, isFollowing)
    }

    private fun hideFollowButton() {
        binding.includePartialProfile.buttonFollow.visibility = View.INVISIBLE
        binding.includePartialProfile.buttonFollowing.visibility = View.VISIBLE
    }

    private fun hideFollowingButton() {
        binding.includePartialProfile.buttonFollowing.visibility = View.INVISIBLE
        binding.includePartialProfile.buttonFollow.visibility = View.VISIBLE
    }
}