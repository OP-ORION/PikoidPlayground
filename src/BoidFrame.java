import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BoidFrame extends JFrame{

    public static final int BOID_DRAW_SIZE = 8;
    public static final int BOID_SPEED_COMPRESSION_CHANGE = 25;
    public static final int BOID_SPEED_LENGTH_CHANGE = 2;
    public static ArrayList<Boid> boids = new ArrayList<Boid>();
    static BoidFrame singleton;

    public BoidFrame(int w, int h, int c) throws Exception {
        if (singleton != null){
            throw new Exception("already running");
        }
        singleton = this;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(w,h));
        setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
        setVisible(true);

        for (int count = 0; count < c/4; count++){
            boids.add(new Boid(new Point((int) (Math.random()*w ), (int) (Math.random()*h)),new DoublePoint((Math.random()*20-10), (Math.random()*20-10)),Color.red));
            boids.add(new Boid(new Point((int) (Math.random()*w ), (int) (Math.random()*h)),new DoublePoint( (Math.random()*20-10), (Math.random()*20-10)),Color.blue));
            boids.add(new Boid(new Point((int) (Math.random()*w ), (int) (Math.random()*h)),new DoublePoint( (Math.random()*20-10), (Math.random()*20-10)),Color.green));
            boids.add(new Boid(new Point((int) (Math.random()*w ), (int) (Math.random()*h)),new DoublePoint( (Math.random()*20-10), (Math.random()*20-10)),Color.yellow));
        }


        initComponents();

    }

    private void drawBoid(Boid boid,Graphics g){
        g.setColor(boid.color);

        // Calculate direction angle
        double angle = Math.atan2(boid.velocity.y, boid.velocity.x);
        double speed = Math.sqrt((boid.velocity.x*boid.velocity.x) + (boid.velocity.y*boid.velocity.y))/ BOID_SPEED_COMPRESSION_CHANGE;

        // Calculate the three vertices of the triangle
        int[] xPoints = {
                (int) (boid.position.x + (Math.cos(angle) * BOID_DRAW_SIZE)*speed*BOID_SPEED_LENGTH_CHANGE),
                (int) (boid.position.x + (Math.cos(angle + Math.PI * 3 / 4) * BOID_DRAW_SIZE)/speed),
                (int) (boid.position.x + (Math.cos(angle - Math.PI * 3 / 4) * BOID_DRAW_SIZE)/speed)
        };
        int[] yPoints = {
                (int) (boid.position.y + (Math.sin(angle) * BOID_DRAW_SIZE)*speed*BOID_SPEED_LENGTH_CHANGE),
                (int) (boid.position.y + (Math.sin(angle + Math.PI * 3 / 4) * BOID_DRAW_SIZE)/speed),
                (int) (boid.position.y + (Math.sin(angle - Math.PI * 3 / 4) * BOID_DRAW_SIZE)/speed)
        };

        g.fillPolygon(xPoints, yPoints, 3);
    }

    private void initComponents() {

        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(Color.BLACK);
                Point mousePos = getMousePosition();
                Boid.target = mousePos;
                if (mousePos != null) {
                    g.drawOval((int) (mousePos.x - Boid.TARGET_MAX_RANGE / 2), (int) (mousePos.y - Boid.TARGET_MAX_RANGE / 2), (int) Boid.TARGET_MAX_RANGE, (int) Boid.TARGET_MAX_RANGE);
                }

                for (Boid boid : boids) {
                    drawBoid( boid, g);
                }

            }

        };

        panel.setPreferredSize(new Dimension(getWidth(),getHeight()));
        panel.setSize(new Dimension(getWidth(), getHeight()));
        panel.setBackground(new Color(162, 161, 171));
        add(panel,BorderLayout.CENTER);

        //update panel
        new Timer(45, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                panel.repaint();
            }
        }).start();
        //update boids
        new Timer(45, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                for (int i = 0; i < boids.size();i++) {
                    boids.get(i).update();
                }
            }
        }).start();

    }
}
