package de._13ducks.spacebatz.server.ai.behaviour.impl.kiter;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.GeoTools;

/**
 *
 * @author michael
 */
public class KiterBehaviour extends Behaviour {

    private Player myTarget;
    private Ability shootAbility;
    private int lastShootTick;

    public KiterBehaviour(Enemy enemy) {
        super(enemy);
    }

    /**
     * Berechnet jeden Tick die KI.
     *
     * @param gameTick der GameTick für den berechnet wird.
     */
    @Override
    public Behaviour tick(int gameTick) {
        super.tick(gameTick);
        // Nur alle 20 Ticks berechnen, um Auslastung zu veringern:
        if (gameTick % 20 == 0) {
            return this;
        }

        // Haben wir schon ein Ziel?
        if (myTarget == null) {
            // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
            for (Client client : Server.game.clients.values()) {
                Player player = client.getPlayer();
                if (owner.getProperties().getSightrange() > GeoTools.getDistance(owner.getX(), owner.getY(), player.getX(), player.getY())) {
                    myTarget = player;
                }
            }
        } else {
            // wenn er eins hat schaut er ob es noch in reichweite ist:
            if (owner.getProperties().getSightrange() * 2 < GeoTools.getDistance(owner.getX(), owner.getY(), myTarget.getX(), myTarget.getY())) {
                myTarget = null;
                owner.move.stopMovement();
            } else {
                // Wenn wir schon nahe genug dran sind anhalten:
                if (3.0 > GeoTools.getDistance(owner.getX(), owner.getY(), myTarget.getX(), myTarget.getY())) {
                    owner.move.stopMovement();

                    if (gameTick - lastShootTick > 120) {
                        shootAbility.tryUseOnPosition(owner, myTarget.getX(), myTarget.getY());
                        lastShootTick = gameTick;
                    }

                } else {
                    // wenn wir noch zu weit entfernt sind hinbewegen:
                    double vectorX = myTarget.getX() - owner.getX();
                    double vectorY = myTarget.getY() - owner.getY();
                    // Sicher gehen, dass die Vektoren nicht 0 sind:
                    if (vectorX == 0.0) {
                        vectorX = 0.1;
                    }
                    if (vectorY == 0.0) {
                        vectorY = 0.1;
                    }
                    owner.move.setVector(vectorX, vectorY);
                }



            }
        }
        return this;
    }

    @Override
    public Behaviour onTargetDeath() {
        return new KiterLurkBehaviour(owner);
    }
}
