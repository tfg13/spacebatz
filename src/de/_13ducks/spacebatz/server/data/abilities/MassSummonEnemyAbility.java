package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.StandardEnemy;

/**
 *
 * @author michael
 */
public class MassSummonEnemyAbility implements Ability {

    @Override
    public void useOnPosition(Char user, double x, double y) {
        for (int i = 0; i < 10; i++) {
            Enemy enemy = new StandardEnemy(x + i, y, Server.game.newNetID(), 0);
            Server.game.getEntityManager().addEntity(enemy.netID, enemy);
        }

    }

    @Override
    public void useInAngle(Char user, double angle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
