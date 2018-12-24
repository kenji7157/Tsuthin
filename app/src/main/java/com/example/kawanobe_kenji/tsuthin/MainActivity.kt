package com.example.kawanobe_kenji.tsuthin

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ListView

import org.jsoup.Jsoup
import java.io.IOException
import android.widget.ArrayAdapter



// タイトルバーを外しておくためにはMainActivityの継承はActivityとする
class MainActivity : AppCompatActivity() {

    // 初期のリスト項目を設定
    //private var arrayAdapter: MyArrayAdapter? = null

    // RecordActivityに送るようのリスト
    private var goodsDataList: ArrayList<GoodsData> = ArrayList<GoodsData>()


    // onCreate アクティビティが起動されたときに呼び出されるメソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(ビュー) 画面に表示するビューを設定する関数
        setContentView(R.layout.activity_main)
        // Toolbar用の変数を用意
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        // setSupportActionBar()によってtoolbarをActionBarと同じ様に使う事ができる
        setSupportActionBar(toolbar)

        // adapter初期化
        //arrayAdapter = MyArrayAdapter(this, 0)


        // getPriceButton押下時のイベント処理
        findViewById<View>(R.id.getPriceButton).setOnClickListener {
            // 非同期処理が実行される
            System.out.println("非同期処理が実行される")
            MyAsyncTask().execute()
        }
    }

    // ツールバーに表示される項目を追加する
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 設置したメニューの項目がクリックされた時に呼ばれる
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
//            R.id.home -> {
//                // main画面に切り替える
//                setContentView(R.layout.activity_main)
//                // Toolbar用の変数を用意
//                var toolbar = findViewById<Toolbar>(R.id.toolbar)
//                // setSupportActionBar()によってtoolbarをActionBarと同じ様に使う事ができる
//                setSupportActionBar(toolbar)
//            }

            R.id.record -> {
                // record画面に切り替える
                val intent = Intent(this, RecordActivity::class.java)
                // 変数sendGoodsDataListにsendGoodsDataListを格納する
                val sendGoodsDataList = goodsDataList
                //intent変数をつなげる(第一引数はキー，第二引数は渡したい変数)
                intent.putExtra("goodsDataList",sendGoodsDataList)

                startActivity(intent)
                // Activity遷移時のアニメーションを無効化
                overridePendingTransition(0, 0);

//                setContentView(R.layout.activity_record)
//                // Toolbar用の変数を用意
//                var toolbar = findViewById<Toolbar>(R.id.toolbar)
//                // setSupportActionBar()によってtoolbarをActionBarと同じ様に使う事ができる
//                setSupportActionBar(toolbar)
//
                // ListView にリスト項目と ArrayAdapter を設定
                //val listView : ListView = findViewById(R.id.listView)
                //listView.adapter = arrayAdapter


            }
        }
        return false
    }

    // AsyncTaskクラスを継承したMyAsyncTaskをMainActivityクラス内に記述
    inner class MyAsyncTask: AsyncTask<Void, Void, GoodsData>() {

        // doInBackground()は非同期で行いたい処理の内容 返戻値はonPostExecute()の引数となる
        override fun doInBackground(vararg p0: Void?): GoodsData? {
            // レイアウト上のテキストビュー:urlTextオブジェクトを変数urTextに格納
            val urlText = findViewById<TextView>(R.id.urlText)
            //　返戻値はonPostExecute()の引数となる
            return getGoods(urlText.text.toString())
        }

        // onPostExecute()はdoIMnBackground()終了後にメインスレッドで実行される処理の内容
        override fun onPostExecute(result: GoodsData) {
            super.onPostExecute(result)
            // レイアウト上のテキストビュー:mytextオブジェクトを変数textviewに格納
            val texview = findViewById<TextView>(R.id.priceText)
            //
            texview.setText(result.beforePrice)
            //System.out.println(result?.substring(2))
            //arrayAdapter?.add(GoodsData(result.name, result.beforePrice,"¥ zzz"))
            // goodsDataListへ要素を追加
            goodsDataList.add(GoodsData(result.name, result.beforePrice,"¥ zzz"))
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

}



