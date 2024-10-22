package com.crobox.sdk.testapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.source.ProductDataSource
import com.crobox.sdk.testapp.ui.adapter.ImageAdapter
import com.crobox.sdk.testapp.ui.adapter.SizeAdapter
import com.crobox.sdk.testapp.util.loadImage

class ProductFragment : Fragment() {

    lateinit var nameView: TextView
    lateinit var productImage: ImageView
    lateinit var descriptionView: TextView
    lateinit var priceView: TextView
    lateinit var imageRecyclerView: RecyclerView
    lateinit var sizeRecyclerView: RecyclerView

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.activity_product, container, false)
        initView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    private fun initView() {
        with(root) {
            nameView = findViewById(R.id.product_name)
            productImage = findViewById(R.id.product_image)
            descriptionView = findViewById(R.id.product_description)
            priceView = findViewById(R.id.product_price)
            imageRecyclerView = findViewById(R.id.image_alternatives)
            sizeRecyclerView = findViewById(R.id.size_alternatives)
        }
    }

    private fun initData() {
        val product = ProductDataSource().products[0]
        nameView.text = product.name
        productImage.loadImage(product.images[0])
        descriptionView.text = product.productAdjective
        priceView.text = "$ ${product.price}"

        val dataset = product.images
        val customAdapter = ImageAdapter(dataset)

        imageRecyclerView.layoutManager =
            LinearLayoutManager(imageRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.adapter = customAdapter

        val sizes = ProductDataSource().variants
        val sizeAdapter = SizeAdapter(sizes)
        sizeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        sizeRecyclerView.adapter = sizeAdapter
    }
}