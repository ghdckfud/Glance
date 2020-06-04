package com.example.glance;

import android.provider.BaseColumns;

public final class DataBases {

    public static final class CreateDB implements BaseColumns{
        public static final String CATEGORY = "category";
        public static final String NAME = "name";
        public static final String TITLE = "title";
        public static final String TEXT = "text";
        public static final String DATETIME = "datetime";
        public static final String ICON = "icon";
        public static final String ALARM = "alarm";
        public static final String _CREATE = "create table if not exists "+ ALARM + "("
                +_ID+" integer primary key autoincrement, "
                +CATEGORY+" text not null , "
                +NAME+" text not null , "
                +TITLE+" text not null , "
                +TEXT+" text not null , "
                +DATETIME+" text not null , "
                +ICON+" blob);";
    }
}


