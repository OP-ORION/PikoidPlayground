import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class Boid {
    static Point target = new Point();
    Color color;
    Point position;
    DoublePoint velocity;
    boolean markedForRemoval = false;
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

    public void updateVelocity(Grid grid){
        Point avgPos = new Point();
        Point avgWeightedPos = new Point();
        Point avgWeightedPosOfOppositeColor = new Point();
        DoublePoint avgVelocity = new DoublePoint();
        int surroundingBoids = 0;
        desiredVelocityChange.x = 0;
        desiredVelocityChange.y = 0;

        List<Boid> localBoids = grid.getNeighbors(this, BoidSettings.VIEW_RANGE);

        for (Boid checkBoid : localBoids) {
            if (checkBoid == this) {
                continue;
            }

            boolean difColor = !this.color.equals(checkBoid.color);

            double distance = this.position.distance(checkBoid.position);

            double xTerm = (velocity.x - checkBoid.velocity.x) * (velocity.x - checkBoid.velocity.x);
            double yTerm = (velocity.y - checkBoid.velocity.y) * (velocity.y - checkBoid.velocity.y);
            double relativeSpeed = Math.sqrt(xTerm + yTerm);

            if (relativeSpeed > BoidSettings.BATTLE_SPEED_MIN && distance < BoidSettings.BATTLE_RANGE && difColor) {
                checkBoid.markedForRemoval = true;
                this.markedForRemoval = true;
                return;
            }


            if (distance <= BoidSettings.SEPARATION_RANGE) {
                double xDif = (position.x - checkBoid.position.x);
                double yDif = (position.y - checkBoid.position.y);
                avgWeightedPos.translate((int)(xDif* BoidSettings.SEPARATION_FORCE),(int)(yDif* BoidSettings.SEPARATION_FORCE));
            } else if (distance <= BoidSettings.VIEW_RANGE && difColor) {


                double xDif = (position.x - checkBoid.position.x);
                double yDif = (position.y - checkBoid.position.y);
                avgWeightedPosOfOppositeColor.translate((int)(xDif* BoidSettings.DIF_COLOR_SEP_FORCE),(int)(yDif* BoidSettings.DIF_COLOR_SEP_FORCE));
            }else if (distance <= BoidSettings.VIEW_RANGE) {
                surroundingBoids++;
                avgPos.translate(checkBoid.position.x, checkBoid.position.y);
                avgVelocity.translate(checkBoid.velocity.x, checkBoid.velocity.y);
            }
        }



        // drag
        desiredVelocityChange.x /= BoidSettings.DRAG;
        desiredVelocityChange.y /= BoidSettings.DRAG;

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
            if (position.distance(avgPos) > BoidSettings.COHESION_RANGE) {
                desiredVelocityChange.x +=  ((-position.x + avgPos.x) * BoidSettings.COHESION_FORCE);
                desiredVelocityChange.y += ((-position.y + avgPos.y) * BoidSettings.COHESION_FORCE);
            }

            //alignment
            desiredVelocityChange.x +=  ((-velocity.x + avgVelocity.x) * BoidSettings.ALIGNMENT_FORCE);
            desiredVelocityChange.y +=  ((-velocity.y + avgVelocity.y) * BoidSettings.ALIGNMENT_FORCE);
        }

        velocity = DoublePoint.lerp(velocity, new DoublePoint(velocity.x + desiredVelocityChange.x, velocity.y + desiredVelocityChange.y), 0.75);

        Target(); // move towards target if in range
        AvoidWall();
        fixSpeed();
    }

    public void update(Grid grid) {
        position.x += velocity.x;
        position.y += velocity.y;
        updateVelocity(grid);
    }
    public void fixSpeed() {
        double magnitude = Math.sqrt((velocity.x * velocity.x) + (velocity.y * velocity.y));
        if (magnitude < BoidSettings.MIN_SPEED || magnitude > BoidSettings.MAX_SPEED) {
            double scale = magnitude < BoidSettings.MIN_SPEED ? BoidSettings.MIN_SPEED / magnitude : BoidSettings.MAX_SPEED / magnitude;
            velocity.x *= scale;
            velocity.y *= scale;
        }
    }

    public void Target(){
        if (target != null && position.distance(target) < BoidSettings.TARGET_MAX_RANGE && position.distance(target) > BoidSettings.TARGET_MIN_RANGE) { // check if close enough to target to move towards
            // Targeting
            velocity.x += (int) ((-position.x + target.x) * BoidSettings.TARGET_FORCE);
            velocity.y += (int) ((-position.y + target.y) * BoidSettings.TARGET_FORCE);
        }
    }

    public void AvoidWall(){
        if (position.x < BoidSettings.BORDER_MARGIN) {
            velocity.x += BoidSettings.TURN_FACTOR;
        }
        if (position.y < BoidSettings.BORDER_MARGIN) {
            velocity.y += BoidSettings.TURN_FACTOR;
        }
        if (position.x > (BoidFrame.singleton.getWidth()) - BoidSettings.BORDER_MARGIN) {
            velocity.x -= BoidSettings.TURN_FACTOR;
        }
        if (position.y > (BoidFrame.singleton.getHeight()) - BoidSettings.BORDER_MARGIN) {
            velocity.y -= BoidSettings.TURN_FACTOR;
        }
    }


}
