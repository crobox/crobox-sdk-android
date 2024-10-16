package com.crobox.sdk.testapp.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.DataSource
import com.crobox.sdk.testapp.ui.adapter.ImageAdapter
import com.crobox.sdk.testapp.ui.adapter.SizeAdapter
import com.crobox.sdk.testapp.util.loadImage

class ProductActivity : AppCompatActivity() {

    lateinit var nameView: TextView
    lateinit var productImage: ImageView
    lateinit var descriptionView: TextView
    lateinit var priceView: TextView
    lateinit var imageRecyclerView: RecyclerView
    lateinit var sizeRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        initView()
        initData()
    }

    private fun initView() {
        nameView = findViewById(R.id.product_name)
        productImage = findViewById(R.id.product_image)
        descriptionView = findViewById(R.id.product_description)
        priceView = findViewById(R.id.product_price)
        imageRecyclerView = findViewById(R.id.image_alternatives)
        sizeRecyclerView = findViewById(R.id.size_alternatives)
    }

    private fun initData() {
        val product = DataSource().products[0]
        nameView.text = product.name
        productImage.loadImage(product.images[0])
        descriptionView.text = product.productAdjective
        priceView.text = "$ ${product.price}"

        val dataset = product.images
        val customAdapter = ImageAdapter(dataset)

        imageRecyclerView.layoutManager =
            LinearLayoutManager(imageRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.adapter = customAdapter


        val sizeAdapter = SizeAdapter(listOf<String>("39", "40", "41", "42"))
        sizeRecyclerView.layoutManager = GridLayoutManager(this, 4)
        sizeRecyclerView.adapter = sizeAdapter
    }
}