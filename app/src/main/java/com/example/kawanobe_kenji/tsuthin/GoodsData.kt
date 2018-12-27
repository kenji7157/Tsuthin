package com.example.kawanobe_kenji.tsuthin

// リスト項目のデータ
// プライマリコンストラクタ -クラス定義と一緒に定義されるコンストラクタ
class GoodsData()  {

    // 商品名
    var name : String = "none"

    // 登録価格
    var beforePrice : String = "¥---"

    // 最新価格
    var nowPrice : String = "¥---"

    // URL情報
    var url : String = "none"

    //　通知設定フラグ true/false 通知発動ON/通知発動OFF
    var notifyFlag : Boolean = false

    // セカンダリコンストラクタ-2つ目以降のコンストラクタ。
    // 必ずプライマリコンストラクタ(:this())を呼ぶ必要がある
    constructor(name: String, beforePrice: String, nowPrice: String, url: String, notifyFlag: Boolean) :this() {
        this.name = name
        this.beforePrice = beforePrice
        this.nowPrice = nowPrice
        this.url = url
        this.notifyFlag = notifyFlag
    }
}