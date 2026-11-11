# Real screenshots for the final submission

The screenshots for this commit must show **the real application running**, not generated examples. The bundled `03_web_menu_cart.png`, `04_web_order_discount.png`, and `05_web_status_notifications.png` were captured from a locally run copy of the actual Java web server, if present.

Please capture the remaining screenshots from your own IntelliJ IDEA / PowerShell / browser:

1. `01_console_order_setup.png`: console choosing order type, discount, pizza and toppings.
2. `02_console_receipt_statuses.png`: completed receipt and actual NEW -> COOKING -> READY -> DELIVERED messages.
3. `03_web_menu_cart.png`: actual menu cards and shopping cart.
4. `04_web_order_discount.png`: submitted order with discount shown.
5. `05_web_status_notifications.png`: after clicking Move to next status, showing notifications.
6. `06_maven_tests.png`: successful final `mvn clean test` output in the IntelliJ terminal, including Tests run / Failures / Errors / BUILD SUCCESS.

You should use `mvn clean test` on your own environment to verify the result, even if previous local Java/JUnit-compatible checks passed. Do not submit a fake screenshot of a successful Maven run.

Run commands from the directory that contains `pom.xml`:

    mvn clean test
    mvn package
    java -jar target/restaurant-ordering-system-1.0-SNAPSHOT.jar
    java -jar target/restaurant-ordering-system-1.0-SNAPSHOT.jar web

Use Windows Snipping Tool (`Win+Shift+S`) for images from your machine.
