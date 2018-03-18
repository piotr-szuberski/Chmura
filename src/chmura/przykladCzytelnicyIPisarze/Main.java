package chmura.przykladCzytelnicyIPisarze;

import chmura.Byt;
import chmura.Chmura;
import chmura.NiebytException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

public class Main {
    public static int LICZBA_OBROTOW_PETLI = 2;
    public static int CZEKAJACY_PISARZE = 0, CZEKAJACY_CZYTELNICY = 0, PISZACY_PISARZE = 0, CZYTAJACY_CZYTELNICY = 0;
    public static Chmura CHMURA = new Chmura((x, y) -> y < 0);
    public static Collection<Byt> BYTY_PISARZE = Collections.synchronizedList(new ArrayList<>());
    public static Collection<Byt> BYTY_CZYTELNICY = Collections.synchronizedList(new ArrayList<>());
    public static Collection<Byt> BYTY_MUTEX = Collections.synchronizedList(new ArrayList<>());
    public static Random rand = new Random();

    public static void main(String[] args) {
        Byt bytPisarze = null;
        Byt bytCzytelnicy = null;
        Byt bytMutex = null;
        try {
            bytPisarze = CHMURA.ustaw(0, 0);
            bytCzytelnicy = CHMURA.ustaw(1, 0);
            bytMutex = CHMURA.ustaw(2, 1);
        } catch (InterruptedException e) {
            System.exit(1);
        }

        BYTY_MUTEX.add(bytMutex);
        BYTY_PISARZE.add(bytPisarze);
        BYTY_CZYTELNICY.add(bytCzytelnicy);

        Czytelnik[] czytelnicy = new Czytelnik[4];
        for (int i = 0; i < 4; ++i) {
            czytelnicy[i] = new Czytelnik(i+1);
            czytelnicy[i].start();
        }

        Pisarz[] pisarze = new Pisarz[2];
        for (int i = 0; i < 2; ++i) {
            pisarze[i] = new Pisarz(i+1);
            pisarze[i].start();
        }
        try {
            for (int i = 0; i < 4; ++i)
                czytelnicy[i].join();
            for (int i = 0; i < 2; ++i)
                pisarze[i].join();
        } catch (InterruptedException e) {
            System.exit(2);
        }
        try {
            CHMURA.kasuj(bytCzytelnicy);
            CHMURA.kasuj(bytPisarze);
            CHMURA.kasuj(bytMutex);
        } catch (NiebytException e) {
            System.err.println("Brak bytu");
            System.exit(3);
        }
    }

}
