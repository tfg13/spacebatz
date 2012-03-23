package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Ein temporärer Buff für einen stat
 *
 * @author michael
 */
public class TemporaryStatBoost extends CharEffect {

    private String stat;
    private int bonus;
    private int remainingDuration;

    /**
     * Erstellt einen neuen Boost
     */
    public TemporaryStatBoost(String stat, int bonus, int duration) {
        this.stat = stat;
        this.bonus = bonus;
        this.remainingDuration = duration;

    }

    @Override
    public void applyToChar(Char affectedChar) {
        //affectedChar.increaseStat(stat,bonus);
        // affectedChar.applyEffect(this)
    }

    @Override
    public void onTick() {
        // if(Server.game.getTick() % 60 == 0){remainingDuration--;}
        // if(remainingDuration < 0){remove();}
    }

    @Override
    public void remove() {
        // affectedChar.decreaseStat(stat, bonus);
        // affectedChar.removeEffect(this);
    }
}
