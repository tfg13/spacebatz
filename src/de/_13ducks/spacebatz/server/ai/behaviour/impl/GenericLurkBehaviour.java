package de._13ducks.spacebatz.server.ai.behaviour.impl;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.Behaviour;
import de._13ducks.spacebatz.server.data.entities.Bullet;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Entity;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.util.geo.Distance;

/**
 * Waits until shot by a player or sighting a player.
 *
 * @author michael
 */
public abstract class GenericLurkBehaviour extends Behaviour {

    public GenericLurkBehaviour(Enemy enemy) {
        super(enemy);
    }

    @Override
    public Behaviour tick(int gameTick) {
        for (Entity target : Server.entityMap.getEntitiesAroundPoint(owner.getX(), owner.getY(), owner.getProperties().getSightrange())) {
            if (target instanceof Player && Distance.getDistance(target.getX(), target.getY(), owner.getX(), owner.getY()) < owner.getProperties().getSightrange()) {
                return targetSpotted((Player) target);
            }
        }
        return super.tick(gameTick);
    }

    @Override
    public Behaviour onCollision(Entity other) {
        if (other instanceof Bullet) {
            Bullet bullet = (Bullet) other;
            if (bullet.getOwner() instanceof Player) {
                Player player = (Player) bullet.getOwner();
                return targetSpotted(player);
            }
            return this;
        } else {
            return this;
        }
    }

    /**
     * Called when a target is spotted.
     *
     * @param target
     */
    public abstract Behaviour targetSpotted(Player target);
}
