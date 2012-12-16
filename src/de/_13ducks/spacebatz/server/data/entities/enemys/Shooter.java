package de._13ducks.spacebatz.server.data.entities.enemys;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.Distance;

/**
 *
 * @author michael
 */
public class Shooter extends Enemy {

    private Player myTarget;
    private Ability shootAbility;
    private int lastShootTick;

    public Shooter(double x, double y, int netId, int enemyTypeId) {
        super(x, y, netId, enemyTypeId);
        shootAbility = new FireBulletAbility(3, 0.1, 9.0, 1, 0.2, 0.025, 0.0, 0.0, 0.0);
    }

    /**
     * Berechnet jeden Tick die KI.
     *
     * @param gameTick der GameTick für den berechnet wird.
     */
    @Override
    public void tick(int gameTick) {
        super.tick(gameTick);
        // Nur alle 20 Ticks berechnen, um Auslastung zu veringern:
        if (gameTick % 20 == 0) {
            return;
        }

        // Haben wir schon ein Ziel?
        if (myTarget == null) {
            // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
            for (Client client : Server.game.clients.values()) {
                Player player = client.getPlayer();
                if (getProperties().getSightrange() > Distance.getDistance(getX(), getY(), player.getX(), player.getY())) {
                    myTarget = player;
                }
            }
        } else {
            // wenn er eins hat schaut er ob es noch in reichweite ist:
            if (getProperties().getSightrange() * 2 < Distance.getDistance(getX(), getY(), myTarget.getX(), myTarget.getY())) {
                myTarget = null;
                stopMovement();
            } else {
                // Wenn wir schon nahe genug dran sind anhalten:
                if (3.0 > Distance.getDistance(getX(), getY(), myTarget.getX(), myTarget.getY())) {
                    stopMovement();
                    double dx = myTarget.getX() - getX();
                    double dy = myTarget.getY() - getY();
                    double dir = Math.atan2(dy, dx);
                    if (dir < 0) {
                        dir += 2 * Math.PI;
                    }
                    if (gameTick - lastShootTick > 120) {
                        shootAbility.useInAngle(this, dir);
                        lastShootTick = gameTick;
                    }

                } else {
                    // wenn wir noch zu weit entfernt sind hinbewegen:
                    double vectorX = myTarget.getX() - getX();
                    double vectorY = myTarget.getY() - getY();
                    // Sicher gehen, dass die Vektoren nicht 0 sind:
                    if (vectorX == 0.0) {
                        vectorX = 0.1;
                    }
                    if (vectorY == 0.0) {
                        vectorY = 0.1;
                    }
                    setVector(vectorX, vectorY);
                }



            }
        }
    }
}
