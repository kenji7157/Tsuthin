package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.IOException
import java.net.MalformedURLException

// AsyncTaskクラスを継承したMyAsyncTaskをMainActivityクラス内に記述
// AsyncTask<String,String,GoodsData>
// 第一引数は処理の実行時に渡すパラメータ -> GoodsData
// 第二引数はonProgressUpdateに渡す引数のパラメータ -> Void
// 第三引数はonPostExecuteに渡す引数のパラメータ -> GoodsData
class ActivityAsyncTask(private var activity: MainActivity?) : AsyncTask<GoodsData, Void, GoodsData>() {

    //private var goodsDataList: ArrayList<GoodsData> = ArrayList()

    // doInBackground()は非同期で行いたい処理の内容 返戻値はonPostExecute()の引数となる
    override fun doInBackground(vararg goodsData: GoodsData): GoodsData? {
        System.out.println("doInBackground")

        System.out.println("アクティビティから起動")
        // リストにURLを登録するだけで商品情報は取得しなくて良い
        // URLが適正か判定する
        //　返戻値はonPostExecute()の引数となる
        return getGoods(goodsData[0])
    }

    // onPostExecute()はdoIMnBackground()終了後にメインスレッドで実行される処理の内容
    override fun onPostExecute(result: GoodsData) {
        System.out.println("onPostExecute")

        // アクティビティ非同期処理開始のためサービスを一旦停止する
        this.activity?.stopService()

        // アクティビティから起動
        if (this.activity != null) {
            // エラーメッセージを取得していた場合
            if(result.name == "ページが見つかりません"){
                this.activity?.showAlert("エラーメッセージ",result.name,result.url)
            }
            else if(result.name == "無効なURLです"){
                this.activity?.showAlert("エラーメッセージ",result.name,result.url)
            }
            // 商品情報が取得できた場合
            else {
                this.activity?.showAlert("登録確認","以下のURLを登録します",result.url)
            }
        }

        // アクティビティ非同期処理完了のためサービスを再開する
        this.activity?.startService()

    }

    // 入力されたURLがinput引数となり、引数のURLの商品情報を返す
    private fun getGoods(goodsData: GoodsData): GoodsData {
        /* スクレイピングの実装 */
        var result = goodsData
        try {
            // 指定したURLにGETメソッドでアクセス: 結果をパース(テキストのみを取得)し Documentオブジェクトに格納
            val document = Jsoup.connect(goodsData.url).get()

            // "h1"タグが持つテキスト群に絞る
            var h1List = document.select("h1")
            // "h1"タグが持つテキスト群の要素から商品名を取得する
            for (element in h1List) {
                // 商品名の取得: "span"タグの"class"属性値が"item-name"のテキスト要素かの判定
                if ( element.attr("class").equals("item-name") ) {
                    // 商品名の設定
                    result.name = element.text().toString()
                }
            }

            // url情報の設定
            result.url = goodsData.url

        } catch (e: HttpStatusException){
            System.out.println("ページが見つかりません")
            result.name = "ページが見つかりません"
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {
            System.out.println("無効なURLです")
            result.name = "無効なURLです"
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

}


