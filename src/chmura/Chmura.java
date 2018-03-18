package chmura;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;

public class Chmura {
    private Map<Byt, int[]> mapaBytow = new ConcurrentHashMap<>();
    private BiPredicate<Integer, Integer> stan;
    private final List<int[]> wspolrzedneDoUstawienia = Collections.synchronizedList(new LinkedList<>());
    private Map<List<int[]>, int[]> mapaPrzestawien = new ConcurrentHashMap<>();

    // buduje chmurę, która w stanie początkowym nie ma żadnego bytu.
    public Chmura() {
        stan = (x, y) -> false;
    }

    // buduje chmurę, której początkową zawartość określa dwuargumentowy predykat stan.
    // W miejscu (x, y) jest BYT wtedy i tylko wtedy, gdy stan.test(x, y) ma wartość true.
    public Chmura(BiPredicate<Integer, Integer> stan) {
        this.stan = stan;
    }

    // daje jako wynik nowy BYT, dodany do chmury w miejscu (x, y).
    public synchronized Byt ustaw(int x, int y) throws InterruptedException {
        Byt nowyByt = new Byt();
        int[] doUstawienia = {x, y};
        wspolrzedneDoUstawienia.add(doUstawienia);

        while (czyWspolrzednaJestZajeta(x, y))
            wait();

        wspolrzedneDoUstawienia.remove(doUstawienia);
        mapaBytow.put(nowyByt, doUstawienia);

        // Budzi byty ktore moga zostac przestawione/ustawione np. po przestawieniu kilku elementow
        if (czyDaSieCosUstawic() || czyDaSieCosPrzestawic())
            notifyAll();

        return nowyByt;
    }

    // przemieszcza na raz wszystkie byty kolekcji byty o wektor (dx, dy).
    // Byt z miejsca (x, y) trafia na miejsce (x + dx, y + dy).
    // Jeśli którykolwiek z bytów kolekcji byty nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
    // Jeżeli wymaga tego niezmiennik chmury, metody ustaw() i przestaw() wstrzymują wątek do czasu
    // gdy ich wykonanie będzie możliwe. W przypadku przerwania zgłaszają wyjątek InterruptedException.
    public synchronized void przestaw(Collection<Byt> byty, int dx, int dy)
            throws NiebytException, InterruptedException {

        List<int[]> wspolrzedneBytow = Collections.synchronizedList(new LinkedList<>());
        aktualizujWspolrzedne(byty, wspolrzedneBytow);
        int[] przestawienie = {dx, dy};

        while (czyZajeteWspolrzedneDocelowe(wspolrzedneBytow, przestawienie)) {
            mapaPrzestawien.put(wspolrzedneBytow, przestawienie);
            wait();
            aktualizujWspolrzedne(byty, wspolrzedneBytow);
            mapaPrzestawien.remove(wspolrzedneBytow);
        }

        for (Byt byt : byty) {
            int[] wspolrzedna = mapaBytow.get(byt);
            wspolrzedna[0] += przestawienie[0];
            wspolrzedna[1] += przestawienie[1];
        }

        // jesli po wykonaniu przestaw zwolnilo sie miejsce do ustawienia/przestawienia
        if (czyDaSieCosUstawic() || czyDaSieCosPrzestawic())
            notifyAll();
    }

    // usuwa BYT z chmury.
    // Jeśli BYT nie jest w chmurze, metoda zgłasza wyjątek NiebytException.
    public synchronized void kasuj(Byt byt) throws NiebytException {

        int[] miejsce = miejsce(byt);

        if (miejsce == null)
            throw new NiebytException();
        else
            mapaBytow.remove(byt);

        // po skasowaniu budzi watki ktore teraz moga cos ustawic/przestawic
        if (czyDaSieCosUstawic() || czyDaSieCosPrzestawic())
            notifyAll();
    }

    //daje dwuelementową tablicę ze współrzędnymi x i y bytu, lub null, jeśli BYT nie jest w chmurze.
    public synchronized int[] miejsce(Byt byt) {
        return mapaBytow.getOrDefault(byt, null);
    }

    private synchronized void aktualizujWspolrzedne(Collection<Byt> byty, List<int[]> wspolrzedne)
            throws NiebytException {

        wspolrzedne.clear();
        for (Byt byt : byty) {
            if (!mapaBytow.containsKey(byt))
                throw new NiebytException();
            wspolrzedne.add(mapaBytow.get(byt));
        }
    }

    private synchronized boolean czyZajeteWspolrzedneDocelowe(List<int[]> wspolrzedneBytow, int[] przestawienie) {
        for (int[] wspolrzedna : wspolrzedneBytow) {
            int x = wspolrzedna[0] + przestawienie[0];
            int y = wspolrzedna[1] + przestawienie[1];
            if (stan.test(x, y))
                return true;

            for (Map.Entry<Byt, int[]> paryMapy : mapaBytow.entrySet()) {
                int x2 = paryMapy.getValue()[0];
                int y2 = paryMapy.getValue()[1];
                if (x == x2 && y == y2)
                    return true;
            }
        }
        return false;
    }

    private boolean czyDaSieCosPrzestawic() {
        for (Map.Entry<List<int[]>, int[]> przestawienia : mapaPrzestawien.entrySet()) {
            int[] ilePrzestawic = przestawienia.getValue();
            List<int[]> lista = przestawienia.getKey();

            if (!czyZajeteWspolrzedneDocelowe(lista, ilePrzestawic))
                return true;
        }
        return false;
    }

    private synchronized boolean czyWspolrzednaJestZajeta(int x, int y) {
        if (stan.test(x, y))
            return true;

        for (Map.Entry<Byt, int[]> para : mapaBytow.entrySet()) {
            if (para.getValue()[0] == x && para.getValue()[1] == y)
                return true;
        }
        return false;
    }

    private boolean czyDaSieCosUstawic() {
        for (int[] wspolrzednaDoUstawienia : wspolrzedneDoUstawienia) {
            int x = wspolrzednaDoUstawienia[0];
            int y = wspolrzednaDoUstawienia[1];
            if (!czyWspolrzednaJestZajeta(x, y))
                return true;
        }
        return false;
    }
}
