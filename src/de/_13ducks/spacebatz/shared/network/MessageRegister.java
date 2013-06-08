package de._13ducks.spacebatz.shared.network;

import de._13ducks.spacebatz.client.network.STCCommand;
import de._13ducks.spacebatz.client.network.STC_ACK;
import de._13ducks.spacebatz.client.network.STC_ANSWER_RCON;
import de._13ducks.spacebatz.client.network.STC_ENTITY_CREATE;
import de._13ducks.spacebatz.client.network.STC_ENTITY_REMOVE;
import de._13ducks.spacebatz.client.network.STC_ENTITY_UPDATE;
import de._13ducks.spacebatz.client.network.STC_FRAGMENTED_MESSAGE;
import de._13ducks.spacebatz.client.network.STC_MULTI;
import de._13ducks.spacebatz.client.network.STC_NET_STATS;
import de._13ducks.spacebatz.client.network.STC_TICK_SYNC;
import de._13ducks.spacebatz.client.network.STC_TRANSFER_CHUNK;
import de._13ducks.spacebatz.server.network.CTSCommand;
import de._13ducks.spacebatz.server.network.CTS_ACK;
import de._13ducks.spacebatz.server.network.CTS_DEBUG;
import de._13ducks.spacebatz.server.network.CTS_FRAGMENTED_MESSAGE;
import de._13ducks.spacebatz.server.network.CTS_REQUEST_RCON;
import de._13ducks.spacebatz.server.network.CTS_TICK_SYNC;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_DELETE_ITEM;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_INVEST_SKILLPOINT;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_INV_ITEM_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_MAP_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_SWITCH_WEAPON;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_USE_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_SHOOT;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_TOGGLE_BUILDMODE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_BROADCAST_TOP_CHANGE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_COLLISION;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_LEVEL;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHANGE_MATERIAL_AMOUNT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_ATTACK;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_CHAR_HIT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_DELETE_ITEM;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_DEL_CLIENT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_INV_ITEM_MOVE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_ITEM_DROP;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_NEW_CLIENT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_NEW_QUEST;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TOGGLE_ALIVE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_PLAYER_TURRET_DIR_UPDATE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_QUEST_RESULT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_CHAR_INVISIBILITY;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_CLIENT;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_PLAYER;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SET_SKILL_MAPPING;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SHADOW_CHANGE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_START_ENGINE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_SWITCH_WEAPON;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_TOGGLE_BUILDMODE;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_TRANSFER_ENEMYTYPES;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_UPDATE_SKILLTREE;

