package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 非同期処理が実行される
        MyAsyncTask().execute()
    }

    // AsyncTaskクラスを継承したMyAsyncTaskをMainActivityクラス内に記述
    inner class MyAsyncTask: AsyncTask<Void, Void, String>() {

        // doInBackground()は非同期で行いたい処理の内容
        override fun doInBackground(vararg p0: Void?): String? {
            return getHtml()
        }

        // onPostExecute()はdoInBackground()終了後にメインスレッドで実行される処理の内容
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            // レイアウト上のテキストビュー:mytextオブジェクトを変数textviewに格納
            val texview = findViewById<TextView>(R.id.mytext)
            //
            texview.setText(result)
        }
    }
}

fun getHtml(): String {
    val client = OkHttpClient()
    var url = "https://item.mercari.com/jp/m87035931007/"
    val req = Request.Builder().url(url).get().build()
    val resp = client.newCall(req).execute()
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
    //return resp.body()!!.string()
}

