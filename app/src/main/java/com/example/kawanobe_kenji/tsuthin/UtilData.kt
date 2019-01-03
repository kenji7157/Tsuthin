package com.example.kawanobe_kenji.tsuthin

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import org.jsoup.Jsoup
import java.io.IOException

// 共通データクラス
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
        // 一度保存されている情報を削除し新たに保存する
        editor.clear()

        val gson = Gson()
        var json: String
        // 新規追加するelement番号numの設定
        // 保存されている商品情報の数
        //var num = prefs.all.size
        // 商品情報リストサイズが0以外でないとIndexOutOfBoundsExceptionが発生
        if(this.goodsDataList.size != 0) {
            // 0 <= num < this.goodsDataList.size
            for (num in 0 until this.goodsDataList.size) {
                System.out.println("削除機能テスト:"+num+","+this.goodsDataList.size)
                // ローカル保存時にインデックスの値を決定し保存する
                this.goodsDataList[num].index = (num + 1).toString()
                json = gson.toJson(this.goodsDataList[num])
                // putString()にて
                // 第一引数：保存データのキー値　URL情報をキーとする
                // 第二引数：保存データ
                editor.putString("element" + (num + 1).toString(), json)
            }
        }
        // 削除機能の為実装
        else{
            //editor.clear()
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
            System.out.println("保存されている情報:"+prefs2.all.size)
            for (num in 0 until prefs2.all.size) {
                // getString()にて
                // 第一引数：保存データのキー値　element0,element1,...elementN
                // 第二引数：取得できなかった場合の仮値
                val json = prefs2.getString("element" + (num+1).toString(), "")
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

    // 商品情報リストの要素を削除
    fun delGoodsDataList(name: String){
        this.getLocalData()
        // 削除対象の要素を取得する
        var delObject = GoodsData()
        for(e in this.goodsDataList){
            if(e.name == name){
                delObject = e
                System.out.println("削除対象オブジェクトの抽出成功"+delObject.name)
            }
        }
        //System.out.println("サイズ削除前:"+this.goodsDataList.size)
        this.goodsDataList.remove(delObject)
        //System.out.println("サイズ削除後・保存前:"+this.goodsDataList.size)
        this.setLocalData()
        //System.out.println("サイズ保存後:"+this.goodsDataList.size)
    }

}