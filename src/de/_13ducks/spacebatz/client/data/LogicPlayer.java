package de._13ducks.spacebatz.client.data;

import de._13ducks.spacebatz.client.PlayerCharacter;

/**
 * Logische Repräsentation eines (Mit-)Spielers.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class LogicPlayer {
    
    /**
     * Die ClientID dieses Spielers.
     */
    public final int clientID;
    /**
     * Die netID der Spielfigur dieses Spielers.
     * Immer gesetzt, auch wenn die eigentliche Spielfigur noch unbekannt ist.
     */
    private int playerNetID = -1;
    /**
     * Der Player, der tatsächlich auf der Map rumgurkt.
     */
    private PlayerCharacter player;
    /**
     * Der Nickname.
     */
    private String nickName;
    /**
     * Ob der Spieler gerade tot ist.
     */
    private boolean dead;
    
    public LogicPlayer(int clientID, String nickName) {
        this.clientID = clientID;
        this.nickName = nickName;
    }

    /**
     * @return the nickName
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * @param nickName the nickName to set
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * @return the player
     */
    public PlayerCharacter getPlayer() {
        return player;
    }

    /**
     * @param player the player to set
     */
    public void setPlayer(PlayerCharacter player) {
        this.player = player;
    }

    /**
     * @return the dead
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * @param dead the dead to set
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * @return the playerNetID
     */
    public int getPlayerNetID() {
        return playerNetID;
    }

    /**
     * @param playerNetID the playerNetID to set
     */
    public void setPlayerNetID(int playerNetID) {
        this.playerNetID = playerNetID;
    }
    
    

}
