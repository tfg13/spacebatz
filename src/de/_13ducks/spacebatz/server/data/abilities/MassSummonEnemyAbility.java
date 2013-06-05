package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.gamelogic.EnemyFactory;

/**
 *
 * @author michael
 */
public class MassSummonEnemyAbility extends Ability {

    @Override
    public void useOnPosition(Char user, double x, double y) {
        for (int i = 0; i < 10; i++) {
            Enemy enemy = EnemyFactory.createEnemy(x, y, Server.game.newNetID(), 1);
            Server.game.getEntityManager().addEntity(enemy.netID, enemy);
        }

    }
}
