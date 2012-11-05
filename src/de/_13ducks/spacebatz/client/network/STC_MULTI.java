/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de._13ducks.spacebatz.client.network;

/**
 * Ein spezielles, internes Kommando, das als Flag mitgeschickt wird, wenn der Server mehr als ein Paket pro Tick verschickt.
 *
 * @author tfg
 */
public class STC_MULTI extends STCCommand {

    @Override
    public void execute(byte[] data) {
        // Nichts tun, die reine Anwesenheit reicht.
    }

    @Override
    public boolean isVariableSize() {
        return false;
    }

    @Override
    public int getSize(byte sizeData) {
        return 0;
    }
    
}
