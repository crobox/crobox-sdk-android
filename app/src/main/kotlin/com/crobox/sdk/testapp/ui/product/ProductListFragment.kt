package com.crobox.sdk.testapp.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.Product
import com.crobox.sdk.testapp.data.source.ProductDataSource
import com.crobox.sdk.testapp.ui.DemoActivity
import com.crobox.sdk.testapp.ui.adapter.ProductAdapter

class ProductListFragment : Fragment() {

    private lateinit var root: View
    private lateinit var basket: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        root = inflater.inflate(R.layout.fragment_product_list, container, false)
        initView()
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
        (requireActivity() as DemoActivity).getCroboxAPI().testPageViewEvent()
    }

    private fun initView() {
        basket = root.findViewById(R.id.basket)

        val customAdapter = ProductAdapter(ProductDataSource.products)

        val recyclerView: RecyclerView = root.findViewById(R.id.product_list)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = customAdapter

        customAdapter.setOnClickListener(object : ProductAdapter.OnClickListener {
            override fun onClick(position: Int, product: Product) {
                openProductDetails(product)
            }
        })

        val search: SearchView = root.findViewById(R.id.search_bar)
        search.setIconifiedByDefault(false)
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                customAdapter.filter(query ?: "")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                customAdapter.filter(newText ?: "")
                return false
            }
        })
    }

    fun initListener() {
        basket.setOnClickListener {
            (requireActivity() as DemoActivity).showBasket()
        }
    }

    private fun openProductDetails(product: Product) {
        (requireActivity() as DemoActivity).showProductDetails(product)
    }
}