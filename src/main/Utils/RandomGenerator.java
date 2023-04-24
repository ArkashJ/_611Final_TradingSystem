package main.Utils;

import java.util.Random;

public class RandomGenerator {
    private static Random accountNumber=new Random();

    //Creates a 6-digit account number
    public static int getAccountNumber() {
        return 100000 + accountNumber.nextInt(900000);
    }

}
