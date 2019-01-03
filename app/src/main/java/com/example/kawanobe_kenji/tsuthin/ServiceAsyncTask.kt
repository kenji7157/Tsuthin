package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import java.io.IOException
import java.net.MalformedURLException

// AsyncTaskクラスを継承したMyAsyncTaskをMainActivityクラス内に記述
// AsyncTask<String,String,GoodsData>
// 第一引数は処理の実行時に渡すパラメータ -> MainActivity
// 第二引数はonProgressUpdateに渡す引数のパラメータ -> Void
// 第三引数はonPostExecuteに渡す引数のパラメータ -> GoodsData
class ServiceAsyncTask(private var service: GoodsService?) : AsyncTask<ArrayList<GoodsData>, Void, ArrayList<GoodsData>>() {

    private var goodsDataList: ArrayList<GoodsData> = ArrayList()

    // doInBackground()は非同期で行いたい処理の内容 返戻値はonPostExecute()の引数となる
    override fun doInBackground(vararg list: ArrayList<GoodsData>): ArrayList<GoodsData>? {
        System.out.println("doInBackground")

        // サービス用非同期処理の開始
        this.service?.serviceTaskStart()

        // 引数用リスト
        var resultList: ArrayList<GoodsData> = ArrayList()

        // 引数から呼び出し元の商品情報リストを取得
        this.goodsDataList = list[0]

        // サービスから起動
        System.out.println("サービスから起動")
        // サービスから起動する場合のみ商品情報を取得しリストを更新する
        for (e in goodsDataList) {
            //System.out.println("商品名："+e.name)
            resultList.add(getGoods(e))
        }



        //　返戻値はonPostExecute()の引数となる
        return resultList
    }

    // onPostExecute()はdoIMnBackground()終了後にメインスレッドで実行される処理の内容
    override fun onPostExecute(resultList: ArrayList<GoodsData>) {
        super.onPostExecute(resultList)
        System.out.println("onPostExecute")
        // サービス用非同期処理の終了
        this.service?.serviceTaskStop()
        // 商品情報リストの更新を行う
        this.service?.utilData?.renew(resultList)

        /*
        // サービスから呼び出された場合のみ商品情報リストの更新を行う
        if(this.activity != null) {
            this.activity?.utilData?.renew(resultList)
            this.activity?.taskFinish()
        }*/
    }

    // 入力されたURLがinput引数となり、引数のURLの商品情報を返す
    private fun getGoods(goodsData: GoodsData): GoodsData {
        /* スクレイピングの実装 */
        var result = goodsData
        try {
            // 指定したURLにGETメソッドでアクセス
            // 結果をパース(テキストのみを取得)し Documentオブジェクトに格納
            val document = Jsoup.connect(goodsData.url).get()

            // 20181228 入力URL挙動テスト S
            //val document = Jsoup.connect("ああhttps://item.mercari.com/jp/m94837177649/４３").get()
            // 20181228 入力URL挙動テスト E

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
                    // 取得価格(Int型)
                    var getPrice = Integer.parseInt(element.text().toString().substring(2).replace(",", ""))

                    // 登録価格が未設定の場合（登録時の場合）
                    if(goodsData.beforePrice == "¥---") {
                        // 登録価格の設定
                        result.beforePrice = element.text().toString()

                    // 登録価格が設定済 かつ　最新価格が未設定の場合
                    }else if(goodsData.beforePrice != "¥---" && goodsData.nowPrice == "¥---") {
                        // 登録価格
                        var beforePrice = Integer.parseInt(goodsData.beforePrice.substring(2).replace(",", ""))

                        // 価格の変動があった場合 nowPrice の登録を行う
                        if(getPrice != beforePrice) {
                            // 最新商品価格の設定
                            result.nowPrice = element.text().toString()

                            // 安くなっていた場合のみ通知を行う
                            if (getPrice < beforePrice) {
                                // 通知オン
                                result.notifyFlag = true
                            }
                        }
                    }

                    // 登録価格が設定済 かつ　最新価格が設定済の場合
                    else {
                        // 登録価格
                        var beforePrice = Integer.parseInt(goodsData.beforePrice.substring(2).replace(",", ""))

                        // 最新価格
                        var nowPrice = Integer.parseInt(goodsData.nowPrice.substring(2).replace(",", ""))
                        // 価格の変動があった場合 nowPrice の登録を行う
                        if(getPrice != nowPrice) {
                            // 最新商品価格の設定
                            result.nowPrice = element.text().toString()

                            // 安くなっていた場合のみ通知を行う
                            if (getPrice < beforePrice) {
                                // 通知オン
                                result.notifyFlag = true
                            }
                        }

                    }
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


