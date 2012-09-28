package de._13ducks.spacebatz.server.ai;

/**
 * Ein Gegnerverhalten, dass alle X gameticks ausgeführt wird.
 *
 * @author michael
 */
public abstract class Behaviour {

    private int frequency;

    public Behaviour(int frequency) {
        this.frequency = frequency;
    }

    public void tick(int gameTick) {
        if (gameTick % frequency == 0) {
            onTick(gameTick);
        }
    }

    protected abstract void onTick(int gameTick);
}
