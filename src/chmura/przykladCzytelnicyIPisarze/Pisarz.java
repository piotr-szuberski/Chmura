package chmura.przykladCzytelnicyIPisarze;

import chmura.NiebytException;



public class Pisarz extends Thread {
    private int numer;

    public Pisarz(int numer) {
        this.numer = numer;
    }

    private void robiCos() {
        try {
            sleep(Main.rand.nextInt(500));
        } catch (InterruptedException e) {
            System.exit(10);
        }
    }

    @Override
    public void run() {
        super.run();
        for(int i = 0; i < Main.LICZBA_OBROTOW_PETLI; ++i) {
            robiCos();
            try {
                Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, -1);
                System.out.println("Pisarz nr " + numer + " wchodzi do budynku");
                if (Main.PISZACY_PISARZE + Main.CZYTAJACY_CZYTELNICY > 0) {
                    System.out.println("Pisarz nr " + numer + " czeka");
                    ++Main.CZEKAJACY_PISARZE;
                    System.out.println("W poczekalni jest " + Main.CZEKAJACY_PISARZE + " pisarzy");
                    Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, 1);
                    Main.CHMURA.przestaw(Main.BYTY_PISARZE, 0, -1);
                    --Main.CZEKAJACY_PISARZE;
                }
                System.out.println("Pisarz nr " + numer + " wchodzi do czytelni");
                ++Main.PISZACY_PISARZE;
                System.out.println("W czytelni jest " + Main.PISZACY_PISARZE + " pisarz");
                System.out.println("Pisarz nr " + numer + " pisze");
                Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, 1);
                robiCos();
                System.out.println("Pisarz nr " + numer + " konczy pisac");
                Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, -1);
                --Main.PISZACY_PISARZE;
                System.out.println("W czytelni jest " + Main.PISZACY_PISARZE + " pisarzy");
                if (Main.CZEKAJACY_CZYTELNICY > 0) {
                    System.out.println("Pisarz nr " + numer + " budzi czytelnikow");
                    Main.CHMURA.przestaw(Main.BYTY_CZYTELNICY, 0, 1);
                } else if (Main.CZEKAJACY_PISARZE > 0) {
                    System.out.println("Pisarz nr " + numer + " budzi pisarzy");
                    Main.CHMURA.przestaw(Main.BYTY_PISARZE, 0, 1);
                } else {
                    Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, 1);
                }
            } catch (NiebytException e) {
                System.err.println("Problem z chmura");
                System.exit(11);
            } catch (InterruptedException e) {
                System.exit(12);
            }
        }
    }
}
