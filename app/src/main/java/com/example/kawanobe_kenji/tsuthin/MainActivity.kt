package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

import okhttp3.OkHttpClient
import okhttp3.Request


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
    val req = Request.Builder().url("http://www.google.co.jp").get().build()
    val resp = client.newCall(req).execute()
    return resp.body()!!.string()
}

