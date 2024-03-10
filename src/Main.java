
import java.awt.*;
import java.util.Random;


public class Main {
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 1024;
    private static final int BOID_COUNT =1000;


    public static void main(String[] args) {
        EventQueue.invokeLater(()-> {
            try {
                new BoidFrame(WIDTH, HEIGHT, BOID_COUNT);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}














