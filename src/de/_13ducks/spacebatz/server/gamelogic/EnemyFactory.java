package de._13ducks.spacebatz.server.gamelogic;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.impl.kamikaze.KamikazeLurkBehaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.kiter.KiterLurkBehaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.lurker.LurkerLurkBehaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.shooter.ShooterLurkBehaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.spectator.SpectatorLurkBehaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.summoner.SummonerLurkBehaviour;
import de._13ducks.spacebatz.server.data.Teams;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.abilities.KamikazeAbility;
import de._13ducks.spacebatz.server.data.abilities.SummonEnemyAbility;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.shared.EnemyTypes;

/**
 *
 * @author michael
 */
public class EnemyFactory {

    public static Enemy createEnemy(double x, double y, int netId, int type) {

        Enemy enem = new Enemy(x, y, netId, type, Teams.Team.MOBS);
        EnemyTypeStats stats = (new EnemyTypes()).getEnemytypelist().get(type);
        // AI-Verhalten einrichten:
        switch (stats.behaviour) {
            case SHOOTER:
                enem.setBehaviour(new ShooterLurkBehaviour(enem));
                break;
            case SPECTATOR:
                enem.setBehaviour(new SpectatorLurkBehaviour(enem));
                break;
            case KAMIKAZE:
                enem.setBehaviour(new KamikazeLurkBehaviour(enem));
                break;
            case LURKER:
                enem.setBehaviour(new LurkerLurkBehaviour(enem));
                break;
            case KITER:
                enem.setBehaviour(new KiterLurkBehaviour(enem));
                break;
            case SUMMONER:
                enem.setBehaviour(new SummonerLurkBehaviour(enem));
                break;
        }
        // Ability setzen:
        switch (stats.shootAbility) {
            case FIREBULLET:
                enem.setShootAbility(new FireBulletAbility(stats.abilityDamage, stats.abilityDamagespread, stats.abilityAttackspeed, stats.abilityRange, stats.abilityBulletpic, stats.abilityBulletspeed, stats.abilitySpread, stats.abilityExplosionradius, stats.abilityMaxoverheat, stats.abilityReduceoverheat));
                enem.getShootAbility().setCooldown((int) Math.ceil(1 / stats.abilityAttackspeed));
                break;
            case KAMIKAZE:
                enem.setShootAbility(new KamikazeAbility(stats.abilityRange, (int) stats.abilityDamage));
                break;
            case SUMMONENEMYS:
                enem.setShootAbility(new SummonEnemyAbility());
        }
        return enem;
    }
}
