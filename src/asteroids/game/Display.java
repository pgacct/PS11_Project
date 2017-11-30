package asteroids.game;

import javax.swing.*;
import static asteroids.game.Constants.*;
import java.awt.*;

/**
 * Defines the top-level appearance of an Asteroids game.
 */
@SuppressWarnings("serial")
public class Display extends JFrame
{
    /** The area where the action takes place */
    private Screen screen;

    /** The system that displays lives remaining, level, and score */
    private JLabel lives;
    private JLabel level;
    private JLabel points;

    private Controller controller;

    /**
     * Lays out the game and creates the controller
     */
    public Display (Controller controller)
    {
        this.controller = controller;

        // Title at the top
        setTitle(TITLE);

        // Default behavior on closing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // The main playing area and the controller
        screen = new Screen(controller);

        // This panel contains the screen to prevent the screen from being
        // resized
        JPanel screenPanel = new JPanel();
        screenPanel.setLayout(new GridBagLayout());
        screenPanel.add(screen);

        // This panel contains buttons and labels
        JPanel controls = new JPanel();
        controls.setLayout(new BorderLayout());

        // The button that starts the game
        JButton startGame = new JButton(START_LABEL);
        controls.add(startGame, "Center");

        // The Points System lives remaining, level, and score
        JPanel system = new JPanel();
        controls.add(system, "East");
        system.setLayout(new FlowLayout());
        lives = new JLabel("Lives: " + controller.shipLives());
        level = new JLabel("Level: " + controller.currentLevel());
        points = new JLabel("Points: " + controller.numPoints());
        system.add(lives);
        system.add(level);
        system.add(points);

        // Organize everything
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(screenPanel, "Center");
        mainPanel.add(controls, "North");
        setContentPane(mainPanel);
        pack();

        // Connect the controller to the start button
        startGame.addActionListener(controller);
    }

    /**
     * Called when it is time to update the screen display. This is what drives the animation.
     */
    public void refresh ()
    {
        screen.repaint();
        repaint();
    }

    public void repaint ()
    {
        lives.setText("Lives: " + controller.shipLives());
        level.setText("Level: " + controller.currentLevel());
        points.setText("Points: " + controller.numPoints());
    }

    /**
     * Sets the large legend
     */
    public void setLegend (String s)
    {
        screen.setLegend(s);
    }
}
