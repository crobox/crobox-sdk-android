package com.crobox.sdk.testapp.ui.product

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
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.source.BasketDataSource
import com.crobox.sdk.testapp.data.source.ProductDataSource
import com.crobox.sdk.testapp.ui.DemoActivity
import com.crobox.sdk.testapp.ui.adapter.ImageAdapter
import com.crobox.sdk.testapp.ui.adapter.SizeAdapter
import com.crobox.sdk.testapp.util.loadImage

class ProductFragment : Fragment() {

    lateinit var backView: View
    lateinit var nameView: TextView
    lateinit var productImage: ImageView
    lateinit var descriptionView: TextView
    lateinit var priceView: TextView
    lateinit var imageRecyclerView: RecyclerView
    lateinit var sizeRecyclerView: RecyclerView
    lateinit var addToBasketView: View

    private lateinit var root: View

    private lateinit var product: Product

    companion object {
        const val KEY_PRODUCT_ID = "productID"

        fun newInstance(product: Product): ProductFragment {
            val fragment = ProductFragment()
            val args = Bundle()
            args.putInt(KEY_PRODUCT_ID, product.id)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_product, container, false)
        initView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
        (requireActivity() as DemoActivity).getCroboxAPI().testPageViewEvent()
    }

    private fun initView() {
        with(root) {
            backView = findViewById(R.id.back)
            nameView = findViewById(R.id.product_name)
            productImage = findViewById(R.id.product_image)
            descriptionView = findViewById(R.id.product_description)
            priceView = findViewById(R.id.product_price)
            imageRecyclerView = findViewById(R.id.image_alternatives)
            sizeRecyclerView = findViewById(R.id.size_alternatives)
            addToBasketView = findViewById(R.id.add_to_basket)
        }
    }

    private fun initData() {
        product = if (arguments == null) {
            ProductDataSource.products[0]
        } else {
            val productId = requireArguments().getInt(KEY_PRODUCT_ID)
            ProductDataSource.products.first { it.id == productId }
        }

        nameView.text = product.name
        productImage.loadImage(product.images[0])
        descriptionView.text = product.productAdjective
        priceView.text = "$ ${product.price}"

        val dataset = product.images
        val customAdapter = ImageAdapter(dataset)

        imageRecyclerView.layoutManager =
            LinearLayoutManager(imageRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.adapter = customAdapter

        val sizes = ProductDataSource.variants
        val sizeAdapter = SizeAdapter(sizes)
        sizeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 6)
        sizeRecyclerView.adapter = sizeAdapter
    }

    private fun initListener() {
        backView.setOnClickListener {
            (requireActivity() as DemoActivity).onBackPressed()
        }

        addToBasketView.setOnClickListener {
            BasketDataSource.addItem(product)
            (requireActivity() as DemoActivity).getCroboxAPI().testAddToCartEvent()
            (requireActivity() as DemoActivity).addToBasket(product)
        }
    }
}