package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import com.dialogapp.dialog.ui.util.autoCleared

class ProfileFragment : Fragment() {

    lateinit var sessionManager: SessionManager

    private var binding by autoCleared<FragmentProfileBinding>()
    private lateinit var dialog: MaterialDialog

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

        dialog = MaterialDialog(this.requireContext())
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.timeline_dest, R.id.mentions_dest,
                R.id.discover_dest, R.id.more_dest))
        binding.toolbarProfile.setupWithNavController(findNavController(), appBarConfiguration)
        val username = ProfileFragmentArgs.fromBundle(arguments).username
        val isSelf = username.equals(sessionManager.user?.username, ignoreCase = true)
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
        binding.viewPagerProfile.offscreenPageLimit = 2
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
                    val text = it
                    binding.includePartialProfile.textProfileAbout.setOnClickListener {
                        dialog.message(text = text).show {
                            positiveButton(text = "Dismiss")
                        }
                    }
                } else {
                    binding.includePartialProfile.textProfileAbout.visibility = View.GONE
                }
            }
        }
    }

    fun isUserCurrentProfile(username: String): Boolean {
        return username.equals(ProfileFragmentArgs.fromBundle(arguments).username, ignoreCase = true)
    }
}