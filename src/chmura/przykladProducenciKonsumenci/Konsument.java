package chmura.przykladProducenciKonsumenci;

import chmura.NiebytException;

public class Konsument extends Thread {
    private int numer;

    public Konsument(int numer) {
        this.numer = numer;
    }

    private void wlasneSprawy() throws InterruptedException {
        System.out.println("Konsument: wlasne sprawy");
        sleep(Main.rand.nextInt(15));
    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < Main.ILOSC_OPERACJI; ++i) {
            try {
                wlasneSprawy();
                Main.CHMURA.przestaw(Main.BYTY_KONSUMENTA, 0, 1);
                Produkt pobranyProdukt = Main.PRODUKTY.remove();
                System.out.println("Konsument: Zabral produkt numer " + pobranyProdukt.numer);
                System.out.println("Konsument: kontener zawiera " + Main.PRODUKTY.size() + " produktow");
                Main.CHMURA.przestaw(Main.BYTY_PRODUCENTA, 0, -1);
            } catch (NiebytException e) {
                System.out.println("Konsument: Niebyt Exception");
                System.exit(3);
            } catch (InterruptedException e) {
                System.out.println("Konsument: Watek przerwany");
                System.exit(4);
            }
        }
    }
}
