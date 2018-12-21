package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

import org.jsoup.Jsoup
import java.io.IOException




class MainActivity : AppCompatActivity() {

    // onCreate アクティビティが起動されたときに呼び出されるメソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(ビュー) 画面に表示するビューを設定する関数
        setContentView(R.layout.activity_main)

        // getPriceButton押下時のイベント処理
        findViewById<View>(R.id.getPriceButton).setOnClickListener {
            // 非同期処理が実行される
            MyAsyncTask().execute()
        }
    }

    // AsyncTaskクラスを継承したMyAsyncTaskをMainActivityクラス内に記述
    inner class MyAsyncTask: AsyncTask<Void, Void, String>() {

        // doInBackground()は非同期で行いたい処理の内容 返戻値はonPostExecute()の引数となる
        override fun doInBackground(vararg p0: Void?): String? {
            // レイアウト上のテキストビュー:urlTextオブジェクトを変数urTextに格納
            val urlText = findViewById<TextView>(R.id.urlText)
            //　返戻値はonPostExecute()の引数となる
            return getPrice(urlText.text.toString())
        }

        // onPostExecute()はdoInBackground()終了後にメインスレッドで実行される処理の内容
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // レイアウト上のテキストビュー:mytextオブジェクトを変数textviewに格納
            val texview = findViewById<TextView>(R.id.priceText)
            //
            texview.setText(result)
        }
    }

}

// 入力されたURLがinput引数となり、引数のURLの商品の価格を返す
fun getPrice(url: String): String {
    /* スクレイピングの実装 */
    try {
        val document = Jsoup.connect(url).get()
        var elements = document.select("span")
        for (element in elements) {
            System.out.println()

            if ( element.attr("class").equals("item-price bold") ) {
                return element.text().toString()
            }
        }
        return elements.text().toString()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return ""
}

