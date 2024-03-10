import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {

    int lastX = 0;
    int lastY = 0;
    boolean skipFirstDragEvent = false;

    public MouseListener(){
    }
    private void left(MouseEvent e){

    }
    private void right(MouseEvent e){
    }
    private void leftDrag(MouseEvent e){

    }
    private void rightDrag(MouseEvent e){

    }

    @Override
    public void mousePressed(MouseEvent e) {


        if (e.getButton() == 1){
            left(e);
        }else if (e.getButton() == 3){
            right(e);
        }
    }


    @Override
    public void mouseDragged(MouseEvent e) {


        if (skipFirstDragEvent){
            // call corrasponding drag
            if (e.getButton() == 1){
                leftDrag(e);
            }else if (e.getButton() == 3){
                rightDrag(e);
            }
        }

        lastX = e.getX();
        lastY = e.getY();
        skipFirstDragEvent = true;

    }

    public void mouseReleased(MouseEvent e){

        skipFirstDragEvent = false;

    }

}