/**
 * Speichert alle bekannten Netzwerkbefehle, sowohl STC als auch CTS.
 * Hiervon laden Server und Client alle Befehle, damit sie senden und empfangen können.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class MessageRegister {

    private static CTSCommand[] ctsMessages = new CTSCommand[256];
    private static STCCommand[] stcMessages = new STCCommand[256];

    /**
     * Läd alle Befehle.
     */
    private static void loadMessages() {
        // interne Befehle, nicht ändern!
        loadInternal();
        // CTS
        loadCTS(MessageIDs.NET_CTS_MOVE, new CTS_MOVE());
        loadCTS(MessageIDs.NET_CTS_SHOOT, new CTS_SHOOT());
        loadCTS(MessageIDs.NET_TCP_CMD_REQUEST_ITEM_EQUIP, new CTS_EQUIP_ITEM());
        loadCTS(MessageIDs.NET_TCP_CMD_REQUEST_ITEM_DEQUIP, new CTS_REQUEST_ITEM_DEQUIP());
        loadCTS(MessageIDs.NET_TCP_CMD_REQUEST_WEAPONSWITCH, new CTS_REQUEST_SWITCH_WEAPON());
        loadCTS(MessageIDs.NET_TCP_CMD_REQUEST_RCON, new CTS_REQUEST_RCON());
        loadCTS(MessageIDs.NET_CTS_USE_ABILITY, new CTS_REQUEST_USE_ABILITY());
        loadCTS(MessageIDs.NET_CTS_MAP_ABILITY, new CTS_REQUEST_MAP_ABILITY());
        loadCTS(MessageIDs.NET_CTS_INVEST_SKILLPOINT, new CTS_INVEST_SKILLPOINT());
        loadCTS(MessageIDs.NET_TCP_CMD_REQUEST_INV_ITEM_MOVE, new CTS_REQUEST_INV_ITEM_MOVE());
        loadCTS(MessageIDs.NET_TCP_CMD_REQUEST_INV_ITEM_DELETE, new CTS_DELETE_ITEM());
        loadCTS(MessageIDs.NET_TCP_CMD_TOGGLE_BUILDMODE, new CTS_TOGGLE_BUILDMODE());
        // STC
        loadSTC(MessageIDs.NET_TCP_CMD_CHAR_HIT, new STC_CHAR_HIT());
        loadSTC(MessageIDs.NET_TCP_CMD_EQUIP_ITEM, new STC_EQUIP_ITEM());
        loadSTC(MessageIDs.NET_TCP_CMD_DEQUIP_ITEM, new STC_ITEM_DEQUIP());
        loadSTC(MessageIDs.NET_TCP_CMD_CHANGE_TOP, new STC_BROADCAST_TOP_CHANGE());
        loadSTC(MessageIDs.NET_TCP_CMD_CHANGE_COLLISION, new STC_CHANGE_COLLISION());
        loadSTC(MessageIDs.NET_TCP_CMD_SWITCH_WEAPON, new STC_SWITCH_WEAPON());
        loadSTC(MessageIDs.NET_TCP_CMD_SPAWN_ITEM, new STC_ITEM_DROP());
        loadSTC(MessageIDs.NET_TCP_CMD_TRANSFER_ENEMYTYPES, new STC_TRANSFER_ENEMYTYPES());
        loadSTC(MessageIDs.NET_STC_SET_CLIENT, new STC_SET_CLIENT());
        loadSTC(MessageIDs.NET_STC_SET_PLAYER, new STC_SET_PLAYER());
        loadSTC(MessageIDs.NET_STC_START_ENGINE, new STC_START_ENGINE());
        loadSTC(MessageIDs.NET_TCP_CMD_ANSWER_RCON, new STC_ANSWER_RCON());
        loadSTC(MessageIDs.NET_CHANGE_LEVEL, new STC_CHANGE_LEVEL());
        loadSTC(MessageIDs.NET_TCP_CMD_CHANGE_MATERIAL_AMOUNT, new STC_CHANGE_MATERIAL_AMOUNT());
        loadSTC(MessageIDs.NET_STC_UPDATE_SKILLTREE, new STC_UPDATE_SKILLTREE());
        loadSTC(MessageIDs.NET_STC_SET_SKILL_MAPPING, new STC_SET_SKILL_MAPPING());
        loadSTC(MessageIDs.NET_TCP_CMD_INV_ITEM_MOVE, new STC_INV_ITEM_MOVE());
        loadSTC(MessageIDs.NET_TCP_CMD_CHAR_ATTACK, new STC_CHAR_ATTACK());
        loadSTC(MessageIDs.NET_NEW_QUEST, new STC_NEW_QUEST());
        loadSTC(MessageIDs.NET_QUEST_RESULT, new STC_QUEST_RESULT());
        loadSTC(MessageIDs.NET_TCP_CMD_PLAYER_TOGGLE_ALIVE, new STC_PLAYER_TOGGLE_ALIVE());
        loadSTC(MessageIDs.NET_SHADOW_CHANGE, new STC_SHADOW_CHANGE());
        loadSTC(MessageIDs.NET_UPDATE_TURRET_DIR, new STC_PLAYER_TURRET_DIR_UPDATE());
        loadSTC(MessageIDs.NET_STC_NEW_CLIENT, new STC_NEW_CLIENT());
        loadSTC(MessageIDs.NET_STC_DEL_CLIENT, new STC_DEL_CLIENT());
        loadSTC(MessageIDs.NET_TCP_CMD_INV_ITEM_DELETE, new STC_DELETE_ITEM());
        loadSTC(MessageIDs.NET_STC_SET_CHAR_INVISIBILITY, new STC_SET_CHAR_INVISIBILITY());
        loadSTC(MessageIDs.NET_STC_TOGGLE_BUILDMODE, new STC_TOGGLE_BUILDMODE());
    }

    /**
     * Registriert ein (nicht-priviligiertes) CTS-Netzwerkkommando.
     *
     * @param messageID
     * @param cmd
     */
    private static void loadCTS(byte messageID, CTSCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ERROR: NETINIT: CTSCommand must not be null.");
        }
        if (messageID < 0 || messageID > 127) {
            throw new IllegalArgumentException("ERROR: NETINIT: Cannot register command with id " + messageID + ", id invalid.");
        }
        if (ctsMessages[messageID] != null || stcMessages[messageID] != null) {
            throw new IllegalArgumentException("ERROR: NETINIT: Cannot register command with id " + messageID + ", id exists.");
        }
        ctsMessages[messageID] = cmd;
    }

    /**
     * Registriert ein (nicht-priviligiertes) STC-Netzwerkkommando.
     *
     * @param messageID
     * @param cmd
     */
    private static void loadSTC(byte messageID, STCCommand cmd) {
        if (cmd == null) {
            throw new IllegalArgumentException("ERROR: NETINIT: STCCommand must not be null.");
        }
        if (messageID < 0 || messageID > 127) {
            throw new IllegalArgumentException("ERROR: NETINIT: Cannot register command with id " + messageID + ", id invalid.");
        }
        if (ctsMessages[messageID] != null || stcMessages[messageID] != null) {
            throw new IllegalArgumentException("ERROR: NETINIT: Cannot register command with id " + messageID + ", id exists.");
        }
        stcMessages[messageID] = cmd;
    }

    /**
     * Läd priviligierte Netzwerkbefehle, sollte nicht geändert werden müssen.
     */
    private static void loadInternal() {
        // CTS
        ctsMessages[0x80] = new CTS_ACK();
        ctsMessages[MessageIDs.NET_FRAGMENTED_MESSAGE] = new CTS_FRAGMENTED_MESSAGE();
        ctsMessages[MessageIDs.NET_CTS_DEBUG] = new CTS_DEBUG();
        ctsMessages[MessageIDs.NET_TICK_SYNC] = new CTS_TICK_SYNC();
        // STC
        stcMessages[0x80] = new STC_ACK();
        stcMessages[0x88] = new STC_MULTI();
        stcMessages[MessageIDs.NET_FRAGMENTED_MESSAGE] = new STC_FRAGMENTED_MESSAGE(); // 0x81
        stcMessages[MessageIDs.NET_ENTITY_UPDATE] = new STC_ENTITY_UPDATE(); // 0x83
        stcMessages[MessageIDs.NET_ENTITY_CREATE] = new STC_ENTITY_CREATE(); // 0x84
        stcMessages[MessageIDs.NET_ENTITY_REMOVE] = new STC_ENTITY_REMOVE(); // 0x85
        stcMessages[MessageIDs.NET_TRANSFER_CHUNK] = new STC_TRANSFER_CHUNK(); // 0x86
        stcMessages[MessageIDs.NET_STATS] = new STC_NET_STATS(); // 0x87
        stcMessages[MessageIDs.NET_TICK_SYNC] = new STC_TICK_SYNC(); // 0x89
    }

    /**
     * Liefert den STC-Befehl für die gegebenen MessageID
     *
     * @param messageID die ID
     * @return der Befehl, oder null
     */
    public static STCCommand getSTC(int messageID) {
        return stcMessages[messageID];
    }

    /**
     * Liefert den CTS-Befehl für die gegebenen MessageID
     *
     * @param messageID die ID
     * @return der Befehl, oder null
     */
    public static CTSCommand getCTS(int messageID) {
        return ctsMessages[messageID];
    }

    static {
        loadMessages();
    }
}
