/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.enemys.StandardEnemy;

/**
 *
 * @author michael
 */
public class SummonEnemyAbility implements Ability {

    @Override
    public void useOnPosition(Char user, double x, double y) {
        Enemy enemy = new StandardEnemy(x, y, Server.game.newNetID(), 0);
        Server.game.getEntityManager().addEntity(enemy.netID, enemy);
    }

    @Override
    public void useInAngle(Char user, double angle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
