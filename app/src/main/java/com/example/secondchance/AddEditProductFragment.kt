package com.example.secondchance

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.secondchance.databinding.FragmentAddEditProductBinding

class AddEditProductFragment : Fragment() {

    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!


    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.ivProductImage.setImageURI(it)
            binding.ivProductImage.visibility = View.VISIBLE
        }
    }


    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            binding.ivProductImage.setImageBitmap(it)
            binding.ivProductImage.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)


        binding.backToListButton1.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSelectImage.setOnClickListener {
            val options = arrayOf("בחר מהגלריה", "צלם תמונה")
            AlertDialog.Builder(requireContext())
                .setTitle("בחר מקור תמונה")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> galleryLauncher.launch("image/*")
                        1 -> cameraLauncher.launch(null)
                    }
                }
                .show()
        }


        binding.btnSaveProduct.setOnClickListener {
            val name = binding.etProductName.text.toString().trim()
            val description = binding.etProductDescription.text.toString().trim()
            val price = binding.Price.text.toString().trim()


            if (name.isBlank()) {
                binding.etProductName.error = "יש להזין שם מוצר"
                return@setOnClickListener
            }

            val priceValue = price.toDoubleOrNull()
            if (priceValue == null || priceValue <= 0) {
                binding.Price.error = "יש להזין מחיר תקין"
                return@setOnClickListener
            }


            Toast.makeText(requireContext(), "המוצר נשמר בהצלחה", Toast.LENGTH_SHORT).show()


            binding.etProductName.text.clear()
            binding.etProductDescription.text.clear()
            binding.Price.text.clear()
            binding.ivProductImage.setImageDrawable(null)
            binding.ivProductImage.visibility = View.GONE
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
