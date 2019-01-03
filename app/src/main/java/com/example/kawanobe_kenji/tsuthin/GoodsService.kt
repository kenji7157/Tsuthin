package com.example.kawanobe_kenji.tsuthin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import java.util.*
import kotlin.collections.ArrayList


class GoodsService : Service() {

    // 共通データ
    var utilData: UtilData = UtilData()

    private var timer: Timer = Timer()

    // サービス実行フラグ　true/false 実行可能/実行禁止(実行完了待ち)
    private var serviceTaskFlag= true

    //
    fun serviceTaskStart(){
        this.serviceTaskFlag = false
    }

    fun serviceTaskStop(){
        this.serviceTaskFlag = true
    }

    // bindService() で呼び出した場合,onStartCommand() ではなく
    // onBind() がcallbackされます
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    // サービスで実行させたいコードはここに記述
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        System.out.println("サービス処理の開始")

        // 共通データの取得
        utilData = this.application as UtilData

        // 非同期（別スレッド）で定期的(2 秒ごと)に処理を実行させるためにTimerを利用する
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                if(serviceTaskFlag) {
                    System.out.println("サービス非同期処理の開始は解禁中")
                    System.out.println("更新対象の数" + utilData.getGoodsDataList().size)
                    //GoodsAsyncTask().getGoods("")
                    // 共通データ.商品情報リストが空の場合は実行しない
                    if (utilData.getGoodsDataList().size != 0) {
                        // 共通データ.商品情報リストに登録されているURLの商品情報を取得する
                        ServiceAsyncTask(null).execute(utilData.getGoodsDataList())
                        //utilData.getGoods()
                        //ServiceAsyncTask().execute(utilData.getGoodsDataList())
                    }
                    // 通知判定フラグの更新を反映
                    utilData.renew(callNotify(utilData.getGoodsDataList()))
                }else{
                    System.out.println("サービス非同期処理の開始は禁止中")
                }
            }
        }, 0, 2000) //2000ミリ秒 ->　2秒 60秒 -> 60000

        // ↓通知イベント処理
        //callNotify()

        return START_STICKY
    }

    // サービス停止時に実行
    override fun onDestroy() {
        super.onDestroy()
        // timerをキャンセル
        timer.cancel()
        System.out.println("サービス処理の終了")
    }


    // 通知イベント処理
    private fun callNotify(goodsDataList : ArrayList<GoodsData>): ArrayList<GoodsData>{
        // 返戻値用リスト
        val resultList: ArrayList<GoodsData> = ArrayList()


        // NotificationManagerをシステムのサービスから取得し変数notificationManagerに格納
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // カテゴリー名（通知設定画面に表示される情報）
        val name = "商品情報更新通知"
        // システムに登録するChannelのID
        val id = "tsuthin_chanel"
        // 通知の詳細情報（通知設定画面に表示される情報）
        val notifyDescription = "商品情報が更新（価格）されたら通知します。"

        // Channelの取得と生成
        // 引数のidの通知チャンネルが作成されているか判定
        if (notificationManager.getNotificationChannel(id) == null) {
            // 通知チャンネルの作成
            val mChannel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
            mChannel.apply {
                description = notifyDescription
            }
            // 通知チャンネルの登録
            notificationManager.createNotificationChannel(mChannel)
        }

        // 通知メッセージ用変数
        var notification: Notification


        // 商品情報リスト(引数)に通知ONになっているものがいるか確認する
        for (goodsData in goodsDataList) {
            // 通知ONのものがいるか調べる
            if(goodsData.notifyFlag){


                var contextText = "商品名:" + goodsData.name + " が値下げされました。"
                // 通知発動したので通知判定フラグをOFFに更新
                goodsData.notifyFlag = false

                notification = NotificationCompat
                        .Builder(this, id)
                        .apply {
                            setSmallIcon(R.drawable.ic_launcher_background)
                            setContentTitle("商品情報更新通知")
                            setContentText(contextText)
                        }.build()

                // 通知を発行する
                notificationManager.notify(1, notification)
            }


            resultList.add(goodsData)
        }



        return resultList
    }

    /*
    fun taskStart(){
        this.taskFlag = false
    }

    fun taskFinish(){
        this.taskFlag = true
    }*/




}
