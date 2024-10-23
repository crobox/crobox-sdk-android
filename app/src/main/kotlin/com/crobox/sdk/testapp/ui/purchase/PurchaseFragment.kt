package com.crobox.sdk.testapp.ui.purchase

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.source.BasketDataSource
import com.crobox.sdk.testapp.ui.DemoActivity
import com.crobox.sdk.testapp.ui.adapter.PurchaseAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PurchaseFragment : BottomSheetDialogFragment() {

    private lateinit var root: View

    lateinit var basketList: RecyclerView
    lateinit var totalPrice: TextView
    lateinit var purchase: View

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
        root = inflater.inflate(R.layout.fragment_purchase, container, false)
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
        totalPrice = root.findViewById(R.id.total_price)
        purchase = root.findViewById(R.id.purchase)
    }

    fun initData() {
        val dataset = BasketDataSource.items
        Log.d("TEST", "there are ${dataset.size} items in basket")
        val customAdapter = PurchaseAdapter(dataset)

        basketList.layoutManager =
            LinearLayoutManager(basketList.context, LinearLayoutManager.VERTICAL, false)
        basketList.adapter = customAdapter

        val sum = dataset.sumOf { it.product.price * it.quantity }
        totalPrice.text = "$ $sum"
    }

    fun initListener() {
        purchase.setOnClickListener {
            (requireActivity() as DemoActivity).getCroboxAPI().testPurchaseEvent()
            BasketDataSource.clearAll()
            Toast.makeText(requireContext(), "Purchase completed!", Toast.LENGTH_LONG)
            dismiss()
        }
    }
}