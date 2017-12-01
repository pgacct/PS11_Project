package asteroids.participants;

import static asteroids.game.Constants.ASTEROID_SCALE;
import static asteroids.game.Constants.RANDOM;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import asteroids.destroyers.AlienDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Alien extends Participant implements ShipDestroyer, AsteroidDestroyer
{
    /** The outline of the ship */
    private Shape outline;

    /** Game controller */
    private Controller controller;
    
    /**Direction for alien ship */
    private double alienDir;

    /**
     * Constructs an alien ship at the specified coordinates that is pointed in the given direction.
     */
    public Alien (int alienSize, Controller controller)
    {
        this.controller = controller;
        setPosition(-5, Constants.RANDOM.nextDouble() * Constants.SIZE);
        setSpeed(8);        
        setDirection(alienDirection());

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(10, 8);
        poly.lineTo(-10, 8);
        poly.lineTo(-20, 0);
        poly.lineTo(20, 0);
        poly.closePath();
        poly.moveTo(20, 0);
        poly.lineTo(10, -8);
        poly.lineTo(-10, -8);
        poly.lineTo(-20, 0);
        poly.closePath();
        poly.moveTo(10, -8);
        poly.lineTo(6, -15);
        poly.lineTo(-6, -15);
        poly.lineTo(-10, -8);
        poly.closePath();       
        
        // Scale to the desired size
        double scale = Constants.ALIENSHIP_SCALE[alienSize];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));
        
        outline = poly;

    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }
    
    public double alienDirection()
    {
        int directionChoice = Constants.RANDOM.nextInt(6);
        double direction = 0;

        // Randomly choose the direction of alien ship.
        switch (directionChoice)
        {
            case 0:
                direction = 0.0;  // Goes to right horizontally.
                break;
            case 1:
                direction = Math.PI;  // Goes to left horizontally.
                break;
            case 2:
                direction = 180 / Math.PI;   // 1 radian downward from horizontal. To the right.
                break;
            case 3:
                direction = -180 / Math.PI;   // 1 radian upward from horizontal. To the right.
                break;
            case 4:
                direction = 180 / (Math.PI * 2);   // 1 radian downward from horizontal. To the left.
                break;
            case 5:
                direction = -180 / (Math.PI * 2);   // 1 radian upward from horizontal. To the left.
                break;
        }
        return direction;
    }
    
    /**
     * This method is called when the alien timer completes its countdown.  Changes ship direction.
     */
    public void changeTurnDirection (double turn)
    {
        alienDir = turn;
    }
    
    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        setDirection(alienDir);
        super.move();        
    }

    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienDestroyer)
        {
            //Create debris from destruction
            controller.addParticipant(new Debris(true, true, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            controller.addParticipant(new Debris(true, true, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            controller.addParticipant(new Debris(true, false, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            controller.addParticipant(new Debris(true, true, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            controller.addParticipant(new Debris(true, true, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            controller.addParticipant(new Debris(true, false, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            
            // Expire the ship from the game
            Participant.expire(this);

            // Tell the controller the alien was destroyed
            controller.alienDestroyed();
        }
    }
}
