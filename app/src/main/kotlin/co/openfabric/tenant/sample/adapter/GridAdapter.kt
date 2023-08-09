package co.openfabric.tenant.sample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.model.Merchant
import co.openfabric.unilateral.sample.R
import co.openfabric.unilateral.sdk.webview.WebViewHelper


class GridAdapter(private val context: Context, private val items: List<Merchant>) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val selectedItem = items[position]
                    WebViewHelper.openWebViewInApp(context, selectedItem.url)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.imageView.setImageResource(item.logo)
        holder.textViewTitle.text = item.name
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
