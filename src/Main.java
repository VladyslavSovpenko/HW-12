import java.util.concurrent.*;

public class Main {

    static int hydrogenCount = 0;
    static Phaser phaserBlock = new Phaser(4);
    static int phase;
    static boolean end = false;

    public static void main(String[] args) {

        ExecutorService HydrogenEX = Executors.newSingleThreadExecutor();
        ExecutorService OxygenEx = Executors.newSingleThreadExecutor();

        phase = phaserBlock.getPhase();

        OxygenEx.execute(() -> releaseOxygen());

        HydrogenEX.execute(() -> releaseHydrogen());

        for (; ; ) {

            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (phaserBlock.getArrivedParties() == 3) {
                System.out.print(", ");
                phaserBlock.arrive();
                if (phase > 2) {
                    end = true;
                    break;
                }
            }
            phase = phaserBlock.getPhase();
        }
        OxygenEx.shutdown();
        HydrogenEX.shutdown();
    }

    static void releaseOxygen() {
        for (; ; ) {
            if (end) {
                break;
            }
            System.out.print("O");
            phaserBlock.arriveAndAwaitAdvance();
        }
    }

    static void releaseHydrogen() {
        for (; ; ) {
            hydrogenCount++;
            if (end) {
                break;
            }
            if (hydrogenCount == 2) {
                System.out.print("H");
                hydrogenCount = 0;
                phaserBlock.arriveAndAwaitAdvance();
            } else {
                System.out.print("H");
                phaserBlock.arrive();
            }
        }
    }
}