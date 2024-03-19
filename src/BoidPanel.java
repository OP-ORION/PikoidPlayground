import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BoidPanel extends JPanel {
    static boolean debugUgly = false; //Background thing
    static boolean debugText = true;
    int BOID_DRAW_SIZE = 8;
    int BOID_SPEED_COMPRESSION_CHANGE = 25;
    int BOID_SPEED_LENGTH_CHANGE = 2;
    double MIN_BOID_DRAW_SPEED = 0.8;// fixes divide by zero error
    double velocityRange = 20;
    public int delay = 45;

    // stuff below ignores presets
    public long lastDelay = 0;
    Grid grid;
    public ArrayList<Boid> boids = new ArrayList<Boid>();
    BoidSettings settings;
    public BoidPanel(int boidCount){
        setPreferredSize(new Dimension(getWidth(),getHeight()));
        setSize(new Dimension(getWidth(), getHeight()));
        setBackground(new Color(162, 161, 171));

        int w = getWidth();
        int h = getHeight();
        grid = new Grid(w, h,settings.VIEW_RANGE);

        for (int count = 0; count < boidCount/4; count++){
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint((Math.random()*velocityRange-(velocityRange/2)), (Math.random()*velocityRange-(velocityRange/2))),Color.red,settings));
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint( (Math.random()*velocityRange-(velocityRange/2)), (Math.random()*velocityRange-(velocityRange/2))),Color.blue,settings));
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint( (Math.random()*velocityRange-(velocityRange/2)), (Math.random()*velocityRange-(velocityRange/2))),Color.green,settings));
            boids.add(new Boid(new DoublePoint( (Math.random()*w ),(Math.random()*h)),new DoublePoint( (Math.random()*velocityRange-(velocityRange/2)), (Math.random()*velocityRange-(velocityRange/2))),Color.yellow,settings));
        }

        //update panel
        new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                repaint();
            }
        }).start();

        //update boids
        new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                long startTime = System.currentTimeMillis();
                grid.changeDimension(getWidth(),getHeight());// update grid size to current panel size (this also clears the boids)
                for (Boid boid : boids) {
                    grid.insertBoid(boid); // update the grid
                }

                List<Boid> toRemove = new LinkedList<>();
                for (Boid boid : boids) {
                    if (boid.markedForRemoval) {
                        toRemove.add(boid);
                    }
                }
                boids.removeAll(toRemove);

                for(Boid boid : boids) {
                    boid.update(grid);
                }
                lastDelay = System.currentTimeMillis() - startTime;
            }
        }).start();

        // update boids bounds when panel resizes
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                settings.boundWidth = getWidth();// update boids bounds when panel resizes
                settings.boundHeight = getHeight();// update boids bounds when panel resizes
            }
        });

    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLACK);
        Point mousePos = getMousePosition();
        Boid.target = mousePos;

        //Background Color
        if (debugUgly) {
            int gridWidth = (int)Math.ceil(getWidth() / grid.cellSize);
            int gridHeight = (int)Math.ceil(getHeight() / grid.cellSize);
            Color[][] cellColors = new Color[gridWidth][gridHeight];

            for (Boid boid : boids) {
                List<Point> neighborCells = grid.getNeighborCells(boid, settings.VIEW_RANGE);
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
            g.drawOval((int) (mousePos.x - settings.TARGET_MAX_RANGE / 2), (int) (mousePos.y - settings.TARGET_MAX_RANGE / 2), (int) settings.TARGET_MAX_RANGE, (int) settings.TARGET_MAX_RANGE);
            g.drawOval((int) (mousePos.x - settings.TARGET_MIN_RANGE / 2), (int) (mousePos.y - settings.TARGET_MIN_RANGE / 2), (int) settings.TARGET_MIN_RANGE, (int) settings.TARGET_MIN_RANGE);
        }
        for (Boid boid : boids) {
            drawBoid( boid, g);
        }

        if (debugText) {
            g.setColor(Color.black);
            g.fillRect(0,0, 100, 30);

            g.setFont(new Font("Helvetica", Font.PLAIN, 16));
            g.setColor(Color.GREEN);
            g.drawString("Delay : " + lastDelay + "ms", 0, 12);
            g.drawString("Count : " + boids.size(), 0, 25);
        }
    }
    private void drawBoid(Boid boid,Graphics g){
        g.setColor(boid.color);

        // Calculate direction angle
        double angle = Math.atan2(boid.velocity.y, boid.velocity.x);
        double speed = Math.sqrt((boid.velocity.x*boid.velocity.x) + (boid.velocity.y*boid.velocity.y))/ BOID_SPEED_COMPRESSION_CHANGE;
        if (speed < MIN_BOID_DRAW_SPEED){
            speed = MIN_BOID_DRAW_SPEED;
        }

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
}
