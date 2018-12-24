package com.example.kawanobe_kenji.tsuthin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView

class RecordActivity : AppCompatActivity() {

    // 変数arrayAdapterを生成
    private var arrayAdapter: MyArrayAdapter? = null

    // intent情報のキー定数
    val KEY_STATE = "goodsDataList"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        // Toolbar用の変数を用意
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        // setSupportActionBar()によってtoolbarをActionBarと同じ様に使う事ができる
        setSupportActionBar(toolbar)

        // arrayAdapterのインスタンス生成
        arrayAdapter = MyArrayAdapter(this, 0)

        //val state = intent.getSerializableExtra(KEY_STATE)
        val goodsDataList = intent.getSerializableExtra(KEY_STATE) as ArrayList<GoodsData>

        // arrayAdapterへgoodsDataListの要素を追加
        for (goodsData in goodsDataList) {
            arrayAdapter?.add(GoodsData(goodsData?.name, goodsData?.beforePrice, goodsData?.nowPrice, goodsData?.url))
        }

        // ListView にリスト項目と ArrayAdapter を設定
        val listView : ListView = findViewById(R.id.listView)
        listView.adapter = arrayAdapter
    }

    // ツールバーに表示される項目を追加する
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 設置したメニューの項目がクリックされた時に呼ばれる
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.home -> {
                // record画面に切り替える
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                // Activity遷移時のアニメーションを無効化
                overridePendingTransition(0, 0);
            }
        }
        return false
    }
}
