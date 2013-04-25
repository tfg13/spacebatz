package de._13ducks.spacebatz.server.data;

/**
 * Stellt die Konstanten für Teams und gibt für jede Angreifer-Opfer Kombination an, ob das Opfer getroffen wird.
 *
 * @author michael
 */
public class Teams {

    /**
     * Die Hitmap gibt an welches Team welche anderen Teams angreifen kann.
     */
    public static int hitMap[][] = {
        // Zeilen geben den Angreifer an (FrienlyPlayers, PVPPlayers, Mobs, FriendlyFireMobs)
        // Spalten geben den Angegriffenen an (FrienlyPlayers, PVPPlayers, Mobs, FriendlyFireMobs)
        {0, 0, 1, 1},
        {0, 1, 1, 1},
        {1, 1, 0, 0},
        {1, 1, 1, 1}
    };

    /**
     * Definiert die möglichen Teams.
     */
    public enum Team {

        FRIENDLYPLAYERS(0), // Normale Spieler
        PVPPLAYERS(1), // Spieler im PVP-Modus
        MOBS(2), // Gegner, die kein friendly-fire haben
        FRIENDLYFIREMOBS(3); // Gegner, die andere Gegner verletzen können
        private final int id;

        Team(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    };   // Gegner mit friendly fire

    /**
     * Gibt an, ob ein Angreifer ein Opfer treffen kann
     *
     * @param attackerTeam das Team des Angreifers
     * @param victimTeam das Team des Verteidigers
     * @return
     */
    public static boolean canHit(Team attackerTeam, Team victimTeam) {
        return hitMap[attackerTeam.getId()][victimTeam.getId()] == 1;
    }
}
