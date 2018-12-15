package com.dialogapp.dialog.ui.common

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dialogapp.dialog.R
import com.dialogapp.dialog.databinding.FragmentImageBinding
import com.google.android.material.snackbar.Snackbar

class ImageViewerFragment : Fragment() {

    lateinit var binding: FragmentImageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_image, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCloseImageViewer.setOnClickListener {
            findNavController().navigateUp()
        }

        val imageUrl = ImageViewerFragmentArgs.fromBundle(arguments).imageUrl
        if (imageUrl == null) {
            Snackbar.make(binding.frameImageViewer, "Invalid image url", Snackbar.LENGTH_SHORT)
                    .show()
        } else {
            Glide.with(this)
                    .load(imageUrl)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.progressbarImageViewer.isIndeterminate = false
                            Snackbar.make(binding.frameImageViewer, "Could not load image", Snackbar.LENGTH_SHORT)
                                    .show()
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.progressbarImageViewer.isIndeterminate = false
                            binding.progressbarImageViewer.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.photoview)
        }
    }
}