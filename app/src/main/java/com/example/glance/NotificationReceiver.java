package com.example.glance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    MainActivity mainActivity;
    public NotificationReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // NotificationService에서 방송한 내용 채취
        String text0 = intent.getStringExtra("text[0]");
        String text1 = intent.getStringExtra("text[1]");
        String text2 = intent.getStringExtra("text[2]");
        String text3 = intent.getStringExtra("text[3]");
        String text4 = intent.getStringExtra("text[4]");
        //ainActivity.mDbOpenHelper.insertColumn(text0, text1, text2, text3, text4); //알림받은 정보 데이터베이스에 저장
        mainActivity.showDatabase();

    }
}
