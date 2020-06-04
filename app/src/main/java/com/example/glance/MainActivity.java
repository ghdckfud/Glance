package com.example.glance;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //private static NotificationReceiver nReceiver;
    private  AlertDialog dialog;
    protected static DbOpenHelper mDbOpenHelper; //Db명령어 수행
    public static AlarmAdapter alarmAdapter; //ListView에 달아 출력시 필요
    public static ListView listView;
    public static SharedPreferences pref;
    Switch dbSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //toobar를 appbar로 지정
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //화면에 toolbar를 등록
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //navigation 등록
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //DB가 아닌 또하나의 저장 공간 SharedPreferences(아주 작은 저장공간)
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        //navigation 중 header
        View header = navigationView.getHeaderView(0);
        //header에 있는 리소스가져오기
        //DB의 동작여부를 담는 스위치
        dbSwitch =(Switch)header.findViewById(R.id.dbSwich);
        //저장된 값을 불러와서 switch설정
        dbSwitch.setChecked(pref.getBoolean("implement",true));

        //DB내용을 담을 adapter 생성 후 listview에 등록
        alarmAdapter=new AlarmAdapter();
        listView = (ListView) findViewById(R.id.db_list_view);
        listView.setAdapter(alarmAdapter);
        mDbOpenHelper = new DbOpenHelper(this); //DB생성자 생성
        mDbOpenHelper.open(); //DB열기
        mDbOpenHelper.create(); //DB만들기
        showDatabase();


        //nReceiver = new NotificationReceiver();
        //IntentFilter filter = new IntentFilter();
        //filter.addAction("NOTIFICATION_LISTENER_EXAMPLE");
        //registerReceiver(nReceiver,filter);
        boolean isPermissionAllowed = isNotiPermissionAllowed();
        if(!isPermissionAllowed) { //권한이 없는경우 권한 받는 페이지로 이동
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }

        //switch값에따라 저장된 값 변경
        dbSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = pref.edit();
                if(isChecked){
                    editor.putBoolean("implement", true);
                }
                else{
                    editor.putBoolean("implement", false);
                }
                editor.commit();
            }
        });

        //버튼 누를시 화면 변경
        final Button allbutton = (Button) findViewById(R.id.allButton);
        final Button socialbutton = (Button)findViewById(R.id.socialButton);
        final Button babybutton = (Button)findViewById(R.id.babyButton);
        final Button workbutton = (Button)findViewById(R.id.workButton);


        allbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allbutton.setTextColor(getResources().getColor(R.color.colorBlack));
                socialbutton.setTextColor(getResources().getColor(R.color.colorGray));
                babybutton.setTextColor(getResources().getColor(R.color.colorGray));
                workbutton.setTextColor(getResources().getColor(R.color.colorGray));
                allbutton.setBackground(getResources().getDrawable(R.drawable.button_style));
                socialbutton.setBackground(getResources().getDrawable(R.drawable.button));
                babybutton.setBackground(getResources().getDrawable(R.drawable.button));
                workbutton.setBackground(getResources().getDrawable(R.drawable.button));
                showDatabase();
            }
        });

        socialbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                allbutton.setTextColor(getResources().getColor(R.color.colorGray));
                socialbutton.setTextColor(getResources().getColor(R.color.colorBlack));
                babybutton.setTextColor(getResources().getColor(R.color.colorGray));
                workbutton.setTextColor(getResources().getColor(R.color.colorGray));
                allbutton.setBackground(getResources().getDrawable(R.drawable.button));
                socialbutton.setBackground(getResources().getDrawable(R.drawable.button_style));
                babybutton.setBackground(getResources().getDrawable(R.drawable.button));
                workbutton.setBackground(getResources().getDrawable(R.drawable.button));
                selectDatabase("소셜");
            }
        });
        babybutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                allbutton.setTextColor(getResources().getColor(R.color.colorGray));
                socialbutton.setTextColor(getResources().getColor(R.color.colorGray));
                babybutton.setTextColor(getResources().getColor(R.color.colorBlack));
                workbutton.setTextColor(getResources().getColor(R.color.colorGray));
                allbutton.setBackground(getResources().getDrawable(R.drawable.button));
                socialbutton.setBackground(getResources().getDrawable(R.drawable.button));
                babybutton.setBackground(getResources().getDrawable(R.drawable.button_style));
                workbutton.setBackground(getResources().getDrawable(R.drawable.button));
                selectDatabase("아이");
            }
        });
        workbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                allbutton.setTextColor(getResources().getColor(R.color.colorGray));
                socialbutton.setTextColor(getResources().getColor(R.color.colorGray));
                babybutton.setTextColor(getResources().getColor(R.color.colorGray));
                workbutton.setTextColor(getResources().getColor(R.color.colorBlack));
                allbutton.setBackground(getResources().getDrawable(R.drawable.button));
                socialbutton.setBackground(getResources().getDrawable(R.drawable.button));
                babybutton.setBackground(getResources().getDrawable(R.drawable.button));
                workbutton.setBackground(getResources().getDrawable(R.drawable.button_style));
                selectDatabase("업무");
            }
        });
    }




    private long lastTimeBackPressed;
    @Override //취소 누를시 navigationbar가 들어가도록 설정
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(System.currentTimeMillis()-lastTimeBackPressed < 3000){ //3초 이내로 다시 한번도 취소 버튼을 누를 시 종료
                finish();

                return ;
            }
            Toast.makeText(this,"뒤로 버튼을 한 번 더 눌러 종료합니다", Toast.LENGTH_SHORT).show();
            lastTimeBackPressed = System.currentTimeMillis();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //appbar에 actionbar생성
        getMenuInflater().inflate(R.menu.appbar_action, menu);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //Navigation 조작옵션
        int id = item.getItemId();
        if (id == R.id.nav_first_layout) {

        } else if (id == R.id.nav_second_layout) {


        } else if (id == R.id.nav_insert) {
            mDbOpenHelper.insertColumn("소셜", "Facebook" ,"[Facebook]상태 알림", "김학래님이 새로운 사진을 추가했습니다.", "2019.11.30 17:24:03", null);
            mDbOpenHelper.insertColumn("소셜", "INSTAGRAM","[INSTAGRAM]알림", "[kai.leeway]:bemyself_sojung님이 스토리를 추가했습니다.", "2019.12.01 20:29:23", null);
            mDbOpenHelper.insertColumn("아이", "키즈노트","없음","온유반 강유주의 알림장이 작성되었어요. 온유반 교사 10월 14일", "2019.12.01 21:20:49", null);
            mDbOpenHelper.insertColumn("아이", "아이엠스쿨","[아이엠스쿨]서울상천초등학교" , "서울상천초등학교 4 - 4학년 2반 학급 공지가 등록되었습니다. 1.수요일 ","2019.12.02 07:47:43", null);
            mDbOpenHelper.insertColumn("업무","SLACK","#공동채널-잡담방 @11기 정회형" ,"shared a file: Image from iOS", "2019.12.11 12:30:44", null);
            mDbOpenHelper.insertColumn("업무", "Calender","[외근]KB증권 미팅" ,"3:30 - 4:30 PM","2019.13.00 00:00:00", null);
            showDatabase();
        } else if (id == R.id.nav_delete) {
            mDbOpenHelper.deleteAllColumns();
            showDatabase();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){ //actionbar 작동 구현
        switch (item.getItemId()) {
            case R.id.action_search :
                return true ;
            case R.id.option :
                return true ;
            default :
                return super.onOptionsItemSelected(item) ;
        }
    }
    //접근 권한 요청이 되어있는지 판단하는 메소드
    private boolean isNotiPermissionAllowed() {
        Set<String> notiListenerSet = NotificationManagerCompat.getEnabledListenerPackages(this);
        String myPackageName = getPackageName();

        for(String packageName : notiListenerSet) {
            if(packageName == null) {
                continue;
            }
            if(packageName.equals(myPackageName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static Bitmap getAppIcon(byte[] b){
        if(b == null){
            return null;
        }
        Bitmap bitmap= BitmapFactory.decodeByteArray(b, 0,b.length);
        return bitmap;
    }


    public static void showDatabase(){ //데이터베이스 출력
        Cursor cCursor = mDbOpenHelper.sortColumn();
        String firstCategory =null;
        String tempIndex=null, tempCategory=null, tempName=null, tempTitle=null, tempText=null, tempDatetime =null;
        byte[] iconData = null;

        alarmAdapter.clearList();

        while(cCursor.moveToNext()){
            //가장 최신 알림부터 카테고리 값을 받음
            firstCategory = cCursor.getString(cCursor.getColumnIndex("category"));
            Cursor iCursor = mDbOpenHelper.seekColumns(firstCategory);
            Log.d("showDatabase", "DB Size: " + iCursor.getCount());
            while(iCursor.moveToNext()){
                //카테고리 별로 어플명당 1개의 알림씩 출력
                tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
                iconData = iCursor.getBlob(iCursor.getColumnIndex("icon"));
                tempCategory = iCursor.getString(iCursor.getColumnIndex("category"));
                tempName = iCursor.getString(iCursor.getColumnIndex("name"));
                tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
                tempText = iCursor.getString(iCursor.getColumnIndex("text"));
                tempDatetime = iCursor.getString(iCursor.getColumnIndex("datetime"));
                alarmAdapter.addItem(tempIndex,getAppIcon(iconData) ,tempCategory, tempName, tempTitle, tempText, tempDatetime);
            }
        }

        //adapter변경된 사항 적용
        alarmAdapter.notifyDataSetChanged();

    }

    public static void selectDatabase(String s){ //데이터베이스 출력
        String tempIndex=null, tempCategory=null, tempName=null, tempTitle=null, tempText=null, tempDatetime =null;
        byte[] iconData = null;

        alarmAdapter.clearList();
        Cursor iCursor = mDbOpenHelper.seekColumns(s);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        while(iCursor.moveToNext()){
            //카테고리 별로 어플명당 1개의 알림씩 출력
            tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            iconData = iCursor.getBlob(iCursor.getColumnIndex("icon"));
            tempCategory = iCursor.getString(iCursor.getColumnIndex("category"));
            tempName = iCursor.getString(iCursor.getColumnIndex("name"));
            tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
            tempText = iCursor.getString(iCursor.getColumnIndex("text"));
            tempDatetime = iCursor.getString(iCursor.getColumnIndex("datetime"));
            alarmAdapter.addItem(tempIndex,getAppIcon(iconData) ,tempCategory, tempName, tempTitle, tempText, tempDatetime);
        }


        //adapter변경된 사항 적용
        alarmAdapter.notifyDataSetChanged();

    }


    @Override
    protected void onStop(){
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }


}
