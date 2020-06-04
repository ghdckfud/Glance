package com.example.glance;

import android.app.Notification;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;


public class NotificationService extends NotificationListenerService {

    Context context;
    public static Map<String,Integer> dict=new HashMap<String, Integer>(); //단어사전
    public static Map<Integer,String> encodedict=new HashMap<Integer, String>(); //결과 숫자를 카테고리로 변환
    public static double[] diction;
    public static double[][] X;
    public static int[] y;
    KNeighborsClassifier clf; //학습 분류기
    Thread thread; //형태소 분석을 진행할 스레드
    String[] alarmValue; //php전달 정보 저장
    Drawable storeIcon = null; //icon 임시저장
    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
        //결과로 나온 숫자를 해당하는 카테고리 값으로 변환
        encodedict.put(0,"건강");
        encodedict.put(1,"광고");
        encodedict.put(2,"교통");
        encodedict.put(3,"금융");
        encodedict.put(4,"날씨");
        encodedict.put(5,"라이프스타일");
        encodedict.put(6,"소셜");
        encodedict.put(7,"아이");
        encodedict.put(8,"취업");
        IntentFilter filter = new IntentFilter();
        filter.addAction("NOTIFICATION_LISTENER_SERVICE_EXAMPLE");
        makeDict();

        X=new double[462][428]; //사전으로 one-hot-encoding한 결과을 저장할 배열
        y=new int[462]; //사전에 대한 답을 저장할 배열
        makeparameter();

