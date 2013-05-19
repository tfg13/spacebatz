package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.RenderUtils;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.client.graphics.overlay.Overlay;
import de._13ducks.spacebatz.client.graphics.renderer.impl.GodControl;
import de._13ducks.spacebatz.shared.Item;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author House of Nikolouse
 */
public class HudControl extends Overlay {

    private Texture itemTiles;
    private Texture hud1;

    public HudControl() {
        itemTiles = RenderUtils.getTextureByName("item.png");
        hud1 = RenderUtils.getTextureByName("hud1.png");
    }

    @Override
    public void render() {

        // HUD-Hintergrund:
        glColor3f(1.0f, 1.0f, 1.0f);
        glEnable(GL_TEXTURE_2D);

        hud1.bind();

        // HUD-Bild bei HP
        float width1 = (0.475f / 16.0f * 9.0f) * GodControl.tilesX;
        float height1 = 0.475f * GodControl.tilesY * 0.262f;
        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
        glTexCoord2f(0, 1);
        glVertex3f(0, 0, 0.0f);
        glTexCoord2f(1, 1);
        glVertex3f(width1, 0, 0.0f);
        glTexCoord2f(1, 0.738f);
        glVertex3f(width1, height1, 0.0f);
        glTexCoord2f(0, 0.738f);
        glVertex3f(0, height1, 0.0f);
        glEnd(); // Zeichnen des QUADs fertig } }

        // HUD-Bild bei Waffen
        float height2 = 0.42f * GodControl.tilesY;
        float height3 = 0.77f * GodControl.tilesY;
        float width2 = width1 * 0.234f;
        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
        glTexCoord2f(0, 0.738f);
        glVertex3f(0, height2, 0.0f);
        glTexCoord2f(0.234f, 0.738f);
        glVertex3f(width2, height2, 0.0f);
        glTexCoord2f(0.234f, 0.0f);
        glVertex3f(width2, height3, 0.0f);
        glTexCoord2f(0, 0.0f);
        glVertex3f(0, height3, 0.0f);
        glEnd(); // Zeichnen des QUADs fertig } }


        if (GameClient.logicPlayer.isDead()) {
            glDisable(GL_TEXTURE_2D);
            glColor4f(1.0f, 0.1f, 0.0f, 0.5f);
            glRectf(0.0f * GodControl.tilesX, 0.0f * GodControl.tilesY, 1.0f * GodControl.tilesX, 1.0f * GodControl.tilesY);
            glEnable(GL_TEXTURE_2D);
            if (GameClient.frozenGametick >= GameClient.player.getRespawntick()) {
                TextWriter.renderText("Press <Fire> to respawn", 0.5f * GodControl.tilesX, 0.5f * GodControl.tilesY);
            } else {
                int seconds = (int) Math.ceil((GameClient.player.getRespawntick() - GameClient.frozenGametick) * GameClient.getNetwork2().getLogicTickDelay() / 1000.0);
                TextWriter.renderText("Respawn in " + seconds + " Seconds", 0.5f * GodControl.tilesX, 0.5f * GodControl.tilesY);
            }
        }

        // Lebensenergie-Balken im HUD zeichnen
        int maxhp = Math.max(1, GameClient.player.getHealthpointsmax());
        int hp = Math.min(GameClient.player.getHealthpoints(), maxhp);
        hp = Math.max(hp, 0);
        if (GameClient.logicPlayer.isDead()) {
            hp = 0;
        }

        glDisable(GL_TEXTURE_2D);
        // roter HP-Balken, Länge anhängig von HP
        glColor3f(0.7f, 0.0f, 0.0f);
        glRectf(0.0295f * GodControl.tilesX, 0.028f * GodControl.tilesY, (0.0295f + 0.1655f * ((float) hp / maxhp)) * GodControl.tilesX, 0.04f * GodControl.tilesY);
        glEnable(GL_TEXTURE_2D);

        // angelegte Waffen in Hud zeichnen
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {

            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];

            if (item != null) {
                float x = 0.016f;
                float y = 0.705f - 0.1f * j;
                float width = 0.05f / 16.0f * 9.0f;
                float height = 0.05f;

                RenderUtils.setTilemap(itemTiles);
                RenderUtils.setTileSize(32, 32);
                RenderUtils.setScreenMapping(0, 1, 0, 1);
                RenderUtils.drawTile(item.getPic(), x, y, width, height);
                RenderUtils.restoreScreenMapping();
            }
        }

        // Rahmen um angelegte Waffen in Hud zeichnen
        glDisable(GL_TEXTURE_2D);
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {

            glColor3f(0.0f, 0.0f, 0.0f);

            float x1 = 0.014f * GodControl.tilesX;
            float y1 = (0.705f - 0.1f * j) * GodControl.tilesY;
            float x2 = (0.018f + 0.05f / 16.0f * 9.0f) * GodControl.tilesX;
            float y2 = (0.7505f - 0.1f * j) * GodControl.tilesY;

            glBegin(GL_LINE_LOOP);
            glVertex2d(x1, y1);
            glVertex2d(x2, y1);
            glVertex2d(x2, y2);
            glVertex2d(x1, y2);

            glEnd();
        }

        // Waffen-Overheat in Hud zeichnen
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {
            float height = 0.69f - 0.1f * j;

            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];
            int maxOverheat = 0;
            if (item != null) {
                maxOverheat = (int) (item.getWeaponAbility().getWeaponStats().getMaxoverheat() * (1 + item.getWeaponAbility().getWeaponStats().getMaxoverheatMultiplicatorBonus()));
            }

            if (item != null && maxOverheat > 0) {

                float overheatpermax = (float) (item.getOverheat() / maxOverheat);
                if (overheatpermax > 1) {
                    overheatpermax = 1.0f;
                } else if (overheatpermax < 0) {
                    overheatpermax = 0.0f;
                }

                // weißer Hintergrund
                glColor3f(1.0f, 1.0f, 1.0f);
                glRectf(0.005f * GodControl.tilesX, height * GodControl.tilesY, 0.055f * GodControl.tilesX, (height + 0.01f) * GodControl.tilesY);
                // roter Overheat-Balken
                glColor3f(0.7f, 0.0f, 0.0f);
                glRectf(0.007f * GodControl.tilesX, (height + 0.002f) * GodControl.tilesY, (0.007f + 0.046f * overheatpermax) * GodControl.tilesX, (height + 0.008f) * GodControl.tilesY);
            } else {
                // grauer Hintergrund
                glColor3f(0.6f, 0.6f, 0.6f);
                glRectf(0.005f * GodControl.tilesX, height * GodControl.tilesY, 0.055f * GodControl.tilesX, (height + 0.01f) * GodControl.tilesY);
            }
            glColor3f(1f, 1f, 1f);
        }

        // Markierung an angelegte Waffe:
        glColor3f(0.7f, 0.0f, 0.0f);
        float height = 0.7f - 0.1f * GameClient.player.getSelectedattack();
        glRectf(0.0f * GodControl.tilesX, height * GodControl.tilesY, 0.005f * GodControl.tilesX, (height + 0.05f) * GodControl.tilesY);
        glEnable(GL_TEXTURE_2D);
        glColor3f(1f, 1f, 1f);
    }
}
