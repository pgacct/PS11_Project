package asteroids.participants;

import static asteroids.game.Constants.SHIP_ACCELERATION;
import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.destroyers.AlienDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents heat seeking missiles
 */
public class Missles extends Participant implements AlienDestroyer, AsteroidDestroyer
{
    /** The game controller */
    private Controller controller;

    /** The shape of the missile */
    private Shape outline;

    /** The shape of the missile with the flame */
    private Shape outlineFlame;

    /** Direction for missiles in enhanced version */
    private double missleDir;

    /** Toggles the flame on the missile */
    private boolean toggleFlame;

    /**
     * Constructs heat seeking missiles. (double x, double y, double direction, controller) parameters.
     */
    public Missles (double x, double y, double speed, double direction, double rotation, Controller controller)
    {
        this.controller = controller;
        missleDir = direction;
        setPosition(x, y);
        setSpeed(speed);
        setRotation(rotation);
        setDirection(direction);
        toggleFlame = true;

        // This will contain the outline without the flame
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(25, 0);
        poly.lineTo(18, -7);
        poly.lineTo(0, -7);
        poly.lineTo(0, 7);
        poly.lineTo(18, 7);
        poly.lineTo(25, 0);
        poly.closePath();
        outline = poly;

        // This will contain the outline with the flame
        Path2D.Double polyFlame = new Path2D.Double();
        polyFlame.moveTo(25, 0);
        polyFlame.lineTo(18, -7);
        polyFlame.lineTo(0, -7);
        polyFlame.lineTo(0, 7);
        polyFlame.lineTo(18, 7);
        polyFlame.lineTo(25, 0);
        polyFlame.closePath();
        // Draws the flame
        polyFlame.moveTo(0, -5);
        polyFlame.lineTo(0, 5);
        polyFlame.lineTo(-10, 0);
        polyFlame.closePath();
        outlineFlame = polyFlame;

        // Makes the missile expire after the specified time
        new ParticipantCountdownTimer(this, "missle", 15000);
    }

    @Override
    protected Shape getOutline ()
    {
        toggleFlame = !toggleFlame;
        if (toggleFlame)
        {
            return outlineFlame;
        }
        else
        {
            return outline;
        }

    }

    /**
     * Changes missile direction.
     */
    public void changeTurnDirection (double turn)
    {

        if (turn - (getRotation()) > 3 * Math.PI / 180)
        {
            rotate(3 * Math.PI / 180);
        }
        else if (turn - (getRotation()) < -3 * Math.PI / 180)
        {
            rotate(-3 * Math.PI / 180);
        }
        else
        {
 
        }
    }

    /**
     * Adjusts the direction of missiles in the enhanced version.
     */
    @Override
    public void move ()
    {
        setDirection(getRotation());
        accelerate();
        super.move();
    }
    
    /**
     * Accelerates the missile by SHIP_ACCELERATION up to speed limit
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION);
    }

    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof Alien || p instanceof Asteroid)
        {
            // Expire the missile from the game
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
