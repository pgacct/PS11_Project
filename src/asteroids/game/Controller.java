package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import asteroids.participants.Alien;
import asteroids.participants.AlienBullet;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.Ship;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    /** The alien (if one is active) or null otherwise */
    private Alien alien;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /** Timer for alien ship */
    private Timer alienTimer;

    /** Timer for alien bullets */
    private Timer alienBulletTimer;

    /** Timer for changing direction of alien ship */
    private Timer alienDirectionTimer;

    /** Timer for the beat sounds */
    private Timer beatTimer;

    /** Timer for ship respawn */
    private Timer shipSpawn;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** Number of lives left */
    private int lives;

    /** The current Level */
    private int level;

    /** The current Number of Points */
    private int points;

    /** Whether there is a new level */
    private boolean newLevel = false;

    /** The game display */
    private Display display;

    /** Tracks if one of the bullet keys is being held down. */
    private boolean fireBullets;

    /** Sound for shooting bullets */
    private Clip fireClip;

    /** A Clip that, when played repeatedly, sounds like a small saucer flying */
    private Clip smallSaucerClip;

    /** A Clip that, when played repeatedly, sounds like a big saucer flying */
    private Clip bigSaucerClip;
    private Clip bangAlienShip;
    private Clip bangLarge;
    private Clip bangMedium;
    private Clip bangShip;
    private Clip bangSmall;
    private Clip beat1;
    private Clip beat2;

    /** Toggles between beat1 and beat 2 */
    private boolean toggleBeat;

    /** Stores the current beat */
    private int beatDelay;
    
    /** States whether game is active*/
    private boolean activeGame = false;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Set up timers
        alienTimer = new Timer(ALIEN_DELAY, this);
        alienBulletTimer = new Timer(1200, this);
        alienDirectionTimer = new Timer(2000, this);
        beatTimer = new Timer(INITIAL_BEAT, this);
        shipSpawn = new Timer(2000, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
        alienTimer.start();
        alienBulletTimer.start();
        alienDirectionTimer.start();
        beatTimer.start();

        // Initialize the sounds clips
        fireClip = createClip("/sounds/fire.wav");
        smallSaucerClip = createClip("/sounds/saucerSmall.wav");
        bigSaucerClip = createClip("/sounds/saucerBig.wav");
        bangAlienShip = createClip("/sounds/bangAlienShip.wav");
        bangLarge = createClip("/sounds/bangLarge.wav");
        bangMedium = createClip("/sounds/bangMedium.wav");
        bangShip = createClip("/sounds/bangShip.wav");
        bangSmall = createClip("/sounds/bangSmall.wav");
        beat1 = createClip("/sounds/beat1.wav");
        beat2 = createClip("/sounds/beat2.wav");

        toggleBeat = true;
        beatDelay = INITIAL_BEAT;

    }

    /**
     * Creates an audio clip from a sound file.
     */
    public Clip createClip (String soundFile)
    {
        // Opening the sound file this way will work no matter how the
        // project is exported. The only restriction is that the
        // sound files must be stored in a package.
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            // Create and return a Clip that will play a sound file. There are
            // various reasons that the creation attempt could fail. If it
            // fails, return null.
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            return clip;
        }
        catch (LineUnavailableException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (UnsupportedAudioFileException e)
        {
            return null;
        }
    }

    /**
     * Plays the requested sound
     */
    public void playSound (Clip clipFile)
    {
        if (clipFile.isRunning())
        {
            clipFile.stop();
        }
        clipFile.setFramePosition(0);
        clipFile.start();
    }

    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
        beatTimer.restart();
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids ()
    {
        addParticipant(new Asteroid(RANDOM.nextInt(4), 2, EDGE_OFFSET + RANDOM.nextInt(100) - 50,
                EDGE_OFFSET + RANDOM.nextInt(100) - 50, 3, this));
        addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE - EDGE_OFFSET + RANDOM.nextInt(100) - 50,
                EDGE_OFFSET + RANDOM.nextInt(100) - 50, 3, this));
        addParticipant(new Asteroid(RANDOM.nextInt(4), 2, EDGE_OFFSET + RANDOM.nextInt(100) - 50,
                SIZE - EDGE_OFFSET + RANDOM.nextInt(100) - 50, 3, this));
        addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE - EDGE_OFFSET + RANDOM.nextInt(100) - 50,
                SIZE - EDGE_OFFSET + RANDOM.nextInt(100) - 50, 3, this));
        for (int x = 0; x < level - 1; x++)
        {
            addParticipant(new Asteroid(RANDOM.nextInt(4), 2, -EDGE_OFFSET + RANDOM.nextInt(100) - 50,
                    -EDGE_OFFSET + RANDOM.nextInt(100) - 50, 3, this));
        }
    }

    /**
     * Places bullets on the screen.
     */
    private void placeBullets ()
    {
        if (pstate.countBullets() <= Constants.BULLET_LIMIT && ship != null)
        {
            addParticipant(new Bullet(ship.getXNose(), ship.getYNose(), ship.getRotation(), this));

            // Plays sounds when shooting
            playSound(fireClip);
        }
    }

    /**
     * Places alien bullets on the screen.
     */
    private void placeAlienBullets ()
    {
        if (level == 2 && alien != null)
        {
            addParticipant(new AlienBullet(alien.getX(), alien.getY(), RANDOM.nextDouble() * Math.PI * 2, this));
        }
        if (level > 2 && alien != null && ship != null)
        {
            // Angle to fire directly at ship
            double angle = Math.atan2(ship.getY() - alien.getY(), ship.getX() - alien.getX());
            // Adjust angle randomly by 5 degrees
            double randAngleAdj = (RANDOM.nextInt(11) - 5) * Math.PI / 180;
            // Add bullets
            addParticipant(
                    new AlienBullet(alien.getX(), alien.getY(), Participant.normalize(angle) + randAngleAdj, this));
        }
    }

    /**
     * Places alien ships on the screen. Removes any existing alien first.
     */
    private void placeAlien ()
    {
        if (level == 2)
        {
            alien = new Alien(1, 3, this);
            addParticipant(alien);

            // Play big alien sounds
            if (bigSaucerClip.isRunning())
            {
                bigSaucerClip.stop();
            }
            bigSaucerClip.setFramePosition(0);
            bigSaucerClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        if (level > 2)
        {
            alien = new Alien(0, 5, this);
            addParticipant(alien);

            // Play small alien sounds
            if (smallSaucerClip.isRunning())
            {
                smallSaucerClip.stop();
            }
            smallSaucerClip.setFramePosition(0);
            smallSaucerClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
        alien = null;
        beatTimer.setDelay(INITIAL_BEAT);
        beatTimer.restart();
        alienTimer.restart();
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        clear();

        // Place asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Remove any alien ships from game.
        alien = null;

        // Reset statistics
        lives = 3;  // should be 3, changed to give more lives when testing

        // Reset Level
        level = 1;  // should be 1, testing at different levels

        // Reset points
        points = 0;

        // game is off to begin
        activeGame = false;
        
        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        // Play sound
        playSound(bangShip);
        
        fireBullets = false;
        beatTimer.stop();

        // Decrement lives
        lives--;

        // Null out the ship
        ship = null;

        // Display a legend
        display.setLegend("Ouch!");

        if (lives == 0)
        {

        }
        else
        {
            shipSpawn.start();
        }

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);

    }

    /**
     * The alien ship has been destroyed.
     */
    public void alienDestroyed ()
    {
        if (level == 2)
        {
            addPoints(200);
        }
        if (level > 2)
        {
            addPoints(1000);
        }
        alien = null;
        alienTimer.restart();

        // Plays sounds when alien is destroyed
        playSound(bangAlienShip);
    }

    /**
     * 
     * @return the number of ship lives
     */
    public int shipLives ()
    {
        return lives;
    }

    /**
     * 
     * @return the number of points
     */
    public int numPoints ()
    {
        return points;
    }

    public void addPoints (int Points)
    {
        points += Points;
    }

    /**
     * 
     * @return the current Level
     */
    public int currentLevel ()
    {
        return level;
    }
    
    /**
     * 
     * @return whether the game is active
     */
    public boolean isGameActive() {
        return activeGame;
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed (int astSize)
    {
        // If all the asteroids are gone, schedule a transition
        if (pstate.countAsteroids() == 0)
        {
            beatTimer.stop();
            alienTimer.stop();
            beatDelay = INITIAL_BEAT;
            if (alien != null)
            {
                Participant.expire(alien);
                alien = null;
            }
            scheduleTransition(END_DELAY);
            level++;
            display.setLegend("");
            newLevel = true;
        }

        // Plays sounds when an asteroid is destroyed
        switch (astSize)
        {
            case 0:
                playSound(bangSmall);
                break;
            case 1:
                playSound(bangMedium);
                break;
            case 2:
                playSound(bangLarge);
                break;
        }
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
            activeGame = true;
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();

            // Fire bullets when a firing key is pressed.
            if (fireBullets && ship != null)
            {
                placeBullets();
            }

            // Stops small saucer sounds if alien ship is null
            if (alien == null && smallSaucerClip != null)
            {
                smallSaucerClip.stop();
            }

            // Stops big saucer sounds if alien ship is null
            if (alien == null && bigSaucerClip != null)
            {
                bigSaucerClip.stop();
            }

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Refresh screen
            display.refresh();
        }
        else if (e.getSource() == alienTimer)
        {
            if (level == 2 && alien == null)
            {
                placeAlien();
            }
            if (level > 2 && alien == null)
            {
                placeAlien();
            }
        }
        else if (e.getSource() == alienBulletTimer)
        {
            if (alien != null && ship != null)
            {
                placeAlienBullets();
            }
        }
        else if (e.getSource() == alienDirectionTimer)
        {
            if (alien != null)
            {
                alien.changeTurnDirection(alien.alienDirection());
            }
        }
        else if (e.getSource() == beatTimer)
        {
            if (toggleBeat)
            {
                playSound(beat1);
                toggleBeat = !toggleBeat;
                if (beatTimer.getDelay() > FASTEST_BEAT + BEAT_DELTA)
                {
                    beatDelay = beatDelay - BEAT_DELTA;
                    beatTimer.setDelay(beatDelay);
                }
            }
            else
            {
                playSound(beat2);
                toggleBeat = !toggleBeat;
                if (beatTimer.getDelay() > FASTEST_BEAT + BEAT_DELTA)
                {
                    beatDelay = beatDelay - BEAT_DELTA;
                    beatTimer.setDelay(beatDelay);
                }
            }
        }
        else if (e.getSource() == shipSpawn)
        {
            placeShip();
            shipSpawn.stop();
        }

    }

    /**
     * Returns an iterator over the active participants
     */
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen.
            if (lives <= 0)
            {
                if (alien != null)
                {
                    Participant.expire(alien);
                    alien = null;
                }
                alienTimer.stop();
                beatTimer.stop();
                finalScreen();
            }
            if (newLevel)
            {
                clear();
                placeShip();
                placeAsteroids();
                newLevel = false;
            }
        }
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if (ship != null)
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    ship.setTurnDirection(turnDirection.RIGHT);
                    break;
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    ship.setTurnDirection(turnDirection.LEFT);
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    ship.setAcceleration(true);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                case KeyEvent.VK_SPACE:
                    if (!fireBullets)
                    {
                        placeBullets();
                    }
                    fireBullets = true;
                    break;
            }
        }
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        if (ship != null)
        {
            switch (e.getKeyCode())
            {
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    ship.setTurnDirection(turnDirection.NONE);
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    ship.setAcceleration(false);
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                case KeyEvent.VK_SPACE:
                    fireBullets = false;
                    break;
            }
        }
    }
}
