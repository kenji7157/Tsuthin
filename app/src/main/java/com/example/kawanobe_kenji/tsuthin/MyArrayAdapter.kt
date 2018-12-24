package com.example.kawanobe_kenji.tsuthin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

// 自作のリスト項目データを扱えるようにした ArrayAdapter
class MyArrayAdapter : ArrayAdapter<GoodsData> {

    private var inflater : LayoutInflater? = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?

    constructor(context : Context, resource : Int) : super(context, resource) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var viewHolder : ViewHolder? = null
        var view = convertView

        // 再利用の設定
        if (view == null) {

            view = inflater!!.inflate(R.layout.list_item, parent, false)

            viewHolder = ViewHolder(
                    view.findViewById(R.id.name),
                    view.findViewById(R.id.beforePrice),
                    view.findViewById(R.id.nowPrice)
            )
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }

        // 項目の情報を設定
        val listItem = getItem(position)
        viewHolder.nameView.text = listItem.name
        viewHolder.beforePriceView.text = listItem.beforePrice.toString()
        viewHolder.nowPriceView.text = listItem.nowPrice.toString()

        return view!!
    }
}