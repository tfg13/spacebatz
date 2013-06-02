package de._13ducks.spacebatz.client.graphics.overlay.impl;

import de._13ducks.spacebatz.client.graphics.overlay.Overlay;

/**
 * Der Net-Graph, zeigt Performance und Netzwerkdaten an.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class NetGraph extends Overlay {

    @Override
    public void render() {
//        // Net-Graph?
//        if (NetStats.netGraph > 0) {
//            ClientNetwork2 net = GameClient.getNetwork2();
//            boolean connectionAlive = net.connectionAlive();
//            glDisable(GL_TEXTURE_2D);
//            if (connectionAlive) {
//                glColor4f(.9f, .9f, .9f, .7f);
//            } else {
//                glColor4f(1f, 0f, 0f, 1f);
//            }
//            glRectf(0, tilesY, 10, NetStats.netGraph >= 2 ? (NetStats.netGraph >= 3 ? tilesY - 5f : tilesY - 2.5f) : tilesY - 1.5f);
//            glColor4f(1f, 1f, 1f, 1f);
//            // Warnung bei Lag?
//            if (GameClient.getNetwork2().isLagging() && connectionAlive) {
//                glColor4f(1f, 0f, 0f, 1f);
//            } else {
//                glColor4f(0f, 1f, 0f, 1f);
//            }
//            glRectf(7, tilesY, 10f, tilesY - 0.5f);
//            // Arbeitet das Netzwerk gerade an dem Problem?
//            if (GameClient.getNetwork2().isTickSyncing() && connectionAlive) {
//                glColor4f(1f, 1f, 0f, 1f);
//                glRectf(7, tilesY - 0.5f, 10f, tilesY - 1f);
//            }
//            glEnable(GL_TEXTURE_2D);
//            if (connectionAlive) {
//                if (GameClient.getNetwork2().isLagging()) {
//                    TextWriter.renderText("LAG", 8, tilesY - 0.5f);
//                } else {
//                    TextWriter.renderText("NET OK", 7.5f, tilesY - 0.5f);
//                }
//                if (GameClient.getNetwork2().isTickSyncing()) {
//                    TextWriter.renderText("tuning net", 7.1f, tilesY - 1f);
//                }
//                TextWriter.renderText("lerp: " + net.getLerp() + " (~" + (GameClient.getNetwork2().getLogicTickDelay() * net.getLerp() + "ms)"), 0, tilesY - .5f);
//                //renderText("netIn/tick: number " + NetStats.getAndResetInCounter() + " bytes " + NetStats.getAndResetInBytes(), 0, tilesY - 1);
//                TextWriter.renderText("fps: " + GameClient.getEngine().getFps() + " ping: " + NetStats.ping, 0, tilesY - 1f);
//                TextWriter.renderText("AFT: " + GraphicsEngine.timing.getNiceAvg(), 5, tilesY - 1f);
//                TextWriter.renderText("Net %health: " + net.getConnectionHealthPercent(), 0, tilesY - 1.5f, net.getConnectionHealthPercent() < 95 ? 1 : 0, 0, 0, 1);
//                TextWriter.renderText("%load: " + net.getConnectionLoadPercent(), 6.5f, tilesY - 1.5f, net.getConnectionLoadPercent() > 80 ? 1 : 0, 0, 0, 1);
//                if (NetStats.netGraph >= 2) {
//                    // Einheitenposition:
//                    TextWriter.renderText("playerpos: " + String.format("%.3f", GameClient.player.getX()), 0, tilesY - 2f);
//                    TextWriter.renderText(String.format("%.3f", GameClient.player.getY()), 6.5f, tilesY - 2f);
//                    // Mausposition:
//                    TextWriter.renderText(String.format("Mouse: %.2f", GameInput.getLogicMouseX()), 0, tilesY - 2.5f);
//                    TextWriter.renderText(String.format("%.2f", GameInput.getLogicMouseY()), 6.5f, tilesY - 2.5f);
//                }
//                if (NetStats.netGraph >= 3) {
//                    TextWriter.renderText("----------SERVER-NET----------", 0, tilesY - 3f);
//                    TextWriter.renderText("Cmds/Prios:", 0, tilesY - 3.5f);
//                    TextWriter.renderText(String.format("%.3f", NetStats.avgNumberOfCmdsPerPacket), 4, tilesY - 3.5f);
//                    TextWriter.renderText(String.format("%.3f", NetStats.avgNumberOfPrioCmdsPerPacket), 7, tilesY - 3.5f);
//                    TextWriter.renderText("PackLoad: ", 0, tilesY - 4f);
//                    TextWriter.renderText(String.format("%.3f", NetStats.avgLoadPerPacket), 3.5f, tilesY - 4f);
//                    TextWriter.renderText("PPT:", 5.5f, tilesY - 4f);
//                    TextWriter.renderText(String.format("%.3f", NetStats.recentNumberOfPacketsPerTick), 7.5f, tilesY - 4f);
//                    TextWriter.renderText("Perc Out/Retrans:", 0, tilesY - 4.5f);
//                    TextWriter.renderText(String.format("%.0f", NetStats.recentOutBufferLoad), 6f, tilesY - 4.5f);
//                    TextWriter.renderText(String.format("%.3f", NetStats.recentRetransmitNumber), 7.5f, tilesY - 4.5f);
//                    TextWriter.renderText("Queues Cmd/Prio:", 0, tilesY - 5f);
//                    TextWriter.renderText(String.format("%.0f", NetStats.recentOutQueueSize), 6f, tilesY - 5f);
//                    TextWriter.renderText(String.format("%.0f", NetStats.recentPrioOutQueueSize), 9f, tilesY - 5f);
//                }
//            } else {
//                TextWriter.renderText(" LOST CONNECTION TO SERVER", 0, tilesY - 1.5f);
//            }
//        }
//        glColor4f(1f, 1f, 1f, 1f);
    }
}
