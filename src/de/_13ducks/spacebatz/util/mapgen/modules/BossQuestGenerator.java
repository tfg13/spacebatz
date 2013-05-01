package de._13ducks.spacebatz.util.mapgen.modules;

import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.ai.behaviour.impl.shooter.ShooterBehaviour;
import de._13ducks.spacebatz.server.ai.behaviour.impl.shooter.ShooterLurkBehaviour;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.Teams;
import de._13ducks.spacebatz.server.data.abilities.FireBulletAbility;
import de._13ducks.spacebatz.server.data.entities.Enemy;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.server.gamelogic.EnemyFactory;
import de._13ducks.spacebatz.shared.EnemyTypeStats;
import de._13ducks.spacebatz.shared.EnemyTypes;
import de._13ducks.spacebatz.shared.network.BitEncoder;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.geo.MPolygon;
import de._13ducks.spacebatz.util.geo.Vector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Erzeugt einen Bossgegner und ein Quest diesen zu besiegen.
 *
 * @author michael
 */
public class BossQuestGenerator extends Module {

    @Override
    public String getName() {
        return "bossquest";
    }

    @Override
    public boolean requiresSeed() {
        return true;
    }

    @Override
    public String[] provides() {
        return new String[]{};
    }

    @Override
    public boolean computesPolygons() {
        return false;
    }

    @Override
    public String[] requires() {
        return new String[]{"SPAWN"};
    }

    @Override
    public void computeMap(final InternalMap map, HashMap<String, String> parameters) {
        Random random = new Random(Long.parseLong(parameters.get("SEED")));
        // Zufälligen Polygon, der weder Start noch besetzt ist suchen
        ArrayList<MPolygon> acceptablePolys = new ArrayList<>();
        for (MPolygon poly : map.polygons.polys) {
            if (poly.border || poly.solid || poly.spawn) {
                continue;
            }
            // Kommt in Frage
            acceptablePolys.add(poly);
        }

        // Zufälligen nehmen:
        final MPolygon targetPoly = acceptablePolys.get(random.nextInt(acceptablePolys.size()));
        final double x = targetPoly.calcCenter().x * map.topTex.length;
        final double y = targetPoly.calcCenter().y * map.topTex[0].length;
        final int netId = map.getNextNetId();
        Enemy boss = EnemyFactory.createEnemy(x, y, netId, 6);
        map.startEntitys.put(netId, boss);
        // Quest bauen:
        Quest q = new Quest() {
            Enemy boss;

            @Override
            public void tick() {
                // Quest macht nichts spezielles an der Spielmechanik
            }

            @Override
            protected int checkState() {
                if (boss == null) {
                    boss = (Enemy) Server.game.getEntityManager().getEntityById(netId);
                }
                if (boss.dead) {
                    return Quest.STATE_COMPLETED;
                } else {
                    return Quest.STATE_RUNNING;
                }
            }

            @Override
            public String getName() {
                return "BossQuest";
            }

            @Override
            public boolean isHidden() {
                return false;
            }

            @Override
            public byte[] getClientData() {
                BitEncoder encoder = new BitEncoder();
                encoder.writeByte((byte) 1);
                encoder.writeInt(questID);
                encoder.writeInt(netId);
                encoder.writeFloat((float) x);
                encoder.writeFloat((float) y);
                return encoder.getBytes();
            }
        };
        map.quests.add(q);
    }
}
