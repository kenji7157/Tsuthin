package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import org.jsoup.Jsoup
import java.io.IOException
import android.R.id.edit
import android.preference.PreferenceManager
import android.content.SharedPreferences
import com.google.gson.Gson


// AsyncTaskクラスを継承したMyAsyncTaskをMainActivityクラス内に記述
// AsyncTask<String,String,GoodsData>
// 第一引数は処理の実行時に渡すパラメータ -> MainActivity
// 第二引数はonProgressUpdateに渡す引数のパラメータ -> Void
// 第三引数はonPostExecuteに渡す引数のパラメータ -> GoodsData
class GoodsAsyncTask: AsyncTask<MainActivity, Void, GoodsData>() {

    private var activity: MainActivity = MainActivity()

    // doInBackground()は非同期で行いたい処理の内容 返戻値はonPostExecute()の引数となる
    override fun doInBackground(vararg list: MainActivity): GoodsData? {
        //System.out.println("doInBackground")
        // レイアウト上のテキストビュー:urlTextオブジェクトを変数urTextに格納
        //val urlText = findViewById<TextView>(R.id.urlText)
        // 引数からurl情報を取得
        activity = list[0]
        //　返戻値はonPostExecute()の引数となる
        return getGoods(activity.getUrl())
    }

    // onPostExecute()はdoIMnBackground()終了後にメインスレッドで実行される処理の内容
    override fun onPostExecute(result: GoodsData) {
        super.onPostExecute(result)
        //System.out.println("onPostExecute")
        // レイアウト上のテキストビュー:mytextオブジェクトを変数textviewに格納
        //val texview = findViewById<TextView>(R.id.priceText)
        //
        //texview.setText(result.beforePrice)
        //System.out.println(result?.substring(2))
        //arrayAdapter?.add(GoodsData(result.name, result.beforePrice,"¥ zzz"))

        // MainActivityのgoodsDataListへ要素を追加
        activity.addGoodsDataList(GoodsData(result.name, result.beforePrice,"¥ zzz"))

        // この時点でローカルにも保存したい
        // arraylistを保存する
        // getDefaultSharedPreferencesで作成したSharedPreferencesのモードはデフォルトでMODE_PRIVATE
        // 以下、MODE_PRIVATEの説明
        // getSharedPreferences を呼んだ(= ファイルを作成した) アプリケーションからのみアクセスできる
        // （ただし、同じ user ID を共有しているアプリケーションもアクセスできる）
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.activity)
        // edit() メソッド :SharedPreferencesのインスタンスから、SharedPreferences.Editor オブジェクトを取得
        val editor = prefs.edit()
        val gson = Gson()
        var num :Int = 0
        for (e in activity.getGoodsDataList()) {
            // Javaオブジェクト(GoodsData)からJSONへの変換
            val json = gson.toJson(e)
            // putString()にて
            // 第一引数：保存データのキー値　element0,element1,...elementN
            // 第二引数：保存データ
            editor.putString("element"+num.toString(), json)
            System.out.println("test2:"+"element"+num.toString())
            System.out.println("test3:"+e.name+","+e.beforePrice+","+e.nowPrice)
            num++
        }
        editor.commit()

//        //値を取得する
//        val prefs2 = PreferenceManager.getDefaultSharedPreferences(this.activity)
//        val json = prefs2.getString("RecList", "")
//        val gson2 = Gson()
//        val p = gson2.fromJson<GoodsData>(json, GoodsData::class.java!!)
//        System.out.println("保存できているかのテスト:"+p.name)
//        //RecipientArray.add(p) //RecipientArray is an ArrayList<Person>
    }
}

// 入力されたURLがinput引数となり、引数のURLの商品の価格を返す
private fun getGoods(url: String): GoodsData {
    /* スクレイピングの実装 */
    var result :GoodsData = GoodsData()
    try {
        // 指定したURLにGETメソッドでアクセス
        // 結果をパース(テキストのみを取得)し Documentオブジェクトに格納
        val document = Jsoup.connect(url).get()

        // "h1"タグが持つテキスト群に絞る
        var h1List = document.select("h1")
        // "h1"タグが持つテキスト群の要素から商品名を取得する
        for (element in h1List) {
            // 商品名の取得
            // "span"タグの"class"属性値が"item-name"のテキスト要素かの判定
            if ( element.attr("class").equals("item-name") ) {
                // 商品名の設定
                result.name = element.text().toString()
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
                result.beforePrice = element.text().toString()
            }
        }

        // 商品名の設定
        result.nowPrice = "¥ ---"

    } catch (e: IOException) {
        e.printStackTrace()
    }
    return result
}
