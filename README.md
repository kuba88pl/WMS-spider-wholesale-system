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