        clf = new KNeighborsClassifier(1, 9, 2, X, y);


    }


    public void makeparameter(){ //train값과 그에 대한 결과 값을 받는과정
        AssetManager asm = getResources().getAssets();
        InputStream is = null;


        try{
            int num1=0;
            //원핫 인코딩된 사전을 입력받는 작업
            is = asm.open("X_parameter.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            String aline = "";
            while((line=reader.readLine())!= null){
                aline += line;
            }
            String array[]=aline.split(" ");
            for(int i=0;i<X.length;i++){
                for(int j=0;j<X[0].length;j++){
                    X[i][j]=Double.parseDouble(array[num1]);
                    num1++;
                }
            }
            //사전의 답을 입력받는 작업
            is = asm.open("y_parameter.txt");
            reader = new BufferedReader(new InputStreamReader(is));
            line = "";
            aline = "";
            num1=0;
            while((line=reader.readLine())!= null){
                aline += line;
            }
            array=aline.split(" ");
            for(int i=0;i<y.length;i++){
                y[i]=Integer.parseInt(array[num1]);
                num1++;
            }



        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if (is != null) {
                try { is.close(); } catch (IOException e) {}
            }
        }

    }
    public double[] wordAnalysis(String text){ //형태소분석 알고리즘으로 형태소 분류
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseNLU";
        String accessKey = "7dd09de4-7660-4e2f-8503-7ac2266b7e60";   // 발급받은 API Key
        String analysisCode = "morp";        // 언어 분석 코드
        double[] dic = new double[428];
        Gson gson = new Gson();

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        argument.put("analysis_code", analysisCode);
        argument.put("text", text);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        Integer responseCode = null;
        String responBodyJson = null;
        Map<String, Object> responeBody = null;

        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes());
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            InputStream is = con.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer sb = new StringBuffer();

            String inputLine = "";
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
            responBodyJson = sb.toString();

            // http 요청 오류 시 처리
            if ( responseCode != 200 ) {
                // 오류 내용 출력

                return null;
            }

            responeBody = gson.fromJson(responBodyJson, Map.class);
            Integer result = ((Double) responeBody.get("result")).intValue();
            Map<String, Object> returnObject;
            List<Map> sentences;

            // 분석 요청 오류 시 처리
            if ( result != 0 ) {

                // 오류 내용 출력
                return null;
            }

            // 분석 결과 활용
            returnObject = (Map<String, Object>) responeBody.get("return_object");
            sentences = (List<Map>) returnObject.get("sentence");


            for( Map<String, Object> sentence : sentences ) {
                // 형태소 분석기 결과 수집 및 정렬
                List<Map<String, Object>> morphologicalAnalysisResult = (List<Map<String, Object>>) sentence.get("morp");
                for( Map<String, Object> morphemeInfo : morphologicalAnalysisResult ) {
                    String lemma = (String) morphemeInfo.get("lemma");
                    String type = (String) morphemeInfo.get("type");
                    if(type.equals("SL")|type.equals("SH")|type.equals("SN")|type.equals("NNG")|type.equals("NNP")|type.equals("NNB")|type.equals("XSN")|type.equals("VV")|type.equals("VA")) {
                        for(String s:dict.keySet()) {
                            if(lemma.equals(s)) {
                                dic[dict.get(s)]=1;
                                break;
                            }
                        }
                    }

                }

            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dic;
    }
    public void makeDict() { //사전구축

        AssetManager am = getResources().getAssets();
        InputStream is = null;


        try{
            int num=0;
            is = am.open("dict.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = "";
            while((line=reader.readLine())!= null){
                dict.put(line, num);
                num++;
            }

        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if (is != null) {
                try { is.close(); } catch (IOException e) {}
            }
        }


    }


    private void getNotiText(StatusBarNotification sbn){ //알림에 따른 구분으로 식별, 알림에 대한 정보 저장
        alarmValue = new String[5]; //데이터 베이스에 저장될 배열
        String packName = sbn.getPackageName(); //패키지네임 확인
        Notification mNotification = sbn.getNotification();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //날짜형식지정
        String dateS = dateFormat.format(mNotification.when);
        Bundle extras = mNotification.extras;
        storeIcon=null;
        final String word; //분류를 예측하는 값
        alarmValue[4]=dateS;
        try {
            alarmValue[1]= getPackageManager().getApplicationLabel
                    (getPackageManager().getApplicationInfo
                            (packName, PackageManager.GET_UNINSTALLED_PACKAGES)).toString();
            storeIcon = getPackageManager().getApplicationIcon(packName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(packName.equalsIgnoreCase("com.whatsapp")){
            alarmValue[2]="없음";
            alarmValue[3] = mNotification.tickerText.toString();
        }
        else if(packName.equalsIgnoreCase("com.kakao.talk")){
            if(extras.getString(Notification.EXTRA_TITLE)!=null){
                alarmValue[2]=extras.getString(Notification.EXTRA_TITLE);
                alarmValue[3] = extras.getString(Notification.EXTRA_TEXT);
            }
        }
        else{
            if(mNotification.tickerText!=null) {
                alarmValue[2] = extras.getString("android.title");
                alarmValue[3] = extras.getCharSequence("android.text").toString();

            }
        }
        if(alarmValue[2]!=null){
            if((packName.equalsIgnoreCase("com.facebook.orca"))||(packName.equalsIgnoreCase("com.kakao.talk"))){
                alarmValue[0]="메신저";
            }
            else if(packName.equalsIgnoreCase("com.Slack")){
                alarmValue[0]="업무";
            }
            //쓰레드를 통해 형태소 분석
            else{
                word = extras.getString(Notification.EXTRA_TEXT);
                thread = new Thread(){
                    public void run(){
                        diction=wordAnalysis(word);
                    }
                };
                thread.start();
                try{
                    thread.join();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                int estimation=clf.predict(diction);
                alarmValue[0]= encodedict.get(estimation);
            }
        }
        if(alarmValue[2]!=null){ //null이 아닐경우만 전송
            //스위치가 켜져있을 경우만 DB에 저장 후 출력 아닐 경우는 저장되지 않는다.
            if(MainActivity.pref.getBoolean("implement",true)){
                SharedPreferences.Editor editor = MainActivity.pref.edit();
                //않읽은 갯수를 불러와서 1을 더해주고 다시 저장한다.
                int num=MainActivity.pref.getInt(alarmValue[1],0);
                editor.putInt(alarmValue[1],num+1);
                editor.commit();
                MainActivity.mDbOpenHelper.insertColumn(alarmValue[0], alarmValue[1], alarmValue[2], alarmValue[3], alarmValue[4], getByteArrayFromDrawable(storeIcon)); //알림받은 정보 데이터베이스에 저장
                MainActivity.showDatabase();
            }
        }

    }
    public byte[] getByteArrayFromDrawable(Drawable d){ //Drawable정보로 부터 byte값 도출하기
        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100 ,stream);
        byte[] data = stream.toByteArray();
        return data;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){ //알림이 올시 실행되는 메소드
        if(sbn == null) return;
        getNotiText(sbn);
    }


    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){ //알림 제거시 실행
        Log.i("Noti_Listen","Notification Removed");

    }
}
