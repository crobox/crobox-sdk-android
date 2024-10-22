package com.crobox.sdk.testapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.source.ProductDataSource
import com.crobox.sdk.testapp.ui.adapter.ProductAdapter

class ProductListFragment : Fragment() {

    private lateinit var root: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_product_list, container, false)
        initView()
        return root
    }

    private fun initView() {
        val customAdapter = ProductAdapter(ProductDataSource().products)

        val recyclerView: RecyclerView = root.findViewById(R.id.product_list)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = customAdapter

        customAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, product: Product) {
                openProductDetails(product)
            }
        })
    }

    private fun openProductDetails(product: Product) {
        (requireActivity() as DemoActivity).showProductDetails(product)
    }
}