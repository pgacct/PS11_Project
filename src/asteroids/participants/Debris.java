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

    public Debris (double x, double y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setSpeed(Constants.RANDOM.nextInt(3));
        setDirection(direction);
        createDebrisOutline();

        // Makes the bullet expire after the specified time
        new ParticipantCountdownTimer(this, "debris", Constants.RANDOM.nextInt(1500) + 250);
    }
// change
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
