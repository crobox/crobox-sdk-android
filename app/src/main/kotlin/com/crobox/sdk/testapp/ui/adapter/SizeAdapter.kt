package com.crobox.sdk.testapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crobox.sdk.testapp.R
import com.crobox.sdk.testapp.data.model.Variant

class SizeAdapter(private val dataSet: List<Variant>) :
    RecyclerView.Adapter<SizeAdapter.ViewHolder>() {

        var selectedPosition = -1

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            // Define click listener for the ViewHolder's View
            textView = view.findViewById(R.id.item_size)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_size, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val variant = dataSet[position]
        with(viewHolder.textView) {
            text = variant.name
            paint.isStrikeThruText = !variant.available
            if (variant.available) {
                if(selectedPosition == position) {
                    setBackgroundResource(R.drawable.size_background_selected)
                }else {
                    setBackgroundResource(R.drawable.size_background)
                }
            } else {
                setBackgroundResource(R.drawable.size_background_disabled)
            }

            setOnClickListener {
                if(variant.available) {
                    selectedPosition = position
                    notifyDataSetChanged()
                }
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}