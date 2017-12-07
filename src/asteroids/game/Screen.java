package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.Iterator;
import javax.swing.*;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private Controller controller;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);
        
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        Iterator<Participant> iter = controller.getParticipants();
        while (iter.hasNext())
        {
            iter.next().draw(g);
        }

        // Draw the legend across the middle of the panel
        
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);
        
        // Lives Levels Points
        if(controller.isGameActive()) {
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 36));
            //Points
            g.drawString(controller.numPoints() + "", SIZE / 14 , SIZE / 12);
            
            //Level
            g.drawString(controller.currentLevel() + "", SIZE - SIZE / 14, SIZE / 11);
            
            //Lives
            for(int x = 1;x < controller.shipLives() + 1;x++) {
                Path2D.Double poly = new Path2D.Double();
                poly.moveTo(21, 0);
                poly.lineTo(-21, 12);
                poly.lineTo(-14, 10);
                poly.lineTo(-14, -10);
                poly.lineTo(-21, -12);
                poly.closePath();
                
                poly.transform(AffineTransform.getQuadrantRotateInstance(3));
                poly.transform(AffineTransform.getTranslateInstance((SIZE / 34) + ((SIZE / 26) * x), SIZE / 8 ));
                
                g.draw(poly);
                
                
            }
            
            
        }
        

        
    }
}
