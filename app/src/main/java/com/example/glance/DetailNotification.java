package com.example.glance;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class DetailNotification extends AppCompatActivity {
    public static AlarmAdapter detailAdapter; //ListView에 달아 출력시 필요
    public static ListView detailView;
    private static String app_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.cancelBar);
        setSupportActionBar(toolbar); //toobar를 appbar로 지정
        Intent intent = new Intent(this.getIntent());
        app_name = intent.getStringExtra("appName");
        ActionBar ab = getSupportActionBar();
        ab.setTitle(app_name);
        //툴바에 홈버튼을 활성화
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.back);
        detailAdapter = new AlarmAdapter();
        detailView = (ListView)findViewById(R.id.dt_list_view);
        detailView.setAdapter(detailAdapter);
        showDetailNoti();

    }
    @Override //취소 누를시 다시 메인 엑티비티로 돌아감
    public void onBackPressed() {
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //appbar에 actionbar생성
        getMenuInflater().inflate(R.menu.detailbar_action, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){ //actionbar 작동 구현
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void showDetailNoti(){
        Cursor iCursor = MainActivity.mDbOpenHelper.spreadColumns(app_name);
        String tempIndex=null, tempCategory=null, tempName=null, tempTitle=null, tempText=null, tempDatetime =null;
        byte[] iconData = null;

        detailAdapter.clearList();

        while(iCursor.moveToNext()){
            //해당 어플명에 대한 모든 알림들 출력
            Log.d("showDatabase", "DB Size: " + iCursor.getCount());
            tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            iconData = iCursor.getBlob(iCursor.getColumnIndex("icon"));
            tempCategory = iCursor.getString(iCursor.getColumnIndex("category"));
            tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
            tempText = iCursor.getString(iCursor.getColumnIndex("text"));
            tempDatetime = iCursor.getString(iCursor.getColumnIndex("datetime"));
            detailAdapter.addItem(tempIndex,MainActivity.getAppIcon(iconData) ,tempCategory, tempName, tempTitle, tempText, tempDatetime);

        }
    }
}
