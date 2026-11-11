# Restaurant Ordering System

Final project for Software Design Patterns. A restaurant ordering application with a console menu and a web menu.
Java 17, Maven and JUnit 5. The application itself uses only the standard Java library.

**GitHub repository:** https://github.com/Ershanchik/sdpnewfinalchik

## Features
- Choose meals (pizza, burger, pasta, drink) and extra toppings (cheese, bacon, mushrooms).
- Create dine-in orders with a table number or takeaway orders, with an optional kitchen comment.
- Apply a student discount (10%), happy hour (drinks half price), or a promo code discount.
- Move orders through NEW -> COOKING -> READY -> DELIVERED with notifications for the kitchen and customer.
- Use the console menu or the web menu.

## Design patterns

| Pattern | Main classes | Responsible student |
|---|---|---|
| Factory | `MealFactory` | Yerulan |
| Builder | `OrderBuilder` | Yerulan |
| Decorator | `MealDecorator`, `ExtraCheese`, `Bacon`, `Mushrooms` | Sherkhan |
| Strategy | `DiscountStrategy`, `StudentDiscount`, `HappyHourDiscount`, `PromoCodeDiscount` | Sherkhan |
| Observer | `OrderObserver`, `KitchenDisplay`, `CustomerNotifier` | Nurken |
| Facade | `RestaurantFacade` (used by `ConsoleMenu` and `WebServer`) | Nurken |

The class and sequence UML diagrams are located in `docs/`.

## Requirements
- JDK 17 or newer
- Apache Maven 3.8 or newer

## Build and run

Build the application and run all tests:

```powershell
mvn clean package
```

Start the console version:

```powershell
java -jar target/restaurant-ordering-system-1.0-SNAPSHOT.jar
```

Start the web version:

```powershell
java -jar target/restaurant-ordering-system-1.0-SNAPSHOT.jar web
```

Open `http://localhost:8080` in a browser after starting the web version.

## Usage
Choose the order type, enter a table number for dine-in, and optionally add a comment for the kitchen. Choose a discount, then add meals. Pizza, burger and pasta can have extra toppings. Enter `0` to finish the order. In the console, press Enter to move the order through its statuses and view kitchen/customer notifications. In the web version, use **Move to next status**.

## Tests

```powershell
mvn test
```

The test suite covers meals, toppings, discounts, order building, observers, facade, console input and HTTP endpoints.

## Project structure

```text
src/main/java/com/restaurant/
    model/       meals, orders, statuses, interfaces
    factory/     MealFactory
    decorator/   topping decorators and Topping
    builder/     OrderBuilder
    discount/    discount strategies and DiscountOption
    observer/    KitchenDisplay and CustomerNotifier
    facade/      RestaurantFacade
    console/     ConsoleMenu and OrderPrinter
    web/         WebServer, OrderJson and FormData
src/main/resources/web/index.html
src/test/java/com/restaurant/      JUnit tests
docs/                              UML diagrams and screenshots
```

## Further work
- Add payments through an external service (Adapter).
- Support cancelling or undoing orders (Command).
- Save orders in a database.
- Add request-size limits and authentication to the web application.
