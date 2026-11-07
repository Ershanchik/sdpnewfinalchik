package com.restaurant.web;
import com.restaurant.builder.OrderBuilder;
import com.restaurant.decorator.Topping;
import com.restaurant.discount.DiscountOption;
import com.restaurant.factory.MealFactory;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
import com.restaurant.observer.CustomerNotifier;
import com.restaurant.observer.KitchenDisplay;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class WebServer{
    private static final String JSON="application/json; charset=utf-8";
    // HttpServer runs handlers one by one on its default executor, so plain collections are enough here
    private final Map<Integer,TrackedOrder> orders=new HashMap<>();
    private final MealFactory factory=new MealFactory();
    private final HttpServer server;
    private int nextOrderId=1;
    private record TrackedOrder(Order order,List<String> notifications){
    }
    public WebServer(int port) throws IOException{
        server=HttpServer.create(new InetSocketAddress(port),0);
        server.createContext("/",this::handlePage);
        server.createContext("/api/menu",this::handleMenu);
        server.createContext("/api/orders",this::handleOrders);
    }
    public void start(){
        server.start();
    }
    public void stop(){
        server.stop(0);
    }
    public int getPort(){
        return server.getAddress().getPort();
    }
    private void handlePage(HttpExchange exchange) throws IOException{
        String path=exchange.getRequestURI().getPath();
        if(!path.equals("/")&&!path.equals("/index.html")){
            send(exchange,404,"Not found","text/plain; charset=utf-8");
            return;
        }
        try (InputStream page=WebServer.class.getResourceAsStream("/web/index.html")){
            if(page==null){
                send(exchange,500,"index.html is missing","text/plain; charset=utf-8");
                return;
            }
            send(exchange,200,new String(page.readAllBytes(),StandardCharsets.UTF_8),"text/html; charset=utf-8");
        }
    }
    private void handleMenu(HttpExchange exchange) throws IOException{
        send(exchange,200,OrderJson.menu(factory),JSON);
    }
    private void handleOrders(HttpExchange exchange) throws IOException{
        String path=exchange.getRequestURI().getPath();
        String method=exchange.getRequestMethod();
        try{
            if(path.equals("/api/orders")&&method.equals("POST")){
                createOrder(exchange);
                return;
            }
            String[] parts=path.split("/");
            TrackedOrder tracked=parts.length>=4 ? findOrder(parts[3]) : null;
            if(tracked==null){
                send(exchange,404,OrderJson.error("Order not found"),JSON);
            } else if(parts.length==4&&method.equals("GET")){
                send(exchange,200,OrderJson.order(tracked.order(),tracked.notifications()),JSON);
            } else if(parts.length==5&&parts[4].equals("advance")&&method.equals("POST")){
                tracked.order().advanceStatus();
                send(exchange,200,OrderJson.order(tracked.order(),tracked.notifications()),JSON);
            } else{
                send(exchange,405,OrderJson.error("Method not allowed"),JSON);
            }
        } catch(IllegalArgumentException | IllegalStateException e){
            send(exchange,400,OrderJson.error(e.getMessage()),JSON);
        }
    }
    private void createOrder(HttpExchange exchange) throws IOException{
        String body=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
        FormData form=FormData.parse(body);
        OrderType type=parseEnum(OrderType.class,form.get("type","DINE_IN"),"order type");
        OrderBuilder builder=new OrderBuilder(nextOrderId).type(type).comment(form.get("comment",""));
        DiscountOption discount=parseEnum(DiscountOption.class,form.get("discount","NONE"),"discount");
        builder.discount(discount.createStrategy());
        if(type==OrderType.DINE_IN){
            String table=form.get("table","0");
            if(!table.matches("\\d{1,2}")){
                throw new IllegalArgumentException("Table number must be a number from 1 to 99");
            }
            builder.table(Integer.parseInt(table));
        }
        for(String entry : form.getAll("meal")){
            builder.addMeal(parseMeal(entry));
        }
        Order order=builder.build();
        nextOrderId++;
        List<String> notifications=new ArrayList<>();
        order.addObserver(new KitchenDisplay(notifications::add));
        order.addObserver(new CustomerNotifier(notifications::add));
        orders.put(order.getId(),new TrackedOrder(order,notifications));
        send(exchange,201,OrderJson.order(order,notifications),JSON);
    }
    // entry looks like "PIZZA" or "PIZZA:EXTRA_CHEESE,BACON"
    private Meal parseMeal(String entry){
        String[] parts=entry.split(":",2);
        MealType type=parseEnum(MealType.class,parts[0],"meal");
        Meal meal=factory.createMeal(type);
        if(parts.length==2&&!parts[1].isEmpty()){
            if(!type.allowsToppings()){
                throw new IllegalArgumentException(type.getTitle()+" cannot have toppings");
            }
            for(String name : parts[1].split(",")){
                meal=parseEnum(Topping.class,name,"topping").applyTo(meal);
            }
        }
        return meal;
    }
    private TrackedOrder findOrder(String idText){
        if(!idText.matches("\\d{1,9}")){
            return null;
        }
        return orders.get(Integer.parseInt(idText));
    }
    private static <E extends Enum<E>> E parseEnum(Class<E> type,String value,String what){
        try{
            return Enum.valueOf(type,value);
        } catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Unknown "+what+": "+value);
        }
    }
    private static void send(HttpExchange exchange,int status,String body,String contentType) throws IOException{
        byte[] bytes=body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type",contentType);
        exchange.sendResponseHeaders(status,bytes.length);
        try (OutputStream out=exchange.getResponseBody()){
            out.write(bytes);
        }
    }
}
