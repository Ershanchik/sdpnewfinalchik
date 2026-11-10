package com.restaurant.web;
import com.restaurant.decorator.Topping;
import com.restaurant.discount.DiscountOption;
import com.restaurant.facade.RestaurantFacade;
import com.restaurant.model.Meal;
import com.restaurant.model.MealType;
import com.restaurant.model.Order;
import com.restaurant.model.OrderType;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public class WebServer{
    private static final String JSON="application/json; charset=utf-8";
    private final RestaurantFacade facade=new RestaurantFacade();
    private final HttpServer server;
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
        try(InputStream page=WebServer.class.getResourceAsStream("/web/index.html")){
            if(page==null){
                send(exchange,500,"index.html is missing","text/plain; charset=utf-8");
                return;
            }
            send(exchange,200,new String(page.readAllBytes(),StandardCharsets.UTF_8),"text/html; charset=utf-8");
        }
    }
    private void handleMenu(HttpExchange exchange) throws IOException{
        if(!exchange.getRequestMethod().equals("GET")){
            send(exchange,405,OrderJson.error("Method not allowed"),JSON);
            return;
        }
        send(exchange,200,OrderJson.menu(facade::createMeal),JSON);
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
            Optional<Order> found=parts.length>=4?findOrder(parts[3]):Optional.empty();
            if(found.isEmpty()){
                send(exchange,404,OrderJson.error("Order not found"),JSON);
            }else if(parts.length==4&&method.equals("GET")){
                send(exchange,200,orderJson(found.get()),JSON);
            }else if(parts.length==5&&parts[4].equals("advance")&&method.equals("POST")){
                send(exchange,200,orderJson(facade.advanceOrder(found.get().getId())),JSON);
            }else{
                send(exchange,405,OrderJson.error("Method not allowed"),JSON);
            }
        }catch(IllegalArgumentException|IllegalStateException e){
            send(exchange,400,OrderJson.error(e.getMessage()),JSON);
        }
    }
    private void createOrder(HttpExchange exchange) throws IOException{
        String body=new String(exchange.getRequestBody().readAllBytes(),StandardCharsets.UTF_8);
        FormData form=FormData.parse(body);
        OrderType type=parseEnum(OrderType.class,form.get("type","DINE_IN"),"order type");
        DiscountOption discount=parseEnum(DiscountOption.class,form.get("discount","NONE"),"discount");
        int table=0;
        if(type==OrderType.DINE_IN){
            String tableText=form.get("table","0");
            if(!tableText.matches("\\d{1,2}")){
                throw new IllegalArgumentException("Table number must be a number from 1 to 99");
            }
            table=Integer.parseInt(tableText);
        }
        List<Meal> meals=new ArrayList<>();
        for(String entry:form.getAll("meal")){
            meals.add(parseMeal(entry));
        }
        Order order=facade.placeOrder(type,table,form.get("comment",""),discount,meals);
        send(exchange,201,orderJson(order),JSON);
    }
    private Meal parseMeal(String entry){
        String[] parts=entry.split(":",2);
        MealType type=parseEnum(MealType.class,parts[0],"meal");
        List<Topping> toppings=new ArrayList<>();
        if(parts.length==2&&!parts[1].isEmpty()){
            for(String name:parts[1].split(",")){
                toppings.add(parseEnum(Topping.class,name,"topping"));
            }
        }
        return facade.createMeal(type,toppings);
    }
    private Optional<Order> findOrder(String idText){
        if(!idText.matches("\\d{1,9}")){
            return Optional.empty();
        }
        return facade.findOrder(Integer.parseInt(idText));
    }
    private String orderJson(Order order){
        return OrderJson.order(order,facade.getNotifications(order.getId()));
    }
    private static <E extends Enum<E>> E parseEnum(Class<E> type,String value,String what){
        try{
            return Enum.valueOf(type,value);
        }catch(IllegalArgumentException e){
            throw new IllegalArgumentException("Unknown "+what+": "+value);
        }
    }
    private static void send(HttpExchange exchange,int status,String body,String contentType) throws IOException{
        byte[] bytes=body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type",contentType);
        exchange.sendResponseHeaders(status,bytes.length);
        try(OutputStream out=exchange.getResponseBody()){
            out.write(bytes);
        }
    }
}
