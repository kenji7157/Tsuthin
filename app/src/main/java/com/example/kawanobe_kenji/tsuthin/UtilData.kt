package com.example.kawanobe_kenji.tsuthin

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import org.jsoup.Jsoup
import java.io.IOException

class UtilData : Application(){
    // パッケージ名
    private val pacName = "com.example.kawanobe_kenji.tsuthin"
    // 商品情報リスト
    private var goodsDataList : ArrayList<GoodsData> = ArrayList()

    // ローカルデータの保存
    private fun setLocalData(){
        val context = createPackageContext(pacName,Context.CONTEXT_RESTRICTED)
        // 取得した商品情報のみをローカルへ保存する
        // getDefaultSharedPreferencesで作成したSharedPreferencesのモードはデフォルトでMODE_PRIVATE
        // 以下、MODE_PRIVATEの説明
        // getSharedPreferences を呼んだ(= ファイルを作成した) アプリケーションからのみアクセスできる
        // （ただし、同じ user ID を共有しているアプリケーションもアクセスできる）
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        // edit() メソッド :SharedPreferencesのインスタンスから、SharedPreferences.Editor オブジェクトを取得
        val editor = prefs.edit()
        val gson = Gson()
        var json: String
        // 新規追加するelement番号numの設定
        // 保存されている商品情報の数
        //var num = prefs.all.size
        for (e in this.goodsDataList) {
            json = gson.toJson(e)
            // putString()にて
            // 第一引数：保存データのキー値　URL情報をキーとする
            // 第二引数：保存データ
            editor.putString("element"+this.goodsDataList.size.toString(), json)
            //num++
        }
        // データを同期的に書き込み
        editor.commit()
    }

    // ローカルデータの取得
    private fun getLocalData(){
        if(this.goodsDataList.size == 0) {
            val context = createPackageContext(pacName, Context.CONTEXT_RESTRICTED)
            val prefs2 = PreferenceManager.getDefaultSharedPreferences(context)
            // 保存されている商品情報の数だけ繰り返す
            for (num in 1..prefs2.all.size) {
                // getString()にて
                // 第一引数：保存データのキー値　element0,element1,...elementN
                // 第二引数：取得できなかった場合の仮値
                val json = prefs2.getString("element" + num.toString(), "")
                val gson2 = Gson()
                // JSONからJavaオブジェクト(GoodsData)への変換
                val goodsData = gson2.fromJson<GoodsData>(json, GoodsData::class.java!!)
                if (goodsData != null) {
                    // goodsDataListへ要素を追加する
                    this.goodsDataList.add(goodsData)
                }
            }
        }
    }

    // 商品情報リストの更新
    fun renew(renewList :ArrayList<GoodsData>){
        val context = createPackageContext(pacName,Context.CONTEXT_RESTRICTED)
        // （ただし、同じ user ID を共有しているアプリケーションもアクセスできる）
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        // edit() メソッド :SharedPreferencesのインスタンスから、SharedPreferences.Editor オブジェクトを取得
        val editor = prefs.edit()
        editor.clear()
        this.goodsDataList = renewList
        setLocalData()
    }

    // 商品情報リストの取得
    fun getGoodsDataList(): ArrayList<GoodsData>{
        this.getLocalData()
        return this.goodsDataList
    }

    // 商品情報リストへ要素を追加
    fun addGoodsDataList(goodsData: GoodsData){
        this.getLocalData()
        this.goodsDataList.add(goodsData)
        this.setLocalData()
    }
    /*

    fun test(){
        System.out.println("test実行")
    }

    // 商品情報リストに登録されているURLから商品情報を取得し商品情報リストを更新する
    fun getGoods() {
        // ローカルデータの取得
        this.getLocalData()
        /* スクレイピングの実装 */
        var goodsData :GoodsData = GoodsData()
        // 再設定用 商品情報リスト
        var renewList : ArrayList<GoodsData> = ArrayList()
        try {
            for (element in this.goodsDataList) {

                // 指定したURLにGETメソッドでアクセス
                // 結果をパース(テキストのみを取得)し Documentオブジェクトに格納
                val document = Jsoup.connect(element.url).get()
                /*
                // "h1"タグが持つテキスト群に絞る
                var h1List = document.select("h1")
                // "h1"タグが持つテキスト群の要素から商品名を取得する
                for (element in h1List) {
                    // 商品名の取得
                    // "span"タグの"class"属性値が"item-name"のテキスト要素かの判定
                    if ( element.attr("class").equals("item-name") ) {
                        // 商品名の設定
                        goodsData.name = element.text().toString()
                    }
                }

                // "span"タグが持つテキスト群に絞る
                var spanList = document.select("span")
                // "span"タグが持つテキスト群の要素から(登録)価格を取得する
                for (element in spanList) {
                    // (登録)価格の取得
                    // "span"タグの"class"属性値が"item-price bold"のテキスト要素かの判定
                    if ( element.attr("class").equals("item-price bold") ) {
                        // 登録価格の設定
                        goodsData.beforePrice = element.text().toString()
                    }
                }

                // 商品名の設定
                goodsData.nowPrice = "¥ ---"

                // url情報の設定
                goodsData.url = element.url

                renewList.add(goodsData)
                */
            }
            System.out.println("button3を押下")
            Log.i("getGoods実行中", "")
            // 商品情報リストの更新
            if(renewList.size != 0) {
                renew(renewList)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }*/

}