package de._13ducks.spacebatz.shared.network;

/**
 *
 * @author michael
 */
public class MessageIDs {

    /**
     * Private, da Utility-Class
     */
    private MessageIDs() {
        /**
         * Ability wird auf einen Key gemappt:
         */
    }
    public static final byte NET_STC_SET_SKILL_MAPPING = 57;
    /**
     * Skilltree-Info update für den Client.
     */
    public static final byte NET_STC_UPDATE_SKILLTREE = 55;
    /**
     * *
     * Fähigkeitsbenutzungsanforderung vom Client
     */
    public static final byte NET_CTS_USE_ABILITY = 38;
    /**
     * *
     * Fähigkeit auf anderen Fähigkeitenslot legen:
     */
    public static final byte NET_CTS_MAP_ABILITY = 52;
    /**
     * *
     * Einen Skillpunkt auf einen Skill setzen
     */
    public static final byte NET_CTS_INVEST_SKILLPOINT = 51;
    /**
     * Teilt dem Spieler mit welchen Char er kontrolliert
     */
    public static final byte NET_STC_SET_PLAYER = 21;
    /**
     * Teilt dem CLients seine ClientID mit
     */
    public static final byte NET_STC_SET_CLIENT = 23;
    /**
     * Startet die Engine auf dem Client
     */
    public static final byte NET_STC_START_ENGINE = 22;
    /**
     * Sendet Bewegungsdaten (WASD) an den Server.
     */
    public static final byte NET_CTS_MOVE = 49;
    /**
     * Sendet Schluss-Request an Server.
     */
    public static final byte NET_CTS_SHOOT = 50;
    /**
     * Debugnachricht, das erste byte ist die länge
     */
    public static final int NET_CTS_DEBUG = 0x81;
    /**
     * Daten eines Fragmentierten Packets
     */
    public static final int NET_FRAGMENTED_MESSAGE = 0x82;
    /**
     * Update von Bewegungsdaten.
     */
    public static final int NET_ENTITY_UPDATE = 0x83;
    /**
     * Neues Entity einfügen.
     */
    public static final int NET_ENTITY_CREATE = 0x84;
    /**
     * Entity entfernen.
     */
    public static final int NET_ENTITY_REMOVE = 0x85;
    /**
     * Ein noch unbekannter Chunk wird übertragen.
     */
    public static final int NET_TRANSFER_CHUNK = 0x86;
    /**
     * Normales Einheitenupdate, das Regelmäßig verschickt wird.
     */
    public static final byte NET_UDP_CMD_NORMAL_ENTITY_UPDATE = 10;
    /**
     * Char einfügen.
     */
    public static final byte NET_UDP_CMD_ADD_ENTITY = 12;
    /**
     * Char löschen.
     */
    public static final byte NET_UDP_CMD_DEL_ENTITY = 13;
    /**
     * Bestätigung, dass eine Bewegung vom Server beim Client angekommen ist.
     */
    public static final byte NET_UDP_CMD_ACK_MOVE = -12;
    /**
     * Bestätigung, dass das Erstellen einer Einheit beim Client angekommen ist.
     */
    public static final byte NET_UDP_CMD_ACK_ADD_ENTITY = -13;
    /**
     * Bestätigung, dass das Löschen einer Einheit beim Client angekommen ist.
     */
    public static final byte NET_UDP_CMD_ACK_DEL_ENTITY = -14;
    /**
     * Normales Input-an-Server-Schicken.
     */
    public static final byte NET_UDP_CMD_INPUT = -10;
    /**
     * Ping-Request an den Server.
     */
    public static final byte NET_UDP_CMD_PING = -5;
    /**
     * Ping-Antwort vom Server.
     */
    public static final byte NET_UDP_CMD_PONG = 5;
    /**
     * Server schickt Infos über Tickdelay.
     */
    public static final byte NET_UDP_CMD_TICK_SYNC_PING = 1;
    /**
     * Antwort an Server, dass Tickdelay-Paket erhalten wurde.
     */
    public static final byte NET_UDP_CMD_TICK_SYNC_PONG = -1;
    /**
     * Die cmdID für Level-senden
     */
    public static final byte NET_CHANGE_LEVEL = 20;
    /**
     * Die cmdID für Char mit Angriff / Bullet treffen
     */
    public static final byte NET_TCP_CMD_CHAR_HIT = 28;
    /**
     * Die cmdID für EnemyTypes-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_ENEMYTYPES = 29;
    /**
     * Die cmdID für EnemyTypes-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_BULLETTYPES = 30;
    /**
     * Die cmdID für ItemTypes-senden
     */
    public static final byte NET_TCP_CMD_TRANSFER_ITEMTYPES = 31;
    /**
     * Die cmdID für Item-Drop senden
     */
    public static final byte NET_TCP_CMD_SPAWN_ITEM = 32;
    /**
     * Die cmdID für Item-Aufsammeln senden
     */
    public static final byte NET_TCP_CMD_GRAB_ITEM = 33;
    /**
     * Die cmdID für komplette Liste der herumliegenden Items
     */
    public static final byte NET_TCP_CMD_TRANSFER_ITEMS = 34;
    /**
     * Die cmdID für geänderten Boden
     */
    public static final byte NET_TCP_CMD_CHANGE_GROUND = 35;
    /**
     * Die cmdId für Kollisionsänderung
     */
    public static final byte NET_TCP_CMD_CHANGE_COLLISION = 44;
    /**
     * Client will was anziehen, muss dafür aber erst Server fragen
     */
    public static final byte NET_TCP_CMD_REQUEST_ITEM_EQUIP = 36;
    /**
     * Client will Item ablegen, muss dafür aber erst Server fragen
     */
    public static final byte NET_TCP_CMD_REQUEST_ITEM_DEQUIP = 37;
    /**
     * Server sagt, dass Client Item anzieht
     */
    public static final byte NET_TCP_CMD_EQUIP_ITEM = 38;
    /**
     * Server sagt, dass Client Item auszieht
     */
    public static final byte NET_TCP_CMD_DEQUIP_ITEM = 39;
    /**
     * Client will andere Waffe auswählen
     */
    public static final byte NET_TCP_CMD_REQUEST_WEAPONSWITCH = 41;
    /**
     * Server ändert für einen Client die gerade ausgewählte Waffe
     */
    public static final byte NET_TCP_CMD_SWITCH_WEAPON = 42;
    /**
     * Client will resync.
     */
    public static final byte NET_TCP_CMD_REQUEST_RESYNC = 43;
    /**
     * Normales Input-an-Server-Schicken.
     */
    public static final byte NET_UDP_CMD_REQUEST_BULLET = -11;
    /**
     * Client möchte RCON aufbauen.
     */
    public static final byte NET_TCP_CMD_REQUEST_RCON = 45;
    /**
     * Server antwortet auf die Client-Rconanfrage
     */
    public static final byte NET_TCP_CMD_ANSWER_RCON = 46;
    /**
     * Die cmdID für Item-Aufsammeln und auf ein anderes draufstacken
     */
    public static final byte NET_TCP_CMD_GRAB_ITEM_TO_STACK = 47;
    /**
     * Die cmdID um allen Spielern von bestimmtem Material zu geben
     */
    public static final byte NET_TCP_CMD_CHANGE_MATERIAL_AMOUNT = 48;
    /**
     * Client will 2 Items im Inventar tauschen, C->S
     */
    public static final byte NET_TCP_CMD_REQUEST_INV_ITEM_MOVE = 53;
    /**
     * Client will 2 Items im Inventar tauschen, S->C
     */
    public static final byte NET_TCP_CMD_INV_ITEM_MOVE = 54;
    /**
     * Client will 2 Items im Inventar tauschen, S->C
     */
    public static final byte NET_TCP_CMD_CHAR_ATTACK = 56;
    /**
     * Neuer Client-Quest
     */
    public static final byte NET_NEW_QUEST = 58;
    /**
     * Client-Quest zuende
     */
    public static final byte NET_QUEST_RESULT = 59;
    /**
     *
     * Player stibt / lebt wieder
     */
    public static final byte NET_TCP_CMD_PLAYER_TOGGLE_ALIVE = 60;
    /**
     * Beleuchtung hat sich geändert
     */
    public static final byte NET_SHADOW_CHANGE = 61;
}
