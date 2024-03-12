import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Grid {
    private final List<Boid>[][] cells;
    public final double cellSize;
    private final int width;
    private final int height;

    @SuppressWarnings("unchecked")
    public Grid(int width, int height, double cellSize) {
        this.cellSize = cellSize;
        this.width = (int)Math.ceil(width / cellSize);
        this.height = (int)Math.ceil(height / cellSize);
        cells = (List<Boid>[][]) new List[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                cells[i][j] = new LinkedList<>();
            }
        }
    }

    public void clear() {
        for (List<Boid>[] cellRow : cells) {
            for (List<Boid> cell : cellRow) {
                cell.clear();
            }
        }
    }

    public void insertBoid(Boid boid) {
        int cellX = (int)(boid.position.x / cellSize);
        int cellY = (int)(boid.position.y / cellSize);
        if(cellX >= 0 && cellX < width && cellY >= 0 && cellY < height) {
            cells[cellX][cellY].add(boid);
        }
    }


    public List<Boid> getNeighbors(Boid boid, double distance) {
        List<Boid> neighbors = new LinkedList<>();
        int cellRadius = (int)Math.ceil(distance / cellSize);

        int centerCellX = (int)(boid.position.x / cellSize);
        int centerCellY = (int)(boid.position.y / cellSize);

        int minCellX = Math.max(centerCellX - cellRadius, 0);
        int maxCellX = Math.min(centerCellX + cellRadius, width - 1);
        int minCellY = Math.max(centerCellY - cellRadius, 0);
        int maxCellY = Math.min(centerCellY + cellRadius, height - 1);

        for (int i = minCellX; i <= maxCellX; i++) {
            for (int j = minCellY; j <= maxCellY; j++) {
                for (Boid neighbor : cells[i][j]) {
                    if (boid != neighbor && boid.position.distance(neighbor.position) <= distance) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }
        return neighbors;
    }

    public List<Point> getNeighborCells(Boid boid, double distance) {
        List<Point> neighbors = new LinkedList<>();
        int cellRadius = (int)Math.ceil(distance / cellSize);

        int centerCellX = (int)(boid.position.x / cellSize);
        int centerCellY = (int)(boid.position.y / cellSize);

        int minCellX = Math.max(centerCellX - cellRadius, 0);
        int maxCellX = Math.min(centerCellX + cellRadius, width - 1);
        int minCellY = Math.max(centerCellY - cellRadius, 0);
        int maxCellY = Math.min(centerCellY + cellRadius, height - 1);

        for (int i = minCellX; i <= maxCellX; i++) {
            for (int j = minCellY; j <= maxCellY; j++) {
                if (i == centerCellX && j == centerCellY) continue;
                neighbors.add(new Point(i, j));
            }
        }
        return neighbors;
    }
}
