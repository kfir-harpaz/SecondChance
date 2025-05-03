package com.example.secondchance

import android.R
import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.secondchance.databinding.FragmentAddEditProductBinding
import java.io.File

class AddEditProductFragment : Fragment() {

    private var _binding: FragmentAddEditProductBinding? = null
    private val binding get() = _binding!!

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.ivProductImage.setImageURI(it)
            binding.ivProductImage.visibility = View.VISIBLE
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = saveBitmapAndGetUri(it)
            selectedImageUri = uri
            binding.ivProductImage.setImageURI(uri)
            binding.ivProductImage.visibility = View.VISIBLE
        }
    }

    private var selectedImageUri: Uri? = null
    private lateinit var sellerList: List<Seller>
    private lateinit var selectedSeller: Seller
    private var editingProduct: Product? = null

    private fun saveBitmapAndGetUri(bitmap: Bitmap): Uri? {
        val file = File(requireContext().cacheDir, "${System.currentTimeMillis()}.jpg")
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            file
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val productViewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]

        // מצב עריכה?
        editingProduct = arguments?.getParcelable("product")

        editingProduct?.let { product ->
            binding.etProductName.setText(product.name)
            binding.etProductDescription.setText(product.description)
            val cleanPrice = product.price.replace("₪", "").trim()
            binding.Price.setText(cleanPrice)

            if (!product.imageUri.isNullOrEmpty()) {
                val uri = Uri.parse(product.imageUri)
                selectedImageUri = uri
                binding.ivProductImage.setImageURI(uri)
                binding.ivProductImage.visibility = View.VISIBLE
            }

            binding.titleAddEdit.text = "עריכת מוצר"
        }

        productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->
            sellerList = sellers

            val sellerNames = sellers.map { it.name }
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, sellerNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sellerSpinner.adapter = adapter

            binding.sellerSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                    selectedSeller = sellerList[position]
                }

                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
            }

            editingProduct?.let { product ->
                val index = sellerList.indexOfFirst { it.sellerId == product.sellerId }
                if (index >= 0) binding.sellerSpinner.setSelection(index)
            }
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
            if (priceValue == null || priceValue < 0) {
                binding.Price.error = "יש להזין מחיר תקין"
                return@setOnClickListener
            }

            val priceWithShekel = if (priceValue == 0.0) "" else "$price ₪"

            val newProduct = Product(
                name = name,
                price = priceWithShekel,
                description = description,
                imageUri = selectedImageUri?.toString(),
                sellerId = selectedSeller.sellerId
            ).apply {
                editingProduct?.let { this.id = it.id }
            }

            if (!::selectedSeller.isInitialized) {
                Toast.makeText(requireContext(), "יש לבחור מוכר", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (editingProduct != null) {
                productViewModel.updateProduct(newProduct)
                Toast.makeText(requireContext(), "המוצר עודכן", Toast.LENGTH_SHORT).show()
            } else {
                productViewModel.addProductToSeller(selectedSeller.sellerId, newProduct)
                Toast.makeText(requireContext(), "המוצר נוסף", Toast.LENGTH_SHORT).show()
            }

            findNavController().navigateUp()
        }

        binding.backToListButton1.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
