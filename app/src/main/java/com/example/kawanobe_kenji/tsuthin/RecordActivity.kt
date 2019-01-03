package com.example.kawanobe_kenji.tsuthin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item.view.*
import android.widget.Toast



class RecordActivity : AppCompatActivity() {

    // 変数arrayAdapterを生成
    private var arrayAdapter: MyArrayAdapter? = null

    // 共通データ
    private var utilData: UtilData = UtilData()

    // intent情報のキー定数
    val KEY_STATE = "goodsDataList"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_record)



        // Toolbar用の変数を用意
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        // setSupportActionBar()によってtoolbarをActionBarと同じ様に使う事ができる
        setSupportActionBar(toolbar)

        // 共通データの取得
        utilData = this.application as UtilData

        // arrayAdapterのインスタンス生成
        arrayAdapter = MyArrayAdapter(this, 0)

        // arrayAdapterへgoodsDataListの要素を追加
        for (goodsData in utilData.getGoodsDataList()) {
            arrayAdapter?.add(GoodsData(goodsData.index,goodsData.name, goodsData.beforePrice, goodsData.nowPrice, goodsData.url,goodsData.notifyFlag))
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
        when (item.itemId) {
            R.id.home -> {
                // record画面に切り替える
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                // Activity遷移時のアニメーションを無効化
                overridePendingTransition(0, 0)
            }
        }
        return false
    }

    // リストから商品情報の削除
    fun deleteGoods(view: View) {
        Toast.makeText(applicationContext, view.name.text, Toast.LENGTH_SHORT).show()


        AlertDialog.Builder(this).apply {
            setTitle("削除確認")
            setMessage("以下の商品を削除しますか？"+"\n"+view.name.text.toString())
            setPositiveButton("OK", { _, _ ->
                // OKをタップしたときの処理
                this@RecordActivity.utilData.delGoodsDataList(view.name.text.toString())
                // リストの更新
                // record画面に切り替える
                val intent = Intent(this@RecordActivity, RecordActivity::class.java)
                startActivity(intent)
                // Activity遷移時のアニメーションを無効化
                overridePendingTransition(0, 0)

            })
            setNegativeButton("Cancel", { _, _ ->
                // Cancelをタップしたときの処理
            })
            show()
        }
    }
}
