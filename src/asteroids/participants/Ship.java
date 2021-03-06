package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import javax.sound.sampled.Clip;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents ships
 */
public class Ship extends Participant implements AsteroidDestroyer, AlienDestroyer
{
    /** The outline of the ship */
    private Shape outline;
    
    /**The outline of the ship with the accelerating flame.*/
    private Shape outlineFlame;

    /** Game controller */
    private Controller controller;

    /**Stores the direction the ship should turn*/
    private turnDirection dir;
    
    /**Stores if the ship should accelerate*/
    private boolean shipAccel;
    
    /**Toggles the flame between off and on when ship is accelerating.*/
    private boolean toggleAccelFlame;
    
    /**Sound when the ship is accelerating forward*/
    private Clip thrust;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public Ship (int x, int y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);
        toggleAccelFlame = false;
        thrust = controller.createClip("/sounds/thrust.wav");

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
        
        Path2D.Double polyFlame = new Path2D.Double();
        polyFlame.moveTo(21, 0);
        polyFlame.lineTo(-21, 12);
        polyFlame.lineTo(-14, 10);
        polyFlame.lineTo(-14, -10);
        polyFlame.lineTo(-21, -12);
        polyFlame.closePath();
        //Draws the flame 
        polyFlame.moveTo(-14, -5);
        polyFlame.lineTo(-14, 5);
        polyFlame.lineTo(-25, 0);
        polyFlame.closePath();
        outlineFlame = polyFlame;
        
        dir = turnDirection.NONE;
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    @Override
    protected Shape getOutline ()
    {
        if (shipAccel)
        {
            toggleAccelFlame = !toggleAccelFlame;
            if (toggleAccelFlame)
            {
                return outlineFlame;
            }
            else
            {
                return outline;
            }
        }
        else
        {
            return outline;
        }        
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        if (dir == turnDirection.RIGHT)
        {
            turnRight();
        }
        if (dir == turnDirection.LEFT)
        {
            turnLeft();
        }
        if (shipAccel)
        {
            accelerate();
        }
        applyFriction(SHIP_FRICTION);
        super.move();        
    }
    
    /**Checks if a turn key button is pressed*/
    public void setTurnDirection (turnDirection turn)
    {
        dir = turn;
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 16);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 16);
    }

    /**Checks if the accelerate (Up or W Key) is being pressed.*/
    public void setAcceleration (boolean setAcc)
    {
        shipAccel = setAcc;
    }
    
    /**
     * Accelerates by SHIP_ACCELERATION
     */
    public void accelerate ()
    {
        controller.playSound(thrust);
        accelerate(SHIP_ACCELERATION);
    }

    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            controller.addParticipant(new Debris(true, true, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            controller.addParticipant(new Debris(true, true, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            controller.addParticipant(new Debris(true, false, this.getX(), this.getY(), this.getRotation() + RANDOM.nextDouble() * 2 * Math.PI, controller));
            
            // Expire the ship from the game
            Participant.expire(this);

            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        if (payload.equals("move"))
        {
            accelerate();
            new ParticipantCountdownTimer(this, "move", 200);
        }
    }
}
