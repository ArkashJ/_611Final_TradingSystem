Explaining the txt files in this package:

Our program creates 4 txt files as follows:
a) accounts.txt - displays account information for registered users 
### includes id, userName, balance and accountType
b) stock.txt - diplays stock information for all stocks
### includes stockName, stockSymbol, stockPrice, stockQuantity, high and low price
c) users.txt
### includes index, userName, password, ownerType
d) customer_stocks.txt
### includes userID, stockSymbol, stockPrice, stockQuantity

- The stocks txt file is read by the database and the information is stored in the database. 
- The database is then used to display the stock information to the user. 
- The user can then buy or sell stocks and the db is updated with the new stock information.
- The user can then view their account information and the database is updated with the new account information. The user can then log out and the program will terminate.

