package com.restaurant.web;
import com.restaurant.decorator.Topping;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public final class OrderJson{
    private OrderJson(){
    }
    public static String menu(MealFactory factory){
        String meals=Stream.of(MealType.values())
                .map(type->{
                    Meal meal=factory.createMeal(type);
                    return String.format(Locale.ROOT,
                            "{\"type\":%s,\"description\":%s,\"price\":%.0f,\"allowsToppings\":%b}",
                            quote(type.name()),quote(meal.getDescription()),meal.getPrice(),type.allowsToppings());
                })
                .collect(Collectors.joining(","));
        String toppings=Stream.of(Topping.values())
                .map(topping->String.format(Locale.ROOT,
                        "{\"id\":%s,\"title\":%s,\"price\":%.0f}",
                        quote(topping.name()),quote(topping.getTitle()),topping.getPrice()))
                .collect(Collectors.joining(","));
        return "{\"meals\":["+meals+"],\"toppings\":["+toppings+"]}";
    }
    public static String order(Order order,List<String> notifications){
        String meals=order.getMeals().stream()
                .map(meal->String.format(Locale.ROOT,
                        "{\"description\":%s,\"price\":%.0f}",quote(meal.getDescription()),meal.getPrice()))
                .collect(Collectors.joining(","));
        String messages=notifications.stream()
                .map(OrderJson::quote)
                .collect(Collectors.joining(","));
        return String.format(Locale.ROOT,
                "{\"id\":%d,\"status\":%s,\"type\":%s,\"table\":%d,\"comment\":%s,"
                        +"\"meals\":[%s],\"total\":%.0f,\"notifications\":[%s]}",
                order.getId(),quote(order.getStatus().name()),quote(order.getType().name()),
                order.getTableNumber(),quote(order.getComment()),meals,order.getTotal(),messages);
    }
    public static String error(String message){
        return "{\"error\":"+quote(message)+"}";
    }
    static String quote(String text){
        StringBuilder result=new StringBuilder("\"");
        for(char c:text.toCharArray()){
            switch(c){
                case '"' -> result.append("\\\"");
                case '\\' -> result.append("\\\\");
                case '\n' -> result.append("\\n");
                case '\r' -> result.append("\\r");
                case '\t' -> result.append("\\t");
                default ->{
                    if(c<0x20){
                        result.append(String.format("\\u%04x",(int)c));
                    }else{
                        result.append(c);
                    }
                }
            }
        }
        return result.append('"').toString();
    }
}