package com.example.glance;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class AlarmAdapter extends BaseAdapter {
    private ArrayList<AppInfo> alarmList =new ArrayList<>();


    public AlarmAdapter() {

    }

    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return alarmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.applicationinfo, parent, false);
        }


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView categoryText = (TextView)convertView.findViewById(R.id.categoryText);
        TextView nameText = (TextView)convertView.findViewById(R.id.nameText);
        TextView contentText = (TextView)convertView.findViewById(R.id.contentText);
        TextView titleText = (TextView)convertView.findViewById(R.id.titleText);
        TextView dateText = (TextView)convertView.findViewById(R.id.dateText);
        ImageView icon = (ImageView)convertView.findViewById(R.id.icon);
        final TextView countText = (TextView)convertView.findViewById(R.id.countText);

        //appinfo에서 position에 위치한 데이터 참조 획득
        final AppInfo appinfo = alarmList.get(position);

        //위치정보를 통해 해당 값 추출후 저장

        if(position==0){
            categoryText.setBackgroundResource(R.drawable.button);
            categoryText.setText(appinfo.getCategory());
            categoryText.setVisibility(View.VISIBLE);
        }
        else{
            AppInfo before =alarmList.get(position-1);
            String beforeCategory = before.getCategory();
            if(appinfo.getCategory().equals(beforeCategory)){
                categoryText.setVisibility(View.GONE);
            }
            else{
                categoryText.setText(appinfo.getCategory());
                categoryText.setVisibility(View.VISIBLE);
            }
        }

        nameText.setText(appinfo.getName());
        String title=appinfo.getTitle();
        if(title==null || title.equals("없음")){
            titleText.setVisibility(View.GONE);
        }
        else{
            titleText.setText(title);
        }
        contentText.setText(appinfo.getText());
        dateText.setText(appinfo.getDatetime());
        if(appinfo.getIcon()==null){
            if(appinfo.getName().equals("Facebook")){
                icon.setImageResource(R.mipmap.facebook);
            }
            else if(appinfo.getName().equals("INSTAGRAM")){
                icon.setImageResource(R.mipmap.instagram);
            }
            else if(appinfo.getName().equals("키즈노트")){
                icon.setImageResource(R.mipmap.kidsnote);
            }
            else if(appinfo.getName().equals("아이엠스쿨")){
                icon.setImageResource(R.mipmap.school);
            }
            else if(appinfo.getName().equals("SLACK")){
                icon.setImageResource(R.mipmap.slack);
            }
            else if(appinfo.getName().equals("Calender")){
                icon.setImageResource(R.mipmap.calendar);
            }
            else{

            }
        }
        else{
            icon.setImageBitmap(appinfo.getIcon());
        }
        //알림 중 읽지 않는 정보를 가져온다.
        int num=MainActivity.pref.getInt(appinfo.getName(),0);

        //0보다 클 시 눈에 보이게하고 않읽은 숫자로 텍스트를 바꾸어준다.
        if(num>99){
            countText.setVisibility(View.VISIBLE);
            countText.setText("+99");
        }
        else if(num>0){
            countText.setVisibility(View.VISIBLE);
            countText.setText(Integer.toString(num));
        }
        else{
            countText.setVisibility(View.GONE);
        }
        LinearLayout listLayout = (LinearLayout)convertView.findViewById(R.id.listLayout);
        listLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = MainActivity.pref.edit();
                editor.putInt(appinfo.getName(),0);
                editor.commit();
                countText.setVisibility(View.GONE);
                MainActivity.showDatabase();
                Intent intent = new Intent(context,DetailNotification.class);
                intent.putExtra("appName",appinfo.getName());
                context.startActivity(intent);
            }
        });
        return convertView;
    }
    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String num, Bitmap icon, String category, String name, String title, String text, String datetime) {
        AppInfo item = new AppInfo();
        item.setNum(num);
        item.setIcon(icon);
        item.setTitle(title);
        item.setDatetime(datetime);
        item.setName(name);
        item.setText(text);
        item.setCategory(category);

        alarmList.add(0,item);
    }
    public void clearList(){
        alarmList.clear();
    }
}
