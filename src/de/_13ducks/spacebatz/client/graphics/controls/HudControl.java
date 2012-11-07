package de._13ducks.spacebatz.client.graphics.controls;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.shared.Item;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

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


        // angelegte Waffen in Hud zeichnen
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {
            
            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];
            
            if (item != null) {
                float x = 0.01f;
                float y = 0.7f - 0.1f * j;
                float width = 0.05f;
                float height = 0.05f;

                renderer.setTilemap(itemTiles);
                renderer.setTileSize(16, 16);
                renderer.setScreenMapping(0, 1, 0, 1);
                renderer.drawTile(item.getPic(), x, y, width, height);
                renderer.restoreScreenMapping();
            }
        }
    }

    @Override
    public void input() {
    }
}
