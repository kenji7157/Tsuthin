package com.example.kawanobe_kenji.tsuthin

import android.os.AsyncTask
import org.jsoup.Jsoup
import java.io.IOException

// AsyncTaskクラスを継承したMyAsyncTaskをMainActivityクラス内に記述
// AsyncTask<String,String,GoodsData>
// 第一引数は処理の実行時に渡すパラメータ -> MainActivity
// 第二引数はonProgressUpdateに渡す引数のパラメータ -> Void
// 第三引数はonPostExecuteに渡す引数のパラメータ -> GoodsData
class GoodsAsyncTask(private var service: GoodsService) : AsyncTask<ArrayList<GoodsData>, Void, ArrayList<GoodsData>>() {

    private var goodsDataList: ArrayList<GoodsData> = ArrayList()

    // doInBackground()は非同期で行いたい処理の内容 返戻値はonPostExecute()の引数となる
    override fun doInBackground(vararg list: ArrayList<GoodsData>): ArrayList<GoodsData>? {
        System.out.println("doInBackground")
        this.service.taskStart()

        // 引数用リスト
        var resultList: ArrayList<GoodsData> = ArrayList()

        // 引数から呼び出し元のMainActivityを取得
        this.goodsDataList = list[0]

        // 改良
        var goodsData:GoodsData
        for (e in goodsDataList) {
            goodsData = getGoods(e)
            resultList.add(goodsData)
        }
        // 呼び出し元のMainActivityにて入力されたURL情報を変数urlに格納する
        //var url = activity.getUrl()
        // 変数urlに格納されたurl先の商品情報を変数goodsDataに格納する
        //var goodsData = getGoods(url)
        //　返戻値はonPostExecute()の引数となる
        return resultList
    }

    // onPostExecute()はdoIMnBackground()終了後にメインスレッドで実行される処理の内容
    override fun onPostExecute(resultList: ArrayList<GoodsData>) {
        super.onPostExecute(resultList)
        //System.out.println("onPostExecute")
        this.service.utilData.renew(resultList)
        this.service.taskFinish()

        // MainActivityのgoodsDataListへ商品情報を追加
        //activity.setGoodsDataList(GoodsData(result.name, result.beforePrice,"¥ zzz",result.url))
        // MainActivityのgoodsDataListへ商品情報リストを設定
        //this.activity?.setGoodsDataList(resultList)
        // 取得した商品情報リストをローカルへ保存する
        //storeList(resultList)
        //System.out.println("test通知２")

//        // 取得した商品情報のみをローカルへ保存する
//        // getDefaultSharedPreferencesで作成したSharedPreferencesのモードはデフォルトでMODE_PRIVATE
//        // 以下、MODE_PRIVATEの説明
//        // getSharedPreferences を呼んだ(= ファイルを作成した) アプリケーションからのみアクセスできる
//        // （ただし、同じ user ID を共有しているアプリケーションもアクセスできる）
//        val prefs = PreferenceManager.getDefaultSharedPreferences(this.activity)
//        // edit() メソッド :SharedPreferencesのインスタンスから、SharedPreferences.Editor オブジェクトを取得
//        val editor = prefs.edit()
//        val gson = Gson()
//        // 新規追加するelement番号numの設定
//        var num :Int = prefs.all.size+1
//        // Javaオブジェクト(GoodsData)からJSONへの変換
//        val json = gson.toJson(result)
//        // putString()にて
//        // 第一引数：保存データのキー値　element0,element1,...elementN
//        // 第二引数：保存データ
//        editor.putString("element"+num.toString(), json)
//        // データを同期的に書き込み
//        editor.commit()
    }

    // 入力されたURLがinput引数となり、引数のURLの商品情報を返す
    private fun getGoods(goodsData: GoodsData): GoodsData {
        /* スクレイピングの実装 */
        var result = goodsData
        try {
            // 指定したURLにGETメソッドでアクセス
            // 結果をパース(テキストのみを取得)し Documentオブジェクトに格納
            val document = Jsoup.connect(goodsData.url).get()

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
                    // 登録価格が未設定の場合（登録時の場合）
                    System.out.println("商品価格："+goodsData.beforePrice)
                    if(goodsData.beforePrice.equals("¥---")) {
                        // 登録価格の設定
                        result.beforePrice = element.text().toString()
                        System.out.println("登録価格の設定："+result.beforePrice)
                    }else {
                        // 取得価格
                        var getPrice = Integer.parseInt(element.text().toString().substring(2).replace(",", ""))
                        //System.out.println("getPrice"+getPrice)
                        // 登録価格
                        //System.out.println(goodsData.beforePrice.substring(2).replace(",",""))
                        var beforePrice = Integer.parseInt(goodsData.beforePrice.substring(2).replace(",", ""))
                        //System.out.println("beforePrice"+beforePrice)
                        // 更新時の場合 取得価格 < 登録価格の場合
                        if(goodsData.nowPrice.equals("¥---")) {
                            if (getPrice < beforePrice) {
                                // 最新商品価格の設定
                                result.nowPrice = element.text().toString()
                                // 通知オン
                                result.notifyFlag = true
                            }
                        }
                        // 一度通知済み商品がさらに値下げされた場合
                        else{
                            var nowPrice = Integer.parseInt(goodsData.nowPrice.substring(2).replace(",", ""))
                            if (getPrice < nowPrice) {
                                // 最新商品価格の設定
                                result.nowPrice = element.text().toString()
                                // 通知オン
                                result.notifyFlag = true
                            }
                        }
                    }
                }
            }
            // url情報の設定
            result.url = goodsData.url

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    // 商品情報リストをローカルへ保存する
    /*private fun storeList(resultList: ArrayList<GoodsData>){
        // 取得した商品情報のみをローカルへ保存する
        // getDefaultSharedPreferencesで作成したSharedPreferencesのモードはデフォルトでMODE_PRIVATE
        // 以下、MODE_PRIVATEの説明
        // getSharedPreferences を呼んだ(= ファイルを作成した) アプリケーションからのみアクセスできる
        // （ただし、同じ user ID を共有しているアプリケーションもアクセスできる）
        val prefs = PreferenceManager.getDefaultSharedPreferences(this.activity)
        // edit() メソッド :SharedPreferencesのインスタンスから、SharedPreferences.Editor オブジェクトを取得
        val editor = prefs.edit()
        val gson = Gson()
        var json: String
        // 新規追加するelement番号numの設定
        var num :Int = 1
        for (e in resultList) {
            json = gson.toJson(e)
            // putString()にて
            // 第一引数：保存データのキー値　URL情報をキーとする
            // 第二引数：保存データ
            editor.putString("element"+num.toString(), json)
            System.out.println("test通知３,num:"+num+",商品名:"+e.name)
            num++
        }
        // データを同期的に書き込み
        editor.commit()



//        // 新規追加するelement番号numの設定
//        var num :Int = prefs.all.size+1
//        // Javaオブジェクト(GoodsData)からJSONへの変換
//        //val json = gson.toJson(result)
//        // putString()にて
//        // 第一引数：保存データのキー値　URL情報をキーとする
//        // 第二引数：保存データ
//        editor.putString("element"+num.toString(), json)
//        // データを同期的に書き込み
//        editor.commit()

    }
*/
}


