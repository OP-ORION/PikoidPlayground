public class DoublePoint {
    public double x;
    public double y;

    public DoublePoint() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(double addX, double addY){
        x += addX;
        y += addY;
    }

    public double distance(DoublePoint other){
        return Math.sqrt(((this.x - other.x) * (this.x - other.x)) * ((this.y - other.y) * (this.y - other.y)));
    }

    public static DoublePoint lerp(DoublePoint a, DoublePoint b, double t) {
        return new DoublePoint(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
    }


}