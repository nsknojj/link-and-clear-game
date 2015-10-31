package connecting;
// 实现了一个能画红边框的按钮
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class RButton extends JButton {
    boolean rim=false;

    RButton() {
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (rim) {
            int w=46, l=13, width=3;
            g.setColor(Color.RED);
            for (int i=0;i<width;i++){
                // x coordinate
                g.drawLine(0, i, l, i);
                g.drawLine(w, i, w-l, i);
                g.drawLine(0, w-i, l, w-i);
                g.drawLine(w, w-i, w-l, w-i);
                // y coordinate
                g.drawLine(i, 0, i, l);
                g.drawLine(i, w, i, w-l);
                g.drawLine(w-i, 0, w-i, l);
                g.drawLine(w-i, w, w-i, w-l);
            }
            g.dispose();
        }
    }
    
    public void setRim(boolean _rim){
        rim=_rim;
    }
}
