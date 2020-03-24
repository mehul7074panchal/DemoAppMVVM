package com.mehul.appinessdemoapp.view

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.mehul.appinessdemoapp.BR
import com.mehul.appinessdemoapp.R
import com.mehul.appinessdemoapp.databinding.LayoutItemBinding
import com.mehul.appinessdemoapp.model.Item
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList


class ItemAdapter(
    private val ItemList: ArrayList<Item>,
    private val context: Context
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var arraylist: ArrayList<Item> = ArrayList()


    var searchText = ""


    init {
        arraylist.addAll(ItemList)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding: LayoutItemBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.layout_item, parent, false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = ItemList[position]
        holder.bind(item)
        val generator = ColorGenerator.MATERIAL


        var ch = 'T'

        if (item.title != null && item.title!!.isNotEmpty()) {
            ch = item.title!!.elementAt(0)
        }


        val colorRandam = generator.getColor(Character.toUpperCase(ch))

        val drawable = TextDrawable.builder()
            .buildRound(
                Character.toUpperCase(item.title!!.elementAt(0)).toString() + "",
                colorRandam
            )

        holder.itemRowBinding.iv.setImageDrawable(drawable)


        val sName: String = item.title!!.toLowerCase(Locale.getDefault())
        if (sName.contains(searchText)) {
            Log.e("test", "$sName contains: $searchText")
            val startPos = sName.indexOf(searchText)
            val endPos = startPos + searchText.length
            val spanText = Spannable.Factory.getInstance()
                .newSpannable(item.title!!) //
            spanText.setSpan(
                BackgroundColorSpan(Color.GRAY),
                startPos,
                endPos,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            holder.itemRowBinding.tvTitle.setText(spanText, TextView.BufferType.SPANNABLE)
        }
    }

    override fun getItemCount(): Int {
        return ItemList.size
    }

    inner class ViewHolder(var itemRowBinding: LayoutItemBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        fun bind(obj: Any?) {
            itemRowBinding.setVariable(BR.model, obj)
            itemRowBinding.executePendingBindings()
        }

    }

    // Filter Class
    @Throws(ParseException::class)
    fun filter(searchText: String) {
        var searchText = searchText
        searchText = searchText.toLowerCase(Locale.getDefault())
        this.searchText = searchText
        ItemList.clear()
        if (searchText.isEmpty()) {
            ItemList.addAll(arraylist)
        } else {
            for (item in arraylist) {
                if (item.title?.toLowerCase(Locale.getDefault())?.contains(searchText)!!) {
                    ItemList.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }
}