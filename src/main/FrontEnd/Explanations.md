Explanation for the Front End

### File Structure
 -> src
    -> main
        -> FrontEnd
            -> AccountPage
            -> ManagerPage
            -> MarketPage
            -> showPage
            -> UserLoginRegistration
            -> RequestReviewPage
            -> StockPage
 
### Explanations
We used Java Swing to make our frontend. Each of the classes does the following:

1) AccountPage:
   @Instance variables: userName, frame, viewAccountsPanel, and loginPage store the user's name, the main frame of the GUI, the panel to view accounts, and a reference to the login page, respectively.
   - Constructor: public AccountPage(String userName, UserLoginRegistration us) initializes the userName and loginPage instance variables.
   - Functions:
     - public void run(): This method sets up the main frame with a tabbed pane that contains two tabs: "View Accounts" and "Create Account". It also adds a "Logout" panel to the frame.
     - private JPanel createViewAccountsPanel(): This method creates a panel to display the user's trading accounts in a list format. Each account includes an "Enter" button that, when clicked, navigates to the StockPage for that account.
     - private JPanel createCreateAccountPanel(): This method creates a panel for users to create a new trading account. The user enters an initial balance and clicks the "Create Account" button to submit a request for a new trading account.
     - private void refreshViewAccountsPanel(): This method refreshes the viewAccountsPanel to update the displayed list of trading accounts after a new account is created.
     - private JPanel createLogoutPanel(): This method creates a panel with a "Logout" button. When clicked, the application closes the current window and returns to the login page.

2) ManagerPage: 

It contains three main sections:
    - Accounts Info and Profits: This section lists all trading accounts with their respective details and profits.
    - Market Stocks: This section lists all stocks in the market along with their quantities and current prices. The manager can also update the price of a stock in this section.
    - Log: This section displays the log of all trading activities in the system.
  
Methods:
    - run(): Initializes and displays the Manager page's frame and its components.
    - createAccountsInfoScrollPane(): Creates the JScrollPane for the accounts info and profits section.
    - createMarketStocksScrollPane(): Creates the JScrollPane for the market stocks section.
    - createLogScrollPane(): Creates the JScrollPane for the log section.
    - refresh(): Refreshes the Manager page by disposing the current frame and running the run() method again.

3) MarketPage:

It has two main sections:
    - Market Stocks: This section lists all stocks in the market along with their quantities and current prices.
    - Buy/Sell: This section allows the user to buy or sell stocks by entering the stock name and quantity.

Methods:
    - run(): Initializes and displays the Market page's frame and its components.
    - createMarketPanel(): Creates the main panel for the market stocks section.
    - createStockListScrollPane(): Creates the JScrollPane for the market stocks list.
    - createMarketStockPanel(): Creates a JPanel for each stock in the market.
    - buyStockPanel(): Creates the panel for buying and selling stocks.

4) showPage class:
   This class has a single main method, which is the entry point for the application. 
   The method first calls Database.createTables() to create necessary tables in the database. 
   Then it runs a new instance of the UserLoginRegistration class using SwingUtilities.invokeLater() to ensure the GUI runs on the Event Dispatch Thread.

5) StockPage class:
   This class represents the stock management page. It displays the account information, user's stock holdings, and allows the user to sell their stocks.

   - StockPage(int accountNumber, AccountPage accountPage): Constructor takes account number and account page as arguments and initializes the corresponding fields.
   - run(): Sets up the stock management page's GUI and makes it visible.
   - createStockListScrollPane(boolean isUserStocks): A helper method to create a scroll pane with a list of stocks. If `isUserStocks` is true, the list will show user's holdings; otherwise, it would show market stocks.
   - createUserStockPanel(CustomerStock holding): A helper method to create a panel displaying a user's stock holding information and a button to sell the stock.
   - refresh(): A method to refresh the stock management page by disposing the current frame and creating a new one.

6) UserLoginRegistration class:
   This class represents the login and registration page of the application. It provides a way for users to log in or register a new account.

   - run(): Sets up the login and registration page's GUI and makes it visible.
   - createLoginPanel(): A helper method to create the login panel, which includes fields for entering the user's name and password and a button to log in.
   - createRegistrationPanel(): A helper method to create the registration panel, which includes fields for entering the user's name, password, role, account number, and a button to register.
   - switchToAccountPage(String ownerName) : A method to switch to the account page after a successful login.
   - switchToManagerPage(String adminName): A method to switch to the manager page after a successful login.

7) RequestReviewPage class:
   - run() method: This method initializes the JFrame with the title "Request Review" and sets the default close operation to DISPOSE_ON_CLOSE. 
        It configures the layout of the main panel and initializes the table for displaying account requests. 
        The table model and action listeners for the approve and reject buttons are also set up within this method. 
   - loadAccountRequests(DefaultTableModel tableModel): This method is responsible for loading the account requests from the database and populating the table with the request data. 
      It executes an SQL query to fetch all rows from the account_requests table and adds each row to the table model.