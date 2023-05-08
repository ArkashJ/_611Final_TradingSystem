# _611Final_TradingSystem
This project is a simulation of a trading system which has the following features

1) Main Portfolio Manager (Singleton class) 
2) Database of stocks (txt) files to trade, update, or hold
3) People - customers and clients
4) Services - stock market
5) Accounts to trade, deposit, withdraw money
6) Money Manager for mananging money
7) Personal Page

All of this is displayed in a GUI built on Java Swing

## Presentation
A Presentation explaining everything we did
[Stock Trading System.pdf](https://github.com/ArkashJ/_611Final_TradingSystem/files/11422753/Stock.Trading.System.pdf)

## Infrastructure - 1st iteration
[InitialStructure.pdf](https://github.com/ArkashJ/_611Final_TradingSystem/files/11223661/InitialStructure.pdf)


# CS611 Final Project

Arkash Jain
arkjain@bu.edu
U58748927

Trisha Anil
tcanil@bu.edu

Jianxio Yang
yangjx@bu.edu

## Files
|
| > main
## -> Accounts
        |> Account.java
        |> ITrading.java
        |> OptionsAccount.java
        |> TradingAccount.java
        |> TradingAccountFactory.java

##   -> Database
        |> Database.java


##    -> Enums
        |> UserType.java


##    -> ModifiedFrontend
        |> AccManagement.java
        |> Explanations.md
        |> AccountPage.java
        |> CreateAcc.java
        |> OptionsAccount.java
        |> RegisterPage.java
        |> StockMarket.java
        |> StockPage.java


##     -> Initiator
        |> Initiator.java


##     -> Log
        |> logSystem.java


##     -> Persons
        |> Client.java
        |> CustomerFactory.java
        |> Person.java
        |> Manager.java
        |> IPerson.java


##     -> PotfolioManager
        |> BankManager.java
        |> Trading.java


##     -> Stocks
        |> CustomerStock.java
        |> CustomerStocks.java
        |> Market.java
        |> MarketStock.java
        |> Stock.java
        |> StockFactory.java


##     -> txtFiles
        |> accounts.txt
        |> stocks.txt
        |> users.txt
        |> customer_stocks.txt
        |> Description.md


##     -> Utils
        |> Profit_Loss.java
        |> Notify.java


##    -> Main.java


### Workflow:
- Workflow initiation: Read text files and begin the process
- User Login Page: The starting point for users
    - Login option:
        - Click the login button, retrieve username and password, and verify login credentials using the database class
        - Proceed if successful, otherwise display a failed login attempt message
    - Registration option:
        - Provide name, password, and role, then initiate user registration in the database
        - Add to the database if no conflicts are detected
        - Automatically transition to the Account Page
        - Display all user accounts on the Account Page, iterating through each account to retrieve the user's trading account from the database's accounts table
        - Create a new account, and set the Enter button to trigger an event that displays stocks on the Stock Page
        - To proceed, input an initial balance and submit an Account Request to the Database Page, which the manager can review from the account requests table; users can also view this information
        - Continuously calculate profit to determine eligibility for additional account types; if eligible, add a new account type
- Bank Manager Functions:
    - Consolidate buy and sell operations in this section
    - Record all transaction prices in a log
    - Implement a sell function in the trading class, allowing queries by stock name only
    - Handle cases where customers purchased stocks at different prices by only displaying the stock name
    - If the requested sell quantity is unavailable, display an error message; otherwise, update the account
    - Update the market as the fourth step
    - Buy Stock:
        - If sufficient balance is available, purchase the stock and update the customer's stock table
- Manager Page:
    - Display market, log, and user information
    - Use the GetStocks function to retrieve market stocks
- Account Page Notifications: Trigger notifications when selling a stock, calculate realized profit, and call the associated event

### main
This folder contains all the java files for the project
## Accounts
1. ITrading.java - Interface for trading. It has methods for depositing and withdrawing money

2. OptionsAccount.java - class makes the options account for the users 
   - including ownername, 
   - customerStocks, 
   - balance, 
   - and accountNumber

3. TradingAccount.java - class makes the trading account. 
   - It implements the ITrading interface and allows users to handle their money including:
   - deposits, 
   - withdrawals, 
   - finding profits, 
   - updating and checking their account balance 
   - and getting account details

4. TradingAccountFactory.java - Factory class for creating trading and options accounts

## Database
1. Database.java - class for reading and writing to the database. 
   - It initializes the connection string and test username and password,
   - makes a singleton class and 
   - creates tables for 
     - users, 
     - accounts, 
     - customerstocks, 
     - market 
     - and the log. 
   - Furthermore, it has functions for 
     - logging in, 
     - registering users, 
     - making a trading account, 
     - getting stocks, 
     - getting customer stocks 
     - and options accounts.
   - We can call this class to insert data into tables as well

## Enums
1. UserType.java - enum for the different types of users
   - ADMIN,
   - USER

## ModifiedFrontend
1. AccManagement.java - class for managing accounts. 
   - Page to view accounts or create an account
2. AccountPage.java - class for logging in and registering users
3. CreateAcc.java - extension for create AccManagement, to create an account and let the user choose their initial balance
4. OptionsAccount.java - class for creating options accounts. 
   - Display the name of the owner,
   - and the profit made
5. RegisterPage.java - class for registering users. 
   - It has a page for the user to enter their name, 
   - username, 
   - password, 
   - type of account they want to create
   - and monthly income
6. StockMarket.java - class for displaying the stock market. allows users to buy and sell stocks by entering the name and quantity
7. StockPage.java - class for displaying the stock page. 
   - It has a page for the user to view their balance, account number and profit

## Initiator
1. initiator.java - singleton class for:
- getting the path of the database and insert
- loading data into the db
- inserting market data

## Log
1. LogSystem.java - class for logging the trades made by the users

## Persons
1. Client.java - class for creating clients. 
   - It has a page for the user to create their account
   - display account
   - transfer money 
   - return all trading and options accounts
2. CustomerFactory.java - Factory class for creating customers
3. IPerson.java - interface for the person class to transfer money and get authentication
4. Person.java - class for creating a person. 
   - It has a page for getting username, password
   - setting password
5. Manager.java - extends the person class and defines the portfolio manager

## PotfolioManager
1. BankManager.java - singleton class for managing the bank. 
   - Map of a map Calculates profits for all users
   - map to find a single users profits
   - update stock prices
   - get profit for a user
2. Trading.java - singleton class to update the sql table whenever a stock is bought or sold

## Stocks
1. CustomerStock.java - class for creating customer stocks. 
   - Stores the stock name, number of stocks, and the price at which the stocks were bought. 
   - Method to calculate the profit made by the customer on the stocks. 
   - Method to set the number of stocks and the price at which the stocks were bought.
2. CustomerStocks.java - initializes an account number and makes a list of stocks for the customer. It can add and remove stocks from the list
3. Market.java - This class represents the market, a singleton class
   - it can get all the stocks in the market
   - set, add or remove them
   - get the closing time, opening time and set them
4. MarketStock.java - singleton class for creating market stocks by calling the market class
5. Stock.java - class for creating stocks. The stock class initializes the basic element for a stock, including:
   - name, 
   - quantity, 
   - time at which the stock was bought
   - high price, low price, divident and price it was bought at
6. StockFactory.java - Factory class for creating stocks


## txtFiles
1. accounts.txt - text file for storing the accounts
2. stocks.txt - text file for storing the stocks
3. users.txt - text file for storing the users
4. customer_stocks.txt - text file for storing the customer stocks
5. Description.md - description of the project

## Utils
1. Profit_Loss.java - class for calculating the profit or loss made by the user
2. Notify.java - class for notifying the user if they are eligible for an options account

## Design Patterns

We have used the following design patterns:

1. Singleton -  Database, initiator, protfolio manager, market, market stock, bank manager, trading account factory, customer factory
2. Factory Method - Trading account factory, customer factory, stock factory
3. Strategy Pattern - Person and Trading interfaces are examples of horizontal inheritance


### Extendability

1. Future teams can improve the UI by simply changing the backend connections from the old one to the new one
2. Options account can be added to the factory and options can easily be traded by adding classed in stocks, making a table in the database and adding a new page in the frontend
3. We realize that the database class can be refractored to make service classes. Future teams can make those classes, initialize DB connections and simply copy paste our code there for it to work.

## How to compile and run

These are my current versions:

```
openjdk version "1.8.0_362"
OpenJDK Runtime Environment (Temurin)(build 1.8.0_362-b09)
OpenJDK 64-Bit Server VM (Temurin)(build 25.362-b09, mixed mode)
```

Head to the root directory of this project (where this README is located) and run:

```
javac -cp . -d bin main/*.java
java -cp bin main.Main
```

<img width="1061" alt="UML" src="https://user-images.githubusercontent.com/86805856/235495321-e508ad66-e6ca-4f8d-b340-a286f986a4d4.png">


