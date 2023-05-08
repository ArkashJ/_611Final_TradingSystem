package main.Utils;

/**
 * @Description: This class is used to notify all customers if a trade over 10k is made
 */
public class Notify {
    private static Notify notify = new Notify();
    private Notify() {}
    public static void toAll(String message) {
        System.out.println(message);
    }
}
