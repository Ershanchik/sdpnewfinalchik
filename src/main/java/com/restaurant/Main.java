package com.restaurant;
import com.restaurant.console.ConsoleMenu;
import com.restaurant.web.WebServer;
import java.io.IOException;
public class Main{
    public static void main(String[] args) throws IOException{
        System.out.println("Restaurant Ordering System");
        if(args.length > 0&&args[0].equals("web")){
            WebServer server=new WebServer(8080);
            server.start();
            System.out.println("Open http://localhost:8080 in the browser");
        } else{
            new ConsoleMenu().run();
        }
    }
}
