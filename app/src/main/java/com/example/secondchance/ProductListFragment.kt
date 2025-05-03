package com.example.secondchance

import android.util.Log
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.secondchance.databinding.FragmentProductListBinding

class ProductListFragment : Fragment((R.layout.fragment_product_list)) {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    private lateinit var productViewModel: ProductViewModel
    private lateinit var sellerAdapter: SellerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
        productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFilterSpinner()
        addDefaultProductsIfNeeded()

        binding.addButton.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment)
        }
    }

    private fun setupFilterSpinner() {
        val options = listOf("הכל", "בתשלום", "למסירה")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterSpinner.adapter = adapter

        binding.filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = options[position]
                filterProducts(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterProducts(filter: String) {
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->
                val filteredSellers = sellers.map { seller ->
                    val filteredProducts = when (filter) {
                        "בתשלום" -> seller.products.filter { it.sellerId == "1" }
                        "למסירה" -> seller.products.filter { it.sellerId == "2" }
                        else -> seller.products
                    }
                    seller.copy(products = filteredProducts)
                }

                sellerAdapter = SellerAdapter(
                    sellers = filteredSellers,
                    onProductClick = { product ->
                        val bundle = Bundle().apply {
                            putParcelable("product", product)
                        }
                        findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment, bundle)
                    },
                    onProductLongClick = { product ->
                        showOptionsDialog(product)
                    }
                )
                binding.rvSellers.layoutManager = LinearLayoutManager(requireContext())
                binding.rvSellers.adapter = sellerAdapter
            }
        }
    }

    private fun addDefaultProductsIfNeeded() {
        productViewModel.productList.observe(viewLifecycleOwner) { products ->
            if (products.isEmpty()) {
                productViewModel.sellerList.observe(viewLifecycleOwner) { sellers ->
                    if (sellers.isNotEmpty()) {
                        val defaultProducts = listOf(
                            Product(
                                name = "Nate Fucking Diaz!",
                                price = "100 ₪",
                                description = "אגדה ב-UFC",
                                imageRes = R.drawable.ic_launcher_background,
                                imageUri = null,
                                sellerId = sellers[0].sellerId
                            ),
                            Product(
                                name = "No des",
                                price = "150 ₪",
                                description = "סתם מוצר מגניב",
                                imageRes = R.drawable.nate,
                                imageUri = null,
                                sellerId = sellers[0].sellerId
                            ),
                            Product(
                                name = "Product 3",
                                price = "200 ₪",
                                description = "עוד מוצר",
                                imageRes = R.drawable.ic_product,
                                imageUri = null,
                                sellerId = sellers[0].sellerId
                            )
                        )

                        productViewModel.addDefaultProducts(defaultProducts)
                    }
                }
            }
        }
    }

    private fun showOptionsDialog(product: Product) {
        val options = arrayOf("ערוך", "מחק")

        AlertDialog.Builder(requireContext())
            .setTitle("בחר פעולה")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val bundle = Bundle().apply {
                            putParcelable("product", product)
                        }
                        findNavController().navigate(
                            R.id.action_productListFragment_to_addEditProductFragment,
                            bundle
                        )
                    }
                    1 -> {
                        AlertDialog.Builder(requireContext())
                            .setMessage("אתה בטוח שברצונך למחוק את המוצר?")
                            .setPositiveButton("מחק") { dialog, _ ->
                                productViewModel.deleteProduct(product)
                                dialog.dismiss()
                            }
                            .setNegativeButton("ביטול") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
