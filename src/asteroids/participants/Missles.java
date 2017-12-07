package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.destroyers.AlienDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents heat seeking missles
 */
public class Missles extends Participant implements AlienDestroyer, AsteroidDestroyer
{
    /** The game controller */
    private Controller controller;
    
    /**The shape of the missle */
    private Shape outline; 
    
    /**Direction for missles in enhanced version */
    private double missleDir;
    
    /**
     * Constructs heat seeking missles.  (double x, double y, double direction, controller) parameters.
     */
    public Missles (double x, double y, double speed, double direction, Controller controller)
    {
        this.controller = controller;
        missleDir = direction;
        setPosition(x, y); 
        setSpeed(speed);  
        setRotation(direction);
        setDirection(direction); 
        
     // This will contain the outline
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(25, 0);
        poly.lineTo(18, -7);
        poly.lineTo(0, -7);
        poly.lineTo(0, 7);
        poly.lineTo(18, 7);
        poly.lineTo(25, 0);
        poly.closePath();
        outline = poly;
        
        //Makes the bullet expire after the specified time
         new ParticipantCountdownTimer(this, "missle", 5000);
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }
    
    /**
     * This method is called when the alien timer completes its countdown.  Changes ship direction.
     */
    public void changeTurnDirection (double turn)
    {
        
        if (turn - (getRotation()) > 3 * Math.PI / 180 )
        {
            rotate(3 * Math.PI / 180);
        }
        else if (turn - (getRotation()) < -3 * Math.PI / 180 )
        {
            rotate(-3 * Math.PI / 180);
        }        
        else 
        {
            //Come back to this.
            /*if (turn > 2 * Math.PI - 3 * Math.PI / 180)
            { 
                rotate(-turn);
            }
            else
            {
            rotate(turn);
            }*/
        }
       
    }
    
    /**
     * Adjusts the direction of missles in the enhanced version.
     */
    @Override
    public void move ()
    {
       setDirection(getRotation());
        super.move();        
    }
    
    
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof Alien || p instanceof Asteroid)
        {
            // Expire the missle from the game
            Participant.expire(this);
        }             
    }
    
    @Override
    public void countdownComplete (Object payload)
    {
        // Tells the bullet to expire after the countdown.
        if (payload.equals("missle"))
        {
            Participant.expire(this);
        }
    }

}
