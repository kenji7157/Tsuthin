package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import org.jsoup.Jsoup
import java.io.IOException
import android.preference.PreferenceManager
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
        // 引数から呼び出し元のMainActivityを取得
        activity = list[0]
        // 呼び出し元のMainActivityにて入力されたURL情報を変数urlに格納する
        var url = activity.getUrl()
        // 変数urlに格納されたurl先の商品情報を変数goodsDataに格納する
        var goodsData = getGoods(url)
        //　返戻値はonPostExecute()の引数となる
        return goodsData
    }

    // onPostExecute()はdoIMnBackground()終了後にメインスレッドで実行される処理の内容
    override fun onPostExecute(result: GoodsData) {
        super.onPostExecute(result)
        // MainActivityのgoodsDataListへ商品情報を追加
        activity.addGoodsDataList(GoodsData(result.name, result.beforePrice,"¥ zzz",result.url))

        // 取得した商品情報のみをローカルへ保存する

        // getDefaultSharedPreferencesで作成したSharedPreferencesのモードはデフォルトでMODE_PRIVATE
        // 以下、MODE_PRIVATEの説明
        // getSharedPreferences を呼んだ(= ファイルを作成した) アプリケーションからのみアクセスできる
        // （ただし、同じ user ID を共有しているアプリケーションもアクセスできる）
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.activity)
        // edit() メソッド :SharedPreferencesのインスタンスから、SharedPreferences.Editor オブジェクトを取得
        val editor = prefs.edit()
        val gson = Gson()
        // 新規追加するelement番号numの設定
        var num :Int = prefs.all.size+1
        // Javaオブジェクト(GoodsData)からJSONへの変換
        val json = gson.toJson(result)
        // putString()にて
        // 第一引数：保存データのキー値　element0,element1,...elementN
        // 第二引数：保存データ
        editor.putString("element"+num.toString(), json)
        // データを同期的に書き込み
        editor.commit()
    }

    // 入力されたURLがinput引数となり、引数のURLの商品情報を返す
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

            // url情報の設定
            result.url = url

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }
}


