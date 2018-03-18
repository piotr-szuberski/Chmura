package chmura.przykladProducenciKonsumenci;

import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;

import java.util.*;

public class Main {
    public static int ILOSC_OPERACJI = 5;
    public static int LIMIT_PRODUKTOW = 2;
    public static LinkedList<Produkt> PRODUKTY = new LinkedList<>();
    public static Chmura CHMURA = new Chmura((x, y) -> y < 0 || y > LIMIT_PRODUKTOW);
    public static Collection<Byt> BYTY_KONSUMENTA = Collections.synchronizedList(new ArrayList<>());
    public static Collection<Byt> BYTY_PRODUCENTA = Collections.synchronizedList(new ArrayList<>());
    public static Random rand = new Random();

    public static void main(String[] args) {
        Main main = new Main();
        Byt bytKonsumenta = null;
        Byt bytProducenta = null;
        try {
            bytKonsumenta = CHMURA.ustaw(0, LIMIT_PRODUKTOW);
            bytProducenta = CHMURA.ustaw(1, 0);
        } catch (InterruptedException e) {
            System.exit(1);
        }
        BYTY_KONSUMENTA.add(bytKonsumenta);
        BYTY_PRODUCENTA.add(bytProducenta);
        Producent producent = new Producent(1);
        Konsument konsument = new Konsument(1);
        producent.start();
        konsument.start();
        try {
            producent.join();
            konsument.join();
        } catch (InterruptedException e) {
            System.exit(2);
        }

        try {
            CHMURA.kasuj(bytKonsumenta);
            CHMURA.kasuj(bytProducenta);
        } catch (NiebytException e) {
            System.err.println("Brak bytu");
            System.exit(3);
        }
    }
}
