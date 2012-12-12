package de._13ducks.spacebatz.util.mapgen.modules;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import de._13ducks.spacebatz.server.Server;
import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.entities.Player;
import de._13ducks.spacebatz.server.data.quests.Quest;
import de._13ducks.spacebatz.util.Bits;
import de._13ducks.spacebatz.util.mapgen.InternalMap;
import de._13ducks.spacebatz.util.mapgen.Module;
import de._13ducks.spacebatz.util.mapgen.data.MPolygon;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Baut einen simplen Test-Quest in die Map ein.
 * Das Ziel dieses Quests ist es, in ein bestimmtes Gebiet zu fahren.
 *
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ExampleQuestCreator extends Module {

    @Override
    public String getName() {
        return "examplequest";
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
        return true;
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
        for (int i = 0; i < map.polygons.getNumGeometries(); i++) {
            MPolygon poly = (MPolygon) map.polygons.getGeometryN(i);
            if (poly.border || poly.solid || poly.spawn) {
                continue;
            }
            // Kommt in Frage
            acceptablePolys.add(poly);
        }

        // Zufälligen nehmen:
        final MPolygon targetPoly = acceptablePolys.get(random.nextInt(acceptablePolys.size()));
        targetPoly.texture = 7;
        // Quest bauen:
        final GeometryFactory fact = new GeometryFactory();
        Quest q = new Quest() {
            @Override
            public void tick() {
                // Quest macht nichts spezielles an der Spielmechanik
            }

            @Override
            protected int checkState() {
                for (Client c : Server.game.clients.values()) {
                    Player p = c.getPlayer();
                    // Implementierung ist ineffizient, ändern!
                    if (targetPoly.covers(new Point(new CoordinateArraySequence(new Coordinate[]{new Coordinate(p.getX() / map.groundTex.length, p.getY() / map.groundTex[0].length)}), fact))) {
                        return Quest.STATE_COMPLETED;
                    }
                }
                return Quest.STATE_RUNNING;
            }

            @Override
            public String getName() {
                return "ExampleQuest";
            }

            @Override
            public boolean isHidden() {
                return false;
            }

            @Override
            public byte[] getClientData() {
                byte[] ret = new byte[13];
                ret[0] = 0; // type
                Bits.putInt(ret, 1, questID);
                Coordinate target = targetPoly.getCentroid().getCoordinate();
                Bits.putFloat(ret, 5, (float) target.x * map.groundTex.length);
                Bits.putFloat(ret, 9, (float) target.y * map.groundTex[0].length);
                return ret;
            }
        };
        map.quests.add(q);
    }
}
