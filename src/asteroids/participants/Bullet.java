package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import javax.sound.sampled.Clip;
import asteroids.destroyers.AlienDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents bullets
 */
public class Bullet extends Participant implements AsteroidDestroyer, AlienDestroyer
{
    /** The game controller */
    private Controller controller;
    
    /**The shape of the bullet */
    private Shape outline;  
    
    /** A Clip that, when played, sounds like a weapon being fired */
    private Clip bulletClip;
    
    /**
     * Constructs bullets.  Accepts x, y, direction, and controller as parameters.
     */
    public Bullet (double x, double y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y); 
        setSpeed(Constants.BULLET_SPEED);        
        setDirection(direction);         
        createBulletOutline();
        
        //Makes the bullet expire after the specified time
        new ParticipantCountdownTimer(this, "bullet", Constants.BULLET_DURATION);
    }
    
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }
    
    /**
     * Creates the outline of the bullet.
     */
    private void createBulletOutline ()
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();
        
        poly.moveTo(0, -1);
        poly.lineTo(-1, 0);
        poly.lineTo(0, 1);
        poly.lineTo(1, 0);
        poly.closePath();
        outline = poly;
    }

    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof Asteroid)
        {
            // Expire the bullet from the game
            Participant.expire(this);

        }        
    }
    
    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Tells the bullet to expire after the countdown.
        if (payload.equals("bullet"))
        {
            Participant.expire(this);
        }
    }

}
