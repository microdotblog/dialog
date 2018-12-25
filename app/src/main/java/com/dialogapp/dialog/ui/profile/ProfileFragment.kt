package com.dialogapp.dialog.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.afollestad.materialdialogs.MaterialDialog
import com.dialogapp.dialog.GlideApp
import com.dialogapp.dialog.R
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.databinding.FragmentProfileBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.ui.util.autoCleared
import com.dialogapp.dialog.workers.FollowWorker

class ProfileFragment : Fragment() {

    lateinit var sessionManager: SessionManager

    private var binding by autoCleared<FragmentProfileBinding>()
    private lateinit var dialog: MaterialDialog
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

        dialog = MaterialDialog(this.requireContext())
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.timeline_dest, R.id.mentions_dest,
                R.id.discover_dest, R.id.more_dest))
        binding.toolbarProfile.setupWithNavController(findNavController(), appBarConfiguration)
        username = ProfileFragmentArgs.fromBundle(arguments!!).username
        isSelf = username.equals(sessionManager.user?.username, ignoreCase = true)
        binding.toolbarProfile.title = username
        setupViewpager(username, isSelf)

        val profileSharedViewModel = ViewModelProviders.of(this).get(ProfileSharedViewModel::class.java)
        profileSharedViewModel.currentProfile = username
        profileSharedViewModel.getEndpointData().observe(viewLifecycleOwner, Observer {
            setEndpointData(it)
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
                        dialog.message(text = text).show {
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
            }
            else {
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
        val tag = "FOL_$username"
        val followRequest = OneTimeWorkRequest.Builder(FollowWorker::class.java)
                .setInputData(FollowWorker.createInputData(username, isFollowing))
                .addTag(tag)
                .build()
        WorkManager.getInstance().enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, followRequest)
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