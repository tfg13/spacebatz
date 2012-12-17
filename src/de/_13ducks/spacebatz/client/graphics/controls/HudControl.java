package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.Settings;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.shared.Item;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import de._13ducks.spacebatz.client.graphics.TextWriter;

/**
 *
 * @author House of Nikolouse
 */
public class HudControl implements Control {

    private Texture itemTiles;
    private Camera camera;

    public HudControl(Renderer renderer) {
        itemTiles = renderer.getTextureByName("item.png");
        camera = renderer.getCamera();
    }

    @Override
    public void render(Renderer renderer) {
        TextWriter textWriter = renderer.getTextWriter();
        
        if (GameClient.getPlayer().isDead()) {
            if (GameClient.frozenGametick >= GameClient.getPlayer().getRespawntick()) {
                textWriter.renderText("Press <Fire> to respawn", 10.5f, camera.getTilesY() - 2.5f);
            } else {
                int seconds = (int) Math.ceil((GameClient.getPlayer().getRespawntick() - GameClient.frozenGametick) * Settings.SERVER_TICKRATE / 1000.0);
                textWriter.renderText("Respawn in " + seconds + " Seconds", 10.5f, camera.getTilesY() - 2.5f);
            }
        } else {
            // Lebensenergie-Balken im HUD zeichnen
            int maxhp = Math.max(1, GameClient.getPlayer().getHealthpointsmax());
            int hp = Math.min(GameClient.getPlayer().getHealthpoints(), maxhp);
            hp = Math.max(hp, 0);

            glDisable(GL_TEXTURE_2D);
            // schwarzer Hintergrund
            glColor3f(0.0f, 0.0f, 0.0f);
            glRectf(0.02f * camera.getTilesX(), 0.02f * camera.getTilesY(), 0.3f * camera.getTilesX(), 0.06f * camera.getTilesY());
            // roter HP-Balken, Länge anhängig von HP
            glColor3f(0.7f, 0.0f, 0.0f);
            glRectf(0.03f * camera.getTilesX(), 0.03f * camera.getTilesY(), (0.03f + 0.26f * ((float) hp / maxhp)) * camera.getTilesX(), 0.05f * camera.getTilesY());
            glEnable(GL_TEXTURE_2D);
            glColor3f(1f, 1f, 1f);
        }

        // angelegte Waffen in Hud zeichnen
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {

            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];

            if (item != null) {
                float x = 0.01f;
                float y = 0.7f - 0.1f * j;
                float width = 0.05f;
                float height = 0.05f;

                renderer.setTilemap(itemTiles);
                renderer.setTileSize(32, 32);
                renderer.setScreenMapping(0, 1, 0, 1);
                renderer.drawTile(item.getPic(), x, y, width, height);
                renderer.restoreScreenMapping();
            }
        }

        // Waffen-Overheat in Hud zeichnen
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {

            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];
            if (item != null && item.getWeaponAbility().getWeaponStats().getMaxoverheat() > 0) {

                float overheatpermax = (float) (item.getOverheat() / item.getWeaponAbility().getWeaponStats().getMaxoverheat());
                if (overheatpermax > 1) {
                    overheatpermax = 1.0f;
                } else if (overheatpermax < 0) {
                    overheatpermax = 0.0f;
                }

                float height = 0.69f - 0.1f * j;

                glDisable(GL_TEXTURE_2D);
                // weißer Hintergrund
                glColor3f(1.0f, 1.0f, 1.0f);
                glRectf(0.01f * camera.getTilesX(), height * camera.getTilesY(), 0.06f * camera.getTilesX(), (height + 0.01f) * camera.getTilesY());
                // roter HP-Balken, Länge anhängig von HP
                glColor3f(0.7f, 0.0f, 0.0f);
                glRectf(0.012f * camera.getTilesX(), (height + 0.002f) * camera.getTilesY(), (0.012f + 0.046f * overheatpermax) * camera.getTilesX(), (height + 0.008f) * camera.getTilesY());
                glEnable(GL_TEXTURE_2D);
                glColor3f(1f, 1f, 1f);
            }
        }

        // Markierung an angelegte Waffe:
        glDisable(GL_TEXTURE_2D);
        // weißer Hintergrund
        glColor3f(0.7f, 0.0f, 0.0f);
        float height = 0.7f - 0.1f * GameClient.player.getSelectedattack();
        glRectf(0.0f * camera.getTilesX(), height * camera.getTilesY(), 0.005f * camera.getTilesX(), (height + 0.05f) * camera.getTilesY());
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
    }

    @Override
    public void input() {
    }
}
