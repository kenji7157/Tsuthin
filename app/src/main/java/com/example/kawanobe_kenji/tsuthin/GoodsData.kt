package com.example.kawanobe_kenji.tsuthin

import android.os.Parcel
import android.os.Parcelable

// リスト項目のデータ
// プライマリコンストラクタ -クラス定義と一緒に定義されるコンストラクタ
class GoodsData() : Parcelable {

    // 商品名
    var name = "none"

    // 登録価格
    var beforePrice : String = "0"

    // 最新価格
    var nowPrice : String = "0"

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        beforePrice = parcel.readString()
        nowPrice = parcel.readString()
    }

    // セカンダリコンストラクタ-2つ目以降のコンストラクタ。
    // 必ずプライマリコンストラクタ(:this())を呼ぶ必要がある
    constructor(name: String, beforePrice: String, nowPrice: String) :this() {
        this.name = name
        this.beforePrice = beforePrice
        this.nowPrice = nowPrice
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(beforePrice)
        parcel.writeString(nowPrice)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GoodsData> {
        override fun createFromParcel(parcel: Parcel): GoodsData {
            return GoodsData(parcel)
        }

        override fun newArray(size: Int): Array<GoodsData?> {
            return arrayOfNulls(size)
        }
    }
}