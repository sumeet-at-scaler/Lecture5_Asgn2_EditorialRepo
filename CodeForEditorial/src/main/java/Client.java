import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);
        int a = scn.nextInt();
        int b = scn.nextInt();

        Adder adder = new Adder(a, b);
        Thread t = new Thread(adder);
        t.start();
    }
}