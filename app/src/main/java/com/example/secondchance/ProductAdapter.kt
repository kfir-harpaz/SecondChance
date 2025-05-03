package com.example.secondchance

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.net.Uri
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.secondchance.databinding.ItemProductBinding
import com.google.android.material.card.MaterialCardView

class ProductAdapter(
    private val onItemClick: (Product) -> Unit,
    private val onItemLongClick: (Product) -> Unit
) : ListAdapter<Product, ProductAdapter.ProductViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))

        (holder.itemView as MaterialCardView).setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val scaleUp = AnimationUtils.loadAnimation(view.context, R.anim.scale_up)
                    view.startAnimation(scaleUp)
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val scaleDown = AnimationUtils.loadAnimation(view.context, R.anim.scale_down)
                    view.startAnimation(scaleDown)
                }
            }
            false
        }

        val context = holder.itemView.context
        val displayMetrics = context.resources.displayMetrics
        val isLandscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val layoutParams = holder.itemView.layoutParams
        if (isLandscape) {
            val maxWidthPx = (displayMetrics.density * 300).toInt() // 500dp
            layoutParams.width = maxWidthPx


            if (layoutParams is ViewGroup.MarginLayoutParams) {
                val horizontalMargin = ((displayMetrics.widthPixels - maxWidthPx) / 2)
                layoutParams.marginStart = horizontalMargin
                layoutParams.marginEnd = horizontalMargin
            }
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }
        holder.itemView.layoutParams = layoutParams
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = product.price

            if (!product.imageUri.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(Uri.parse(product.imageUri))
                    .override(200, 200)
                    .centerCrop()
                    .into(binding.ivProductImage)
            } else {
                Glide.with(binding.root.context)
                    .load(product.imageRes)
                    .override(200, 200)
                    .centerCrop()
                    .into(binding.ivProductImage)
            }

            binding.root.setOnClickListener {
                onItemClick(product)
            }

            binding.root.setOnLongClickListener {
                onItemLongClick(product)
                true
            }
        }
    }
}
