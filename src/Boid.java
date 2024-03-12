
import java.awt.*;

public class Boid {

    static final double VIEW_RANGE = 50;
    static final double SEPARATION_FORCE = 0.6;
    static final double SEPARATION_RANGE = 5;
    static final double ALIGNMENT_FORCE = .5;
    static final double COHESION_FORCE = 0.0125;
    static final double COHESION_RANGE = 50;
    static final double TARGET_FORCE = .06;
    static final double BORDER_MARGIN = 50;
    static final int TURN_FACTOR = 20;
    static final double TARGET_MIN_RANGE = 50;
    static final double TARGET_MAX_RANGE = 250;
    static final double DIF_COLOR_SEP_FORCE = 0.125;
    static final double MAX_SPEED = 55;
    static final double MIN_SPEED = 15;
    static final double DRAG = 1.02;
    static final double BATTLE_RANGE = 10;
    static final double BATTLE_SPEED_MIN = 60;
    static Point target = new Point();
    Color color;
    Point position;
    DoublePoint velocity;
    DoublePoint desiredVelocityChange = new DoublePoint();
    public Boid() {
        position = new Point();
        velocity = new DoublePoint();
    }

    public Boid(Point p, DoublePoint v) {
        position = p;
        velocity = v;
        color = Color.blue;
    }
    public Boid(Point p, DoublePoint v,Color c) {
        position = p;
        velocity = v;
        color = c;
    }

    public void updateVelocity(){
        Point avgPos = new Point();
        Point avgWeightedPos = new Point();
        Point avgWeightedPosOfOppositeColor = new Point();
        DoublePoint avgVelocity = new DoublePoint();
        int surroundingBoids = 0;
        desiredVelocityChange.x = 0;
        desiredVelocityChange.y = 0;

        for (int i = 0; i < BoidFrame.boids.size();i++) {
            Boid checkBoid = BoidFrame.boids.get(i);
            if (checkBoid == this) {
                continue;
            }

            boolean difColor = !this.color.equals(checkBoid.color);

            double distance = this.position.distance(checkBoid.position);

            double xTerm = (velocity.x - checkBoid.velocity.x) * (velocity.x - checkBoid.velocity.x);
            double yTerm = (velocity.y - checkBoid.velocity.y) * (velocity.y - checkBoid.velocity.y);
            double relativeSpeed = Math.sqrt(xTerm + yTerm);

            if (relativeSpeed > BATTLE_SPEED_MIN && distance < BATTLE_RANGE && difColor){
                BoidFrame.boids.remove(i);
                BoidFrame.boids.remove(this);
                return;
            }


            if (distance <= SEPARATION_RANGE) {
                double xDif = (position.x - checkBoid.position.x);
                double yDif = (position.y - checkBoid.position.y);
                avgWeightedPos.translate((int)(xDif* SEPARATION_FORCE),(int)(yDif* SEPARATION_FORCE));
            } else if (distance <= VIEW_RANGE && difColor) {


                double xDif = (position.x - checkBoid.position.x);
                double yDif = (position.y - checkBoid.position.y);
                avgWeightedPosOfOppositeColor.translate((int)(xDif* DIF_COLOR_SEP_FORCE),(int)(yDif* DIF_COLOR_SEP_FORCE));
            }else if (distance <= VIEW_RANGE) {
                surroundingBoids++;
                avgPos.translate(checkBoid.position.x, checkBoid.position.y);
                avgVelocity.translate(checkBoid.velocity.x, checkBoid.velocity.y);
            }
        }



        // drag
        desiredVelocityChange.x /= DRAG;
        desiredVelocityChange.y /= DRAG;

        // Separation
        desiredVelocityChange.x += avgWeightedPos.x;
        desiredVelocityChange.y += avgWeightedPos.y;

        // Color Separation
        desiredVelocityChange.x += avgWeightedPosOfOppositeColor.x;
        desiredVelocityChange.y += avgWeightedPosOfOppositeColor.y;

        if (surroundingBoids != 0) {
            avgVelocity.x /= surroundingBoids;
            avgVelocity.y /= surroundingBoids;
            avgPos.x /= surroundingBoids;
            avgPos.y /= surroundingBoids;

            //cohesion
            if (position.distance(avgPos) > COHESION_RANGE) {
                desiredVelocityChange.x +=  ((-position.x + avgPos.x) * COHESION_FORCE);
                desiredVelocityChange.y += ((-position.y + avgPos.y) * COHESION_FORCE);
            }

            //alignment
            desiredVelocityChange.x +=  ((-velocity.x + avgVelocity.x) * ALIGNMENT_FORCE);
            desiredVelocityChange.y +=  ((-velocity.y + avgVelocity.y) * ALIGNMENT_FORCE);
        }

        velocity = DoublePoint.lerp(velocity, new DoublePoint(velocity.x + desiredVelocityChange.x, velocity.y + desiredVelocityChange.y), 0.75);

        Target(); // move towards target if in range
        AvoidWall();
        fixSpeed();
    }

    public void update() {
        position.x += velocity.x;
        position.y += velocity.y;
        updateVelocity();
    }
    public void fixSpeed() {
        double magnitude = Math.sqrt((velocity.x * velocity.x) + (velocity.y * velocity.y));
        if (magnitude < MIN_SPEED || magnitude > MAX_SPEED) {
            double scale = magnitude < MIN_SPEED ? MIN_SPEED / magnitude : MAX_SPEED / magnitude;
            velocity.x *= scale;
            velocity.y *= scale;
        }
    }

    public void Target(){
        if (target != null && position.distance(target) < TARGET_MAX_RANGE && position.distance(target) > TARGET_MIN_RANGE) { // check if close enough to target to move towards
            // Targeting
            velocity.x += (int) ((-position.x + target.x) * TARGET_FORCE);
            velocity.y += (int) ((-position.y + target.y) * TARGET_FORCE);
        }
    }

    public void AvoidWall(){
        if (position.x < BORDER_MARGIN) {
            velocity.x += TURN_FACTOR;
        }
        if (position.y < BORDER_MARGIN) {
            velocity.y += TURN_FACTOR;
        }
        if (position.x > (BoidFrame.singleton.getWidth()) - BORDER_MARGIN) {
            velocity.x -= TURN_FACTOR;
        }
        if (position.y > (BoidFrame.singleton.getHeight()) - BORDER_MARGIN) {
            velocity.y -= TURN_FACTOR;
        }
    }


}
