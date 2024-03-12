import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BoidFrame extends JFrame{
    public static boolean debugUgly = false; //Background thing
    public static boolean debugText = true;
    public static final int BOID_DRAW_SIZE = 8;
    public static final int BOID_SPEED_COMPRESSION_CHANGE = 25;
    public static final int BOID_SPEED_LENGTH_CHANGE = 2;
    public static ArrayList<Boid> boids = new ArrayList<Boid>();
    static BoidFrame singleton;
    Grid grid;
    public long lastDelay = 0;
    public long boidCount = 0;
    public BoidFrame(int w, int h, int c) throws Exception {
        if (singleton != null){
            throw new Exception("already running");
        }
        singleton = this;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(w,h));
        grid = new Grid(w, h, BoidSettings.VIEW_RANGE);
        setLayout(new BoxLayout(getContentPane(),BoxLayout.X_AXIS));
        setVisible(true);

        for (int count = 0; count < c/4; count++){
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint((Math.random()*20-10), (Math.random()*20-10)),Color.red));
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint( (Math.random()*20-10), (Math.random()*20-10)),Color.blue));
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint( (Math.random()*20-10), (Math.random()*20-10)),Color.green));
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint( (Math.random()*20-10), (Math.random()*20-10)),Color.yellow));
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
        g.setColor(Color.black);
        g.drawPolygon(xPoints, yPoints, 3);
    }

    private void initComponents() {

        JPanel panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(Color.BLACK);
                Point mousePos = getMousePosition();
                Boid.target = mousePos;

                //Background Color
                if (BoidFrame.debugUgly) {
                    int gridWidth = (int)Math.ceil(getWidth() / grid.cellSize);
                    int gridHeight = (int)Math.ceil(getHeight() / grid.cellSize);
                    Color[][] cellColors = new Color[gridWidth][gridHeight];

                    for (Boid boid : boids) {
                        List<Point> neighborCells = grid.getNeighborCells(boid, BoidSettings.VIEW_RANGE);
                        for (Point cell : neighborCells) {
                            if (cell.x >= 0 && cell.x < gridWidth && cell.y >= 0 && cell.y < gridHeight) {
                                if (cellColors[cell.x][cell.y] == null) {
                                    cellColors[cell.x][cell.y] = Color.GRAY;
                                }
                            }
                        }
                    }

                    for (Boid boid : boids) {
                        int cellX = (int)(boid.position.x / grid.cellSize);
                        int cellY = (int)(boid.position.y / grid.cellSize);
                        if (cellX >= 0 && cellX < gridWidth && cellY >= 0 && cellY < gridHeight) {
                            cellColors[cellX][cellY] = boid.color;
                        }
                    }

                    for (int x = 0; x < gridWidth; x++) {
                        for (int y = 0; y < gridHeight; y++) {
                            if (cellColors[x][y] != null) {
                                if (cellColors[x][y] == Color.GRAY) continue; //Dont wanna show surrounding color..
                                g.setColor(cellColors[x][y]);
                                g.fillRect(x * (int)grid.cellSize, y * (int)grid.cellSize, (int)grid.cellSize, (int)grid.cellSize);
                            }
                        }
                    }
                }


                if (mousePos != null) {
                    g.setColor(Color.GREEN);
                    g.drawOval((int) (mousePos.x - BoidSettings.TARGET_MAX_RANGE / 2), (int) (mousePos.y - BoidSettings.TARGET_MAX_RANGE / 2), (int) BoidSettings.TARGET_MAX_RANGE, (int) BoidSettings.TARGET_MAX_RANGE);
                }
                for (Boid boid : boids) {
                    drawBoid( boid, g);
                }

                if (BoidFrame.debugText) {
                    g.setColor(Color.black);
                    g.fillRect(0,0, 100, 30);

                    g.setFont(new Font("Helvetica", Font.PLAIN, 16));
                    g.setColor(Color.GREEN);
                    g.drawString("Delay : " + lastDelay + "ms", 0, 12);
                    g.drawString("Count : " + boids.size(), 0, 25);
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
                long startTime = System.currentTimeMillis();
                grid.clear();
                for (Boid boid : BoidFrame.boids) {
                    grid.insertBoid(boid); // Populate the grid
                }

                List<Boid> toRemove = new LinkedList<>();
                for (Boid boid : BoidFrame.boids) {
                    if (boid.markedForRemoval) {
                        toRemove.add(boid);
                    }
                }
                BoidFrame.boids.removeAll(toRemove);

                for(Boid boid : BoidFrame.boids) {
                    boid.update(grid);
                }
                lastDelay = System.currentTimeMillis() - startTime;
            }
        }).start();

    }
}
