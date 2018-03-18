package chmura.przykladCzytelnicyIPisarze;

import chmura.NiebytException;

public class Czytelnik extends Thread {
    private int numer;

    public Czytelnik(int numer) {
        this.numer = numer;
    }

    private void robiCos() {
        try {
            sleep(Main.rand.nextInt(200));
        } catch (InterruptedException e) {
            System.exit(20);
        }
    }

    @Override
    public void run() {
        super.run();
        for (int i = 0; i < Main.LICZBA_OBROTOW_PETLI; ++i) {
            try {
                robiCos();
                Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, -1);
                System.out.println("Czytelnik nr " + numer + " wchodzi do budynku");
                if (Main.PISZACY_PISARZE + Main.CZEKAJACY_PISARZE > 0) {
                    ++Main.CZEKAJACY_CZYTELNICY;
                    System.out.println("Czytelnik nr " + numer + " czeka");
                    System.out.println("W poczekalni jest " + Main.CZEKAJACY_CZYTELNICY + " czytelnikow");
                    Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, 1);
                    Main.CHMURA.przestaw(Main.BYTY_CZYTELNICY, 0, -1);
                    --Main.CZEKAJACY_CZYTELNICY;
                }
                System.out.println("Czytelnik nr " + numer + " wchodzi do czytelni");
                ++Main.CZYTAJACY_CZYTELNICY;
                System.out.println("W czytelni jest " + Main.CZYTAJACY_CZYTELNICY + " czytelnikow");
                if (Main.CZEKAJACY_CZYTELNICY > 0) {
                    System.out.println("Czytelnik nr " + numer + " budzi innych czytelnikow");
                    Main.CHMURA.przestaw(Main.BYTY_CZYTELNICY, 0, 1);
                } else {
                    Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, 1);
                }
                System.out.println("Czytelnik nr " + numer + " czyta");
                robiCos();
                Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, -1);
                System.out.println("Czytelnik nr " + numer + " przestaje czytac");
                --Main.CZYTAJACY_CZYTELNICY;
                System.out.println("W czytelni jest " + Main.CZYTAJACY_CZYTELNICY + " czytelnikow");
                if (Main.CZYTAJACY_CZYTELNICY == 0 && Main.CZEKAJACY_PISARZE > 0) {
                    System.out.println("Czytelnik nr " + numer + " budzi pisarzy");
                    Main.CHMURA.przestaw(Main.BYTY_PISARZE, 0, 1);
                } else {
                    Main.CHMURA.przestaw(Main.BYTY_MUTEX, 0, 1);
                }

            } catch (NiebytException e) {
                System.err.println("Problem z chmura");
                System.exit(21);
            } catch (InterruptedException e) {
                System.exit(22);
            }
        }
    }
}
