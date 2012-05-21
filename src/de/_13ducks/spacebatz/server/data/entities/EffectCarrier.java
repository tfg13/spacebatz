package de._13ducks.spacebatz.server.data.entities;

import de._13ducks.spacebatz.server.data.effects.Effect;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Eine Entity, die von Effekten beeinflusst werden kann.
 * Verwaltet und berechnet Effekte regelmäßig.
 *
 * @author michael
 */
public class EffectCarrier extends Char {

    /**
     * Liste aller Effekte, die der EffectCarrier gerade hat
     */
    private ArrayList<Effect> effects;

    /**
     * Erzeugt einen neuen EffectCarrier.
     *
     ** @param x X-Koordinate des EffectCarrier
     * @param y Y-Koordinate des EffectCarrier
     * @param netID die netID des EffectCarrier
     * @param entityTypeID die typeID des EffectCarrier
     */
    public EffectCarrier(double x, double y, int netID, byte entityTypeID) {
        super(x, y, netID, entityTypeID);
        effects = new ArrayList<>();
    }

    /**
     * Fügt einen neuen Effekt hinzu.
     *
     * @param newEffect der neue Effekt
     */
    public final void addTemporaryEffect(Effect newEffect) {
        effects.add(newEffect);
    }

    /**
     * Berechnet alle Effekte und entfernt abgelaufene Effekte.
     */
    public final void tick() {
        Iterator<Effect> iter = effects.iterator();
        while (iter.hasNext()) {
            Effect effect = iter.next();
            if (!effect.tick()) {
                effect.remove();
                iter.remove();
            }
        }
    }
}
