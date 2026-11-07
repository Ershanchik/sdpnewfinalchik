package com.restaurant.web;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public final class FormData{
    private final Map<String,List<String>> values=new HashMap<>();
    private FormData(){
    }
    public static FormData parse(String body){
        FormData form=new FormData();
        if(body==null||body.isBlank()){
            return form;
        }
        for(String pair : body.split("&")){
            if(pair.isEmpty()){
                continue;
            }
            int eq=pair.indexOf('=');
            String key=decode(eq < 0 ? pair : pair.substring(0,eq));
            String value=eq < 0 ? "" : decode(pair.substring(eq+1));
            form.values.computeIfAbsent(key,k -> new ArrayList<>()).add(value);
        }
        return form;
    }
    public String get(String key,String defaultValue){
        List<String> list=values.get(key);
        return list==null ? defaultValue : list.get(0);
    }
    public List<String> getAll(String key){
        return values.getOrDefault(key,List.of());
    }
    private static String decode(String text){
        return URLDecoder.decode(text,StandardCharsets.UTF_8);
    }
}
