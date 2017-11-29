package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class Debris extends Participant
{
    /** The game controller */
    private Controller controller;

    /** The shape of the bullet */
    private Shape outline;

    /** Specifies if it is Ship or Asteroid Debris */
    private boolean isShip;
    
    /**For ship, specifies if it's for a long piece or short piece.*/
    private boolean isLong;

    /**
     * Constructs debris based on if a ship or asteroid is hit.
     * Parameters (boolean ShipOrAst, boolean isLong, double x, double y, double direction, Controller controller)
     */
    public Debris (boolean ShipOrAst, boolean LongOrShort, double x, double y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setSpeed(Constants.RANDOM.nextInt(3));
        setDirection(direction);
        setRotation(Constants.RANDOM.nextDouble() * 2 * Math.PI);
        isShip = ShipOrAst;
        isLong = LongOrShort;
        int debrisDuration = 0;
        
        if (isShip)
        {
            debrisDuration = 1500;
            if (isLong)
            {
                createLongShipDebrisOutline(); 
                
            }
            else
            {
                createShortShipDebrisOutline();
                
            }
        }
        else
        {
            createDebrisOutline();
            debrisDuration = Constants.RANDOM.nextInt(1500) + 250;
        }


        // Makes the bullet expire after the specified time
        new ParticipantCountdownTimer(this, "debris", debrisDuration);
    }


    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Creates the outline of the debris.
     */
    private void createDebrisOutline ()
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        poly.moveTo(0, -0.5);
        poly.lineTo(-0.5, 0);
        poly.lineTo(0, 0.5);
        poly.lineTo(0.5, 0);
        poly.closePath();
        outline = poly;
    }
    
    /**
     * Creates the long pieces of debris when the ship is hit.
     */
    private void createLongShipDebrisOutline ()
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        poly.moveTo(12, 0);
        poly.lineTo(0, 12);
        //poly.closePath();
        outline = poly;
    }
    
    /**
     * Creates the short pieces of debris when the ship is hit.
     */
    private void createShortShipDebrisOutline ()
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        poly.moveTo(3, 0);
        poly.lineTo(-3, 0);
        //poly.closePath();
        outline = poly;
    }

    @Override
    public void collidedWith (Participant p)
    {
        // TODO Auto-generated method stub

    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Tells the bullet to expire after the countdown.
        if (payload.equals("debris"))
        {
            Participant.expire(this);
        }
    }

}
