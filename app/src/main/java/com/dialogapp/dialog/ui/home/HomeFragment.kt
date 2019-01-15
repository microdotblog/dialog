package com.dialogapp.dialog.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkInfo
import com.dialogapp.dialog.R
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.databinding.FragmentHomeBinding
import com.dialogapp.dialog.di.Injector
import com.dialogapp.dialog.ui.common.RequestViewModel
import com.dialogapp.dialog.ui.util.autoCleared
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

class HomeFragment : Fragment() {

    lateinit var sessionManager: SessionManager

    private var binding by autoCleared<FragmentHomeBinding>()

    override fun onAttach(context: Context?) {
        sessionManager = Injector.get().sessionManager()
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.isLoggedIn) {
            findNavController().navigate(R.id.action_home_dest_to_login_dest_no_transition)
        }

        val requestViewModel = activity?.run {
            ViewModelProviders.of(this).get(RequestViewModel::class.java)
        }

        requestViewModel?.getReplyWorkInfo()?.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = it[0]
            if (workInfo.state == WorkInfo.State.RUNNING) {
                Timber.i("Sending reply, Tags: %s", workInfo.tags)
                Toast.makeText(this.requireContext(), "Sending", Toast.LENGTH_SHORT).show()
            }

            if (!requestViewModel.replyEvent.consumed && workInfo.state.isFinished) {
                requestViewModel.replyEvent.consume()
                Timber.i("Task Reply complete, Tags: %s State: %s", workInfo.tags, workInfo.state)
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Toast.makeText(this.requireContext(), "Successful", Toast.LENGTH_SHORT).show()
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Snackbar.make(binding.coordinatorLayoutHome, "Could not send reply", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry") {
                                requestViewModel.retryReply()
                            }
                            .show()
                }
            }
        })

        requestViewModel?.getNewPostWorkInfo()?.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = it[0]
            if (workInfo.state == WorkInfo.State.RUNNING) {
                Timber.i("Sending post, Tags: %s", workInfo.tags)
                Toast.makeText(this.requireContext(), "Sending", Toast.LENGTH_SHORT).show()
            }

            if (!requestViewModel.postEvent.consumed && workInfo.state.isFinished) {
                requestViewModel.postEvent.consume()
                Timber.i("Task NewPost complete, Tags: %s State: %s", workInfo.tags, workInfo.state)
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Toast.makeText(this.requireContext(), "Successful", Toast.LENGTH_SHORT).show()
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Snackbar.make(binding.coordinatorLayoutHome, "Could not send post", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry") {
                                requestViewModel.retryPost()
                            }
                            .show()
                }
            }
        })

        requestViewModel?.getDeleteWorkInfo()?.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = it[0]
            if (workInfo.state == WorkInfo.State.RUNNING) {
                Timber.i("Requesting deletion, Tags: %s", workInfo.tags)
                Toast.makeText(this.requireContext(), "Requesting", Toast.LENGTH_SHORT).show()
            }

            if (!requestViewModel.deleteEvent.consumed && workInfo.state.isFinished) {
                requestViewModel.deleteEvent.consume()
                Timber.i("Task Deletion complete, Tags: %s State: %s", workInfo.tags, workInfo.state)
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    Toast.makeText(this.requireContext(), "Request successful", Toast.LENGTH_SHORT).show()
                } else if (workInfo.state == WorkInfo.State.FAILED) {
                    Toast.makeText(this.requireContext(), "Request unsuccessful", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = Navigation.findNavController(view.findViewById(R.id.nav_host_home))
        binding.bottomNavigationHome.setupWithNavController(navController)
    }
}