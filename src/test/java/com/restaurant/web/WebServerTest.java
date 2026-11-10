package com.restaurant.web;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
class WebServerTest{
    private WebServer server;
    private final HttpClient client=HttpClient.newHttpClient();
    @BeforeEach
    void startServer() throws IOException{
        server=new WebServer(0);
        server.start();
    }
    @AfterEach
    void stopServer(){
        server.stop();
    }
    private URI uri(String path){
        return URI.create("http://localhost:"+server.getPort()+path);
    }
    private HttpResponse<String> get(String path) throws Exception{
        HttpRequest request=HttpRequest.newBuilder(uri(path)).GET().build();
        return client.send(request,HttpResponse.BodyHandlers.ofString());
    }
    private HttpResponse<String> post(String path,String body) throws Exception{
        HttpRequest request=HttpRequest.newBuilder(uri(path))
                .header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request,HttpResponse.BodyHandlers.ofString());
    }
    @Test
    void pageIsServed() throws Exception{
        HttpResponse<String> response=get("/");
        assertEquals(200,response.statusCode());
        assertTrue(response.body().contains("Restaurant Ordering System"));
    }
    @Test
    void menuListsMealsAndToppings() throws Exception{
        HttpResponse<String> response=get("/api/menu");
        assertEquals(200,response.statusCode());
        assertTrue(response.body().contains("\"type\":\"PIZZA\""));
        assertTrue(response.body().contains("\"id\":\"BACON\""));
    }
    @Test
    void createdOrderHasTotalWithToppings() throws Exception{
        HttpResponse<String> response=post("/api/orders","type=TAKEAWAY&meal=PIZZA%3AEXTRA_CHEESE&meal=DRINK");
        assertEquals(201,response.statusCode());
        assertTrue(response.body().contains("\"total\":4500"));
        assertTrue(response.body().contains("\"status\":\"NEW\""));
    }
    @Test
    void advancingOrderNotifiesKitchenAndCustomer() throws Exception{
        post("/api/orders","type=DINE_IN&table=3&comment=no+onions&meal=BURGER");
        HttpResponse<String> response=post("/api/orders/1/advance","");
        assertEquals(200,response.statusCode());
        assertTrue(response.body().contains("\"status\":\"COOKING\""));
        assertTrue(response.body().contains("Kitchen: order #1 NEW -> COOKING (note: no onions)"));
        assertTrue(response.body().contains("Customer: your order #1 is being prepared"));
    }
    @Test
    void invalidOrdersAreRejected() throws Exception{
        assertEquals(400,post("/api/orders","type=TAKEAWAY&meal=SUSHI").statusCode());
        assertEquals(400,post("/api/orders","type=TAKEAWAY&meal=DRINK%3ABACON").statusCode());
        assertEquals(400,post("/api/orders","type=TAKEAWAY").statusCode());
        assertEquals(400,post("/api/orders","type=DINE_IN&meal=PIZZA").statusCode());
    }
    @Test
    void unknownOrderIsNotFound() throws Exception{
        assertEquals(404,get("/api/orders/99").statusCode());
    }
    @Test
    void chosenDiscountIsAppliedToTotal() throws Exception{
        HttpResponse<String>response=post("/api/orders","type=TAKEAWAY&discount=STUDENT&meal=PIZZA&meal=DRINK");
        assertEquals(201,response.statusCode());
        assertTrue(response.body().contains("\"discount\":410"));
        assertTrue(response.body().contains("\"total\":3690"));
    }
    @Test
    void unknownDiscountIsRejected() throws Exception{
        assertEquals(400,post("/api/orders","type=TAKEAWAY&discount=FREE&meal=DRINK").statusCode());
    }

    @Test
    void orderMovesThroughAllStatuses() throws Exception{
        post("/api/orders","type=TAKEAWAY&meal=PASTA");
        post("/api/orders/1/advance","");
        post("/api/orders/1/advance","");
        HttpResponse<String> delivered=post("/api/orders/1/advance","");
        HttpResponse<String> extra=post("/api/orders/1/advance","");
        assertTrue(delivered.body().contains("\"status\":\"DELIVERED\""));
        assertTrue(delivered.body().contains("Customer: order #1 delivered, enjoy your meal"));
        assertEquals(delivered.body(),extra.body());
        assertEquals(extra.body(),get("/api/orders/1").body());
    }
    @Test
    void menuAcceptsOnlyGet() throws Exception{
        assertEquals(405,post("/api/menu","").statusCode());
    }
}
