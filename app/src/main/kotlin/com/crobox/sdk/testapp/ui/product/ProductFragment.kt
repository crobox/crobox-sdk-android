package com.crobox.sdk.testapp.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.model.PurchaseItem
import com.crobox.sdk.testapp.data.source.BasketDataSource
import com.crobox.sdk.testapp.data.source.ProductDataSource
import com.crobox.sdk.testapp.ui.DemoActivity
import com.crobox.sdk.testapp.ui.adapter.ImageAdapter
import com.crobox.sdk.testapp.ui.adapter.SizeAdapter
import com.crobox.sdk.testapp.util.loadImage

class ProductFragment : Fragment() {

    lateinit var backView: View
    lateinit var nameView: TextView
    lateinit var ratingView: RatingBar
    lateinit var productImage: ImageView
    lateinit var descriptionView: TextView
    lateinit var priceView: TextView
    lateinit var imageRecyclerView: RecyclerView
    lateinit var sizeRecyclerView: RecyclerView
    lateinit var addToBasketView: View
    lateinit var increaseView: View
    lateinit var decreaseView: View
    lateinit var quantityView: TextView

    private lateinit var root: View

    private lateinit var product: Product
    private var quantity = 1

    companion object {
        const val KEY_PRODUCT_ID = "productID"
        const val KEY_PRODUCT_QUANTITY = "productQuantity"

        fun newInstance(product: Product): ProductFragment {
            val fragment = ProductFragment()
            val args = Bundle()
            args.putInt(KEY_PRODUCT_ID, product.id)
            fragment.setArguments(args)
            return fragment
        }

        fun newInstance(item: PurchaseItem): ProductFragment {
            val fragment = ProductFragment()
            val args = Bundle()
            args.putInt(KEY_PRODUCT_ID, item.product.id)
            args.putInt(KEY_PRODUCT_QUANTITY, item.quantity)
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
        (requireActivity() as DemoActivity).getCroboxAPI().pageViewEvent("product-details")
    }

    private fun initView() {
        with(root) {
            backView = findViewById(R.id.back)
            nameView = findViewById(R.id.product_name)
            ratingView = findViewById(R.id.product_rating)
            productImage = findViewById(R.id.product_image)
            descriptionView = findViewById(R.id.product_description)
            priceView = findViewById(R.id.product_price)
            imageRecyclerView = findViewById(R.id.image_alternatives)
            sizeRecyclerView = findViewById(R.id.size_alternatives)
            addToBasketView = findViewById(R.id.add_to_basket)
            quantityView = findViewById(R.id.quantity)
            increaseView = findViewById(R.id.add)
            decreaseView = findViewById(R.id.remove)
        }
    }

    private fun initData() {
        product = if (arguments == null) {
            ProductDataSource.products[0]
        } else {
            val productId = requireArguments().getInt(KEY_PRODUCT_ID)
            quantity = requireArguments().getInt(KEY_PRODUCT_QUANTITY, 1)
            ProductDataSource.products.first { it.id == productId }
        }

        nameView.text = product.name
        ratingView.rating = product.rating.toFloat()
        productImage.loadImage(product.images[0])
        descriptionView.text = product.productAdjective

        val dataset = product.images
        val customAdapter = ImageAdapter(dataset)

        imageRecyclerView.layoutManager =
            LinearLayoutManager(imageRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        imageRecyclerView.adapter = customAdapter

        val sizes = product.variants
        val sizeAdapter = SizeAdapter(sizes)
        sizeRecyclerView.layoutManager = GridLayoutManager(requireContext(), 6)
        sizeRecyclerView.adapter = sizeAdapter

        setProductPrice()
    }

    private fun setProductPrice() {
        if (quantity == 1) {
            decreaseView.visibility = View.INVISIBLE
        } else {
            decreaseView.visibility = View.VISIBLE
        }

        quantityView.text = "$quantity"
        priceView.text = "$ ${product.price * quantity}"
    }

    private fun initListener() {
        backView.setOnClickListener {
            (requireActivity() as DemoActivity).onBackPressed()
        }

        increaseView.setOnClickListener {
            quantity++
            setProductPrice()
        }

        decreaseView.setOnClickListener {
            quantity--
            setProductPrice()
        }

        addToBasketView.setOnClickListener {
            BasketDataSource.addItem(product, quantity)
            val purchaseItem = BasketDataSource.getProduct(product)!!
            (requireActivity() as DemoActivity).getCroboxAPI().addToCartEvent(purchaseItem)
            (requireActivity() as DemoActivity).addToBasket(product)
        }
    }
}