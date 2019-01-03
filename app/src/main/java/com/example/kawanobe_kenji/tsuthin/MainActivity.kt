package com.example.kawanobe_kenji.tsuthin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.webkit.URLUtil
import android.widget.Toast
import com.google.gson.Gson
import org.jsoup.HttpStatusException

class MainActivity : AppCompatActivity() {

    // 共通データ
    var utilData: UtilData = UtilData()

    // onCreate アクティビティが起動されたときに呼び出されるメソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContentView(ビュー) 画面に表示するビューを設定する関数
        setContentView(R.layout.activity_main)

        // Toolbar用の変数を用意
        var toolbar = findViewById<Toolbar>(R.id.toolbar)

        // setSupportActionBar()によってtoolbarをActionBarと同じ様に使う事ができる
        setSupportActionBar(toolbar)

        // 共通データの取得
        this.utilData = this.application as UtilData

        // サービスの開始
        startService()
        // サービステスト　E

        // getPriceButton押下時のイベント処理-> 共通データ.商品情報リストへURLのみ登録
        findViewById<View>(R.id.entryButton).setOnClickListener {
            // 登録された数が20未満であるばあいに登録する事ができる処理を追加する
            // FILL_IN
             if (this.utilData.getGoodsDataList().size < 20) {

                 // 入力されたURLを変数urlに格納する
                 var url = findViewById<TextView>(R.id.urlText).text.toString()
                 // URL確認用商品情報を生成する
                 var checkUrl = GoodsData("entry", "", "¥---", "¥---", url, false)

                 // 非同期処理を行う前に既に登録されているURLでないか確認する
                 if (containCheck(url)) {
                     // 共通データ.URL確認リスト を持つ非同期通信処理を実行する
                     ActivityAsyncTask(this).execute(checkUrl)
                 } else {
                     // 既に登録されている商品情報のURLであることをアラート
                     showAlert("エラーメッセージ", "入力されたURLは既に登録されています", "")
                 }
             } else {
                 // 既に登録されている商品情報のURLであることをアラート
                 showAlert("エラーメッセージ", "登録できる商品情報は20個です。", "")
             }
        }

    }



    // 既に登録されたURLでないか判定する true/false 未登録/登録済
    private  fun containCheck(url: String): Boolean{
        for(goodsData in this.utilData.getGoodsDataList()){
            if(url == goodsData.url){
                System.out.println("登録済URL")
                return false
            }
        }
        System.out.println("未登録URL")
        return true
    }

    fun showAlert(title: String, message: String, url: String){
        // ダイアログを作成して表示
        if(title == "エラーメッセージ") {
            AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage(message)
                setPositiveButton("OK", null)
                show()
            }
        } else if (title == "登録確認") {
            AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage(message+"\n"+url)
                setPositiveButton("OK", { _, _ ->
                    // OKをタップしたときの処理
                    // URLのみをもつ商品情報を生成する
                    //✨テスト
                    var goodsData = GoodsData("---", url, "¥---", "¥---", url, false)

                    //var goodsData = GoodsData("---", "商品情報取得中", "¥---", "¥---", url, false)
                    // 共通データ.商品情報リストに要素を追加
                    this@MainActivity.utilData.addGoodsDataList(goodsData)

                })
                setNegativeButton("Cancel", { _, _ ->
                    // Cancelをタップしたときの処理
                })
                show()
            }
        }
    }

    // サービス停止時に実行
    override fun onDestroy() {
        super.onDestroy()
        //stopService(intent)
    }

    // サービスの開始
    fun startService(){
        val intent = Intent(this, GoodsService::class.java)
        // 開始後は、開始したActivityが破棄されても基本的には実行し続けられる
        startService(intent)
    }

    // サービスの停止
    fun stopService(){
        val intent = Intent(this, GoodsService::class.java)
        // 開始後は、開始したActivityが破棄されても基本的には実行し続けられる
        stopService(intent)
    }

    // ツールバーに表示される項目を追加する
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // 設置したメニューの項目がクリックされた時に呼ばれる
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            // メニュー:R.id.record押下時のイベント
            R.id.record -> {
                // record画面に切り替える為のインテントの生成
                val intent = Intent(this, RecordActivity::class.java)
                // インテント情報を引き渡しrecord画面に切り替える
                startActivity(intent)
                // Activity遷移時のアニメーションを無効化
                overridePendingTransition(0, 0);
            }
        }
        return false
    }
}



