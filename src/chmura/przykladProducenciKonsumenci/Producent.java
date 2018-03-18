package chmura.przykladProducenciKonsumenci;

import chmura.NiebytException;

public class Producent extends Thread {
    private int numer;

    public Producent(int numer) {
        this.numer = numer;
    }

    private void wlasneSprawy() throws InterruptedException {
        System.out.println("Producent: wlasne sprawy");
        sleep(Main.rand.nextInt(12));
    }

    @Override
    public void run() {
        super.run();
        int numerProduktu = 1;
        for (int i = 0; i < Main.ILOSC_OPERACJI; ++i) {
            try {
                wlasneSprawy();
                Produkt nowyProdukt = new Produkt(numerProduktu);
                numerProduktu++;
                Main.CHMURA.przestaw(Main.BYTY_PRODUCENTA, 0, 1);
                Main.PRODUKTY.add(nowyProdukt);
                System.out.println("Producent: wstawia produkt nr " + nowyProdukt.numer);
                System.out.println("Producent: kontener zawiera " + Main.PRODUKTY.size() + " produktow");
                Main.CHMURA.przestaw(Main.BYTY_KONSUMENTA, 0, -1);
            } catch (NiebytException e) {
                System.out.println("Producent: Niebyt Exception");
                System.exit(5);
            } catch (InterruptedException e) {
                System.out.println("Producent: Watek przerwany");
                System.exit(6);
            }
        }
    }
}
