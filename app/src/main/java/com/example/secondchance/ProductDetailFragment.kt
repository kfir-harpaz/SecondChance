package com.example.secondchance

import android.app.AlertDialog
import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.secondchance.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getParcelable<Product>("product")

        product?.let {
            binding.tvProductName.text = it.name
            binding.tvProductPrice.text = it.price
            binding.tvProductDescription.text = it.description ?: "אין תיאור"

            val imageSource = if (!it.imageUri.isNullOrEmpty()) {
                Uri.parse(it.imageUri)
            } else {
                it.imageRes
            }

            Glide.with(requireContext())
                .load(imageSource)
                .centerCrop()
                .into(binding.ivProductImage)

            binding.ivProductImage.setOnClickListener {
                showImageFullScreen(imageSource)
            }

            val sellerPhone = when (it.sellerId) {
                "1" -> "050-1111111"
                "2" -> "050-2222222"
                else -> "מספר לא ידוע"
            }

            binding.btnContactSeller.setOnClickListener {
                showPhoneDialog(sellerPhone)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showImageFullScreen(imageSource: Any?) {
        imageSource ?: return

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.window?.attributes?.windowAnimations = android.R.style.Animation_Dialog
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val imageView = dialog.findViewById<ImageView>(R.id.fullscreenImageView)

        Glide.with(requireContext())
            .load(imageSource)
            .into(imageView)

        imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showPhoneDialog(phone: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("מספר טלפון של המוכר")
            .setMessage(phone)
            .setPositiveButton("סגור") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
