package de._13ducks.spacebatz.server.data.effects;

import de._13ducks.spacebatz.server.data.Char;

/**
 * Beispielklasse für einen Gifteffekt
 *
 * @author michael
 */
public class DemoDeadlyPoisonEffect extends CharEffect {

    /**
     * Der Schaden der periodisch angerichtet wird
     */
    private int damage;
    /**
     * Die Anzahl der Ticks die zwischen den Schäden vergeht
     */
    private int ticks;

    /**
     * Erstellt einen neuen Gifteffekt
     *
     * @param damage der Schaden der angerichtet wird
     * @param ticks die Ticks die zwischen dem Schaden vergehen sollen
     */
    public DemoDeadlyPoisonEffect(int damage, int ticks) {
        this.damage = damage;
        this.ticks = ticks;
    }

    /**
     * Erstellt eine Kopie des gegebenen Effekts
     *
     * @param other der Effekt dessen Werte kopiert werden
     */
    public DemoDeadlyPoisonEffect(DemoDeadlyPoisonEffect other) {
        this.damage = other.damage;
        this.ticks = other.ticks;
    }

    @Override
    public void applyToChar(Char affectedChar) {
        // affectedChar.applyEffect(this);
    }

    @Override
    public void onTick() {
//        if (Server.game.getTick() % ticks == 1) {
//            getAffectedChar().damage(damage);
//        }
    }

    @Override
    public void remove() {
        // affectedChar.removeEffect(this);
    }
}
