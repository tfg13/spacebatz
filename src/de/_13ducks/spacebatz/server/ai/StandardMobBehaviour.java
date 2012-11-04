package de._13ducks.spacebatz.server.ai;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.Distance;

/**
 *
 * @author michael
 */
public class StandardMobBehaviour extends Behaviour {

    private Enemy owner;

    public StandardMobBehaviour(Enemy owner) {
        super(20);
        this.owner = owner;
    }

    @Override
    protected void onTick(int gameTick) {
        computeStandardMobBehaviour(owner);
    }

    /**
     * Berechnet das Standard-MobverhaltenX
     *
     * @param mob der Enemy für den das Standardverhalten berechnet werde nsoll
     */
    private void computeStandardMobBehaviour(Enemy mob) {

        // Hat der Mob ein Ziel?
        if (mob.getMyTarget() == null) {
            // wenn er kein Ziel hat sucht er ob eines in dwer Nähe ist:
            for (Client client : Server.game.clients.values()) {
                Player player = client.getPlayer();
                if (mob.getProperties().getSightrange() > Distance.getDistance(mob.getX(), mob.getY(), player.getX(), player.getY())) {
                    mob.setMyTarget(player);
                }
            }
        } else {
            // wenn er eins hat schaut er ob es noch in reichweite ist:
            if (mob.getProperties().getSightrange() * 2 < Distance.getDistance(mob.getX(), mob.getY(), mob.getMyTarget().getX(), mob.getMyTarget().getY())) {
                mob.setMyTarget(null);
                mob.stopMovement();
            } else {
                // Wenn wir schon nahe genug dran sind anhalten:
                if (1.0 > Distance.getDistance(mob.getX(), mob.getY(), mob.getMyTarget().getX(), mob.getMyTarget().getY())) {
                    mob.stopMovement();
                } else {
                    // wenn wir noch zu weit entfernt sind hinbewegen:
                    double vecX = mob.getMyTarget().getX() - mob.getX();
                    double vecY = mob.getMyTarget().getY() - mob.getY();
                    // Sicher gehen, dass die Vektoren nicht 0 sind:
                    if (vecX == 0.0) {
                        vecX = 0.1;
                    }
                    if (vecY == 0.0) {
                        vecY = 0.1;
                    }
                    mob.setVector(vecX, vecY);
                }



            }
        }

    }
}
