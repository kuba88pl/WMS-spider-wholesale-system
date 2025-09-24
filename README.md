# Spiders Wholesale System 
Order management application written in Spring Boot. Application allows to add customers, products (spiders - in this case), create and manage orders. Data is strored in mySQL database.
## Contents

- [Functionality](#functionality)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Licence](#licence)
- [Contact](#contact)

## Functionality

- **Add Customer:** Add a new customer, with a name, last name, unique ID and contact infromations like an addrress,
 parcel locker, telephone number and email .
- **Remove Customer:** Remove customers by their ID.
- **Add Spider:** Add a new spider, with species, type names, quantity, size and price.
- **Remove Spider:** Remove spiders by their ID.
- **Add Order:** Add a new order, with unique ID, customer and spiders loaded from a mySQL database.
- **Display List:** Show all customers, spiders and orders in the list with pagination.
- **Data Persistence:** Automatically saves and loads the customers, spiders and orders list from a mySQL database.

## Requirements

- Java Development Kit (JDK) 8 or later.

## Getting Started

1.  Clone the repository:
```bash
git clone [[https://github.com/YourUsername/user-manager-cli.git] (https://github.com/kuba88pl/WMS-spider-wholesale-system.git)
```
2. Crewate EMPTY database. For example:

```bash
CREATE database wms_spiders_wholesale_system;
```

The application connects to a MySQL database. The connection parameters are defined in the src/main/resources/application.properties file. You may need to adjust these values to fit your local environment or a different server.
```bash
spring.datasource.url=jdbc:mysql://database_url
spring.datasource.username=database_user
spring.datasource.password=database_password
```
3. Navigate to the project directory.
```bash
cd WMS-spider-wholesale-system
```
4. Build the project:
Use Maven to build the project and create a runnable .jar dile.
```bash
mvn clean package
```
5. Run the application
```bash
java -jar target/spiders-wholesale-system-0.0.1-SNAPSHOT.jar
```
After this steps application is running. Open your internet browswer and type url: 
```bash
http://localhost:8080/index.html
```

