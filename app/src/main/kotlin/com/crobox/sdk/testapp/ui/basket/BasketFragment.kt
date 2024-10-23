package com.crobox.sdk.testapp.ui.basket

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.source.BasketDataSource
import com.crobox.sdk.testapp.ui.DemoActivity
import com.crobox.sdk.testapp.ui.adapter.BasketAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BasketFragment : BottomSheetDialogFragment() {

    private lateinit var root: View

    lateinit var basketList: RecyclerView
    lateinit var continueToPurchase: View
    lateinit var totalPrice: TextView
    lateinit var customAdapter: BasketAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setDimAmount(0.4f)
            /** Set dim amount here (the dimming factor of the parent fragment) */

            /** IMPORTANT! Here we set transparency to dialog layer */
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(android.R.color.transparent)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_basket, container, false)
        initView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initListener()
        (requireActivity() as DemoActivity).getCroboxAPI().testPageViewEvent()
    }

    fun initView() {
        basketList = root.findViewById(R.id.basket_list)
        continueToPurchase = root.findViewById(R.id.purchase)
        totalPrice = root.findViewById(R.id.total_price)
    }

    fun initData() {
        val dataset = BasketDataSource.items
        Log.d("TEST", "there are ${dataset.size} items in basket")
        customAdapter = BasketAdapter(dataset)

        basketList.layoutManager =
            LinearLayoutManager(basketList.context, LinearLayoutManager.VERTICAL, false)
        basketList.adapter = customAdapter

        customAdapter.setOnClickListener(object : BasketAdapter.OnClickListener {

            override fun increment(position: Int, product: Product) {
                BasketDataSource.increment(product)
                (requireActivity() as DemoActivity).getCroboxAPI().testAddToCartEvent()
                customAdapter.notifyDataSetChanged()
            }

            override fun decrement(position: Int, product: Product) {
                BasketDataSource.decrement(product)
                (requireActivity() as DemoActivity).getCroboxAPI().testRemoveFromCartEvent()
                customAdapter.notifyDataSetChanged()
            }

            override fun delete(position: Int, product: Product) {
                (requireActivity() as DemoActivity).getCroboxAPI().testRemoveFromCartEvent()
                BasketDataSource.delete(product)
            }

            override fun onClick(position: Int, product: Product) {
                (requireActivity() as DemoActivity).showProductDetails(product)
            }

        })
        updateBasket()
    }

    fun updateBasket() {
        val sum = BasketDataSource.items.sumOf { it.product.price * it.quantity }

        totalPrice.text = "$ $sum"
        customAdapter.notifyDataSetChanged()
    }

    fun initListener() {
        continueToPurchase.setOnClickListener {
            (requireActivity() as DemoActivity).getCroboxAPI().testCheckOutEvent()
            dismiss()
            (requireActivity() as DemoActivity).showPurchase()
        }
    }
}