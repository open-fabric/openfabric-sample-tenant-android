package co.openfabric.tenant.sample.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.openfabric.tenant.sample.activity.WebViewActivity
import co.openfabric.tenant.sample.service.Partner
import co.openfabric.unilateral.sample.R


class GridAdapter(private val context: Context, private val items: List<Partner>) : RecyclerView.Adapter<GridAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val itemTitleView: TextView = itemView.findViewById(R.id.itemTitleView)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openWebView(itemView.context, items[position])
                }
            }
        }
    }

    private fun openWebView(context: Context, merchant: Partner) {
        val webViewIntent = Intent(context, WebViewActivity::class.java)
        webViewIntent.putExtra(WebViewActivity.INTENT_PARTNER, merchant)
        context.startActivity(webViewIntent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_grid_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        var backgroundColor: Int
        var logo: Int

        when (item.name) {
            "Lazada" -> {
                backgroundColor = R.color.lazada
                logo = R.drawable.lazada
            }
            "Shopee" -> {
                backgroundColor = R.color.shopee
                logo = R.drawable.shopee
            }
            else -> {
                backgroundColor = R.color.item_background_color
                logo = R.drawable.lazada
            }
        }
        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
        holder.itemImageView.setImageResource(logo)
        holder.itemTitleView.text = item.name
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
