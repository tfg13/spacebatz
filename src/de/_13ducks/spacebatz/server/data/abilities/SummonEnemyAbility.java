/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.data.Teams;
import de._13ducks.spacebatz.server.data.Teams.Team;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.entities.Char;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.ai.behaviour.impl.standardenemy.StandardEnemyBehaviour;

/**
 *
 * @author michael
 */
public class SummonEnemyAbility extends Ability {

    public SummonEnemyAbility() {
        setCooldown(100);
    }

    @Override
    public void useOnPosition(Char user, double x, double y) {
        Enemy enemy = new Enemy(x, y, Server.game.newNetID(), 0, Team.MOBS);
        enemy.setBehaviour(new StandardEnemyBehaviour(enemy));
        Server.game.getEntityManager().addEntity(enemy.netID, enemy);
    }

    @Override
    public void useInAngle(Char user, double angle) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
