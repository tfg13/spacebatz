package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.shared.Item;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

/**
 *
 * @author House of Nikolouse
 */
public class HudControl implements Control {

    private Texture itemTiles;
    private Texture hud1;
    private Camera camera;

    public HudControl(Renderer renderer) {
        itemTiles = renderer.getTextureByName("item.png");
        hud1 = renderer.getTextureByName("hud1.png");
        camera = renderer.getCamera();
    }

    @Override
    public void render(Renderer renderer) {
        TextWriter textWriter = renderer.getTextWriter();

        // HUD-Hintergrund:
        glColor3f(1.0f, 1.0f, 1.0f);
        glEnable(GL_TEXTURE_2D);

        hud1.bind();

        // HUD-Bild bei HP
        float width1 = (0.475f / 16.0f * 9.0f) * camera.getTilesX();
        float height1 = 0.475f * camera.getTilesY() * 0.262f;
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
        float height2 = 0.42f * camera.getTilesY();
        float height3 = 0.77f * camera.getTilesY();
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
            glRectf(0.0f * camera.getTilesX(), 0.0f * camera.getTilesY(), 1.0f * camera.getTilesX(), 1.0f * camera.getTilesY());
            glEnable(GL_TEXTURE_2D);
            if (GameClient.frozenGametick >= GameClient.player.getRespawntick()) {
                textWriter.renderText("Press <Fire> to respawn", 0.5f * camera.getTilesX(), 0.5f * camera.getTilesY());
            } else {
                int seconds = (int) Math.ceil((GameClient.player.getRespawntick() - GameClient.frozenGametick) * GameClient.getNetwork2().getLogicTickDelay() / 1000.0);
                textWriter.renderText("Respawn in " + seconds + " Seconds", 0.5f * camera.getTilesX(), 0.5f * camera.getTilesY());
            }
        }

        // Lebensenergie-Balken im HUD zeichnen
        int maxhp = Math.max(1, GameClient.player.getHealthpointsmax());
        int hp = Math.min(GameClient.player.getHealthpoints(), maxhp);
        hp = Math.max(hp, 0);

        glDisable(GL_TEXTURE_2D);
        // roter HP-Balken, Länge anhängig von HP
        glColor3f(0.7f, 0.0f, 0.0f);
        glRectf(0.0295f * camera.getTilesX(), 0.028f * camera.getTilesY(), (0.0295f + 0.1655f * ((float) hp / maxhp)) * camera.getTilesX(), 0.04f * camera.getTilesY());
        glEnable(GL_TEXTURE_2D);

        // angelegte Waffen in Hud zeichnen
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {

            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];

            if (item != null) {
                float x = 0.016f;
                float y = 0.705f - 0.1f * j;
                float width = 0.05f / 16.0f * 9.0f;
                float height = 0.05f;

                renderer.setTilemap(itemTiles);
                renderer.setTileSize(32, 32);
                renderer.setScreenMapping(0, 1, 0, 1);
                renderer.drawTile(item.getPic(), x, y, width, height);
                renderer.restoreScreenMapping();
            }
        }

        // Rahmen um angelegte Waffen in Hud zeichnen
        glDisable(GL_TEXTURE_2D);
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {

            glColor3f(0.0f, 0.0f, 0.0f);

            float x1 = 0.014f * camera.getTilesX();
            float y1 = (0.705f - 0.1f * j) * camera.getTilesY();
            float x2 = (0.018f + 0.05f / 16.0f * 9.0f) * camera.getTilesX();
            float y2 = (0.7505f - 0.1f * j) * camera.getTilesY();

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
            if (item != null && item.getWeaponAbility().getWeaponStats().getMaxoverheat() > 0) {

                float overheatpermax = (float) (item.getOverheat() / item.getWeaponAbility().getWeaponStats().getMaxoverheat());
                if (overheatpermax > 1) {
                    overheatpermax = 1.0f;
                } else if (overheatpermax < 0) {
                    overheatpermax = 0.0f;
                }

                // weißer Hintergrund
                glColor3f(1.0f, 1.0f, 1.0f);
                glRectf(0.005f * camera.getTilesX(), height * camera.getTilesY(), 0.055f * camera.getTilesX(), (height + 0.01f) * camera.getTilesY());
                // roter Overheat-Balken
                glColor3f(0.7f, 0.0f, 0.0f);
                glRectf(0.007f * camera.getTilesX(), (height + 0.002f) * camera.getTilesY(), (0.007f + 0.046f * overheatpermax) * camera.getTilesX(), (height + 0.008f) * camera.getTilesY());
            } else {
                // grauer Hintergrund
                glColor3f(0.6f, 0.6f, 0.6f);
                glRectf(0.005f * camera.getTilesX(), height * camera.getTilesY(), 0.055f * camera.getTilesX(), (height + 0.01f) * camera.getTilesY());
            }
            glColor3f(1f, 1f, 1f);
        }

        // Markierung an angelegte Waffe:
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
