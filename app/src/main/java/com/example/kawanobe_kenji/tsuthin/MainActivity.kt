package com.example.kawanobe_kenji.tsuthin

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    // RecordActivityに送るようのリスト
    private var goodsDataList: ArrayList<GoodsData> = ArrayList<GoodsData>()

    // url情報
    private var url: String = String()

    // onCreate アクティビティが起動されたときに呼び出されるメソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(ビュー) 画面に表示するビューを設定する関数
        setContentView(R.layout.activity_main)
        // Toolbar用の変数を用意
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        // setSupportActionBar()によってtoolbarをActionBarと同じ様に使う事ができる
        setSupportActionBar(toolbar)

        // goodsDataListへローカル保存されている値を設定する
        settingList()

        // getPriceButton押下時のイベント処理
        findViewById<View>(R.id.getPriceButton).setOnClickListener {
            // テキストビューの入力値をurl情報に格納する
            url = findViewById<TextView>(R.id.urlText).text.toString()
            // 非同期処理が実行される 引数にthis:MainActivity を設定する。
            GoodsAsyncTask().execute(this)
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

            // メニュー:R.id.record押下時のイベント
            R.id.record -> {
                // record画面に切り替える為のインテントの生成
                val intent = Intent(this, RecordActivity::class.java)
                // 変数sendGoodsDataListにgoodsDataListを格納する
                val sendGoodsDataList = goodsDataList
                // 生成したインテントに変数sendGoodsDataListを格納する。(第一引数はキー，第二引数は渡したい変数)
                intent.putExtra("goodsDataList",sendGoodsDataList)
                // インテント情報を引き渡しrecord画面に切り替える
                startActivity(intent)
                // Activity遷移時のアニメーションを無効化
                overridePendingTransition(0, 0);
            }
        }
        return false
    }

    // goodsDataListへ引数の要素を追加する
    fun addGoodsDataList(goodsData: GoodsData) {
        this.goodsDataList.add(goodsData)
    }

    // goodsDataListを返す
    fun getGoodsDataList() : ArrayList<GoodsData>{
        return this.goodsDataList
    }

    // urlを返す
    fun getUrl(): String {
        return this.url
    }

    // goodsDataListへローカル保存されている値を設定する
    private fun settingList(){
        // getDefaultSharedPreferencesで作成したSharedPreferencesのモードはデフォルトでMODE_PRIVATEになる
        // 以下、MODE_PRIVATEの説明
        // getSharedPreferences を呼んだ(= ファイルを作成した) アプリケーションからのみアクセスできる
        // （ただし、同じ user ID を共有しているアプリケーションもアクセスできる）
        val prefs2 = PreferenceManager.getDefaultSharedPreferences(this)
        // 保存されている商品情報の数だけ繰り返す
        for (i in 1..prefs2.all.size) {
            // getString()にて
            // 第一引数：保存データのキー値　element0,element1,...elementN
            // 第二引数：取得できなかった場合の仮値
            val json = prefs2.getString("element"+i.toString(), "")
            val gson2 = Gson()
            // JSONからJavaオブジェクト(GoodsData)への変換
            val goodsData = gson2.fromJson<GoodsData>(json, GoodsData::class.java!!)
            if (goodsData != null) {
                // goodsDataListへ要素を追加する
                goodsDataList.add(goodsData)
            }
        }

    }


}



