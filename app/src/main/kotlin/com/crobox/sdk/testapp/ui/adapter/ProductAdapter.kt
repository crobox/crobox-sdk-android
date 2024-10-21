package com.crobox.sdk.testapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.util.loadImage

class ProductAdapter(private val dataSet: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // Interface for the click listener
    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView
        val nameView: TextView
        val descriptionView: TextView
        val priceView: TextView
        val imageRecyclerView: RecyclerView
        val root: View

        init {
            // Define click listener for the ViewHolder's View
            root = view
            productImage = view.findViewById(R.id.product_image)
            nameView = view.findViewById(R.id.product_name)
            descriptionView = view.findViewById(R.id.product_description)
            priceView = view.findViewById(R.id.product_price)
            imageRecyclerView = view.findViewById(R.id.image_alternatives)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_product, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val product = dataSet[position]
        viewHolder.productImage

        if (product.images[0].isNotEmpty()) {
            viewHolder.productImage.loadImage(product.images[0])
        } else {
            viewHolder.productImage.setImageResource(R.mipmap.ic_launcher)
        }

        viewHolder.nameView.text = product.name
        viewHolder.descriptionView.text = product.productAdjective
        viewHolder.priceView.text = "$ ${product.price}"

        val dataset = product.images
        val customAdapter = ImageAdapter(dataset)

        viewHolder.imageRecyclerView.layoutManager =
            LinearLayoutManager(viewHolder.imageRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        viewHolder.imageRecyclerView.adapter = customAdapter
        viewHolder.root.setOnClickListener {
            onClickListener?.onClick(position, product)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}