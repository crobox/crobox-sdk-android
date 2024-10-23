package com.crobox.sdk.testapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.PurchaseItem
import com.crobox.sdk.testapp.util.loadImage

class BasketAdapter(private val dataSet: List<PurchaseItem>) :
    RecyclerView.Adapter<BasketAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // Interface for the click listener
    interface OnClickListener {
        fun increment(position: Int, product: Product)
        fun decrement(position: Int, product: Product)
        fun delete(position: Int, product: Product)
        fun onClick(position: Int, product: Product)
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView
        val nameView: TextView
        val increaseView: ImageView
        val quantityView: TextView
        val decreaseView: ImageView
        val deleteView: ImageView
        val priceView: TextView
        val imageRecyclerView: RecyclerView
        val root: View

        init {
            // Define click listener for the ViewHolder's View
            root = view
            productImage = view.findViewById(R.id.product_image)
            nameView = view.findViewById(R.id.product_name)
            increaseView = view.findViewById(R.id.add)
            quantityView = view.findViewById(R.id.quantity)
            decreaseView = view.findViewById(R.id.remove)
            deleteView = view.findViewById(R.id.delete)
            priceView = view.findViewById(R.id.product_price)
            imageRecyclerView = view.findViewById(R.id.image_alternatives)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_basket, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val purchaseItem = dataSet[position]
        val product = purchaseItem.product
        viewHolder.productImage

        if (product.images[0].isNotEmpty()) {
            viewHolder.productImage.loadImage(product.images[0])
        } else {
            viewHolder.productImage.setImageResource(R.mipmap.ic_launcher)
        }

        viewHolder.nameView.text = product.name
        viewHolder.priceView.text = "$ ${product.price}"
        viewHolder.quantityView.text = purchaseItem.quantity.toString()

        viewHolder.increaseView.setOnClickListener {
            onClickListener?.increment(position, product)
        }

        viewHolder.decreaseView.setOnClickListener {
            onClickListener?.decrement(position, product)
        }

        viewHolder.deleteView.setOnClickListener {
            onClickListener?.delete(position, product)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}