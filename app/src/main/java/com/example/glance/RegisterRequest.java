package com.example.glance;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    final static private  String URL = "http://172.30.1.29/RegisterAlarm.php";
    private Map<String, String> parameters;
    public RegisterRequest(String datetime, String category, String name, String title, String text, String sid, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("datetime",datetime);
        parameters.put("category",category);
        parameters.put("name",name);
        parameters.put("title",title);
        parameters.put("text",text);
        parameters.put("sid",sid);
    }
    @Override
    public Map<String, String> getParams(){
        return parameters;
    }
}
