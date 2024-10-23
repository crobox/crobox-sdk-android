package com.crobox.sdk.testapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.PurchaseItem
import com.crobox.sdk.testapp.util.loadImage

class PurchaseAdapter(private val dataSet: List<PurchaseItem>) :
    RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView
        val quantity: TextView
        val priceView: TextView

        val root: View

        init {
            // Define click listener for the ViewHolder's View
            root = view
            nameView = view.findViewById(R.id.product_name)
            quantity = view.findViewById(R.id.quantity)
            priceView = view.findViewById(R.id.product_price)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_purchase, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val purchaseItem = dataSet[position]
        val product = purchaseItem.product

        viewHolder.nameView.text = product.name
        viewHolder.quantity.text = purchaseItem.quantity.toString()
        viewHolder.priceView.text = "$ ${product.price}"
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}