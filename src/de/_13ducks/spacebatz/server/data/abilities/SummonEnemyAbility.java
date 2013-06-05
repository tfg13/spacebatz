/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.gamelogic.EnemyFactory;

/**
 *
 * @author michael
 */
public class SummonEnemyAbility extends Ability {

    private Enemy lastSummonedEnemy;

    public SummonEnemyAbility() {
        setCooldown(300);
    }

    @Override
    public void useOnPosition(Char user, double x, double y) {
        lastSummonedEnemy = EnemyFactory.createEnemy(x, y, Server.game.newNetID(), 1);
        Server.game.getEntityManager().addEntity(lastSummonedEnemy.netID, lastSummonedEnemy);
    }

    public Enemy getLastSummEnemy() {
        return lastSummonedEnemy;
    }
}
