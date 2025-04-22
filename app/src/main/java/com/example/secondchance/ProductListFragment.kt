package com.example.secondchance

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.example.secondchance.databinding.FragmentProductListBinding

class ProductListFragment : Fragment() {

    private var _binding : FragmentProductListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProductListBinding.inflate(inflater, container, false)
/*

        binding.fab.setOnClickListener{
            findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragmen t)
        }
*/


        binding.addButton.setOnClickListener{
            findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment)
        }


        binding.detailButton.setOnClickListener{
            findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment)
        }


        binding.settingsButton.setOnClickListener{
            findNavController().navigate(R.id.action_productListFragment_to_settingsFragment)
        }


        return binding.root
    }





//    override fun onViewCreated(view: View, savedInstanceState: Bundle?): LinearLayout {
//        super.onViewCreated(view, savedInstanceState)
//        return binding.root
//
//    }
        /*
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)

        // כפתור לעבור למסך הוספה
        val addButton = Button(requireContext()).apply {
            text = "Go to Add"
            setOnClickListener {
                findNavController().navigate(R.id.action_productListFragment_to_addEditProductFragment)
            }
        }

        val detailButton = Button(requireContext()).apply {
            text = "Go to Detail"
            setOnClickListener {
                findNavController().navigate(R.id.action_productListFragment_to_productDetailFragment)
            }
        }

        val settingsButton = Button(requireContext()).apply {
            text = "Go to Settings"
            setOnClickListener {
                findNavController().navigate(R.id.action_productListFragment_to_settingsFragment)
            }
        }

        // הוספת הכפתורים לדף
        (view as LinearLayout).apply {
            addView(addButton)
            addView(detailButton)
            addView(settingsButton)
        }

        return view
    }*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
