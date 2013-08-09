package de._13ducks.spacebatz.client.graphics.overlay.impl;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.overlay.Overlay;
import de._13ducks.spacebatz.client.graphics.vao.DynamicRectangleVAO;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTexturedRectangleVAO;
import de._13ducks.spacebatz.client.graphics.vao.DynamicTileVAO;
import de._13ducks.spacebatz.client.graphics.vao.VAOFactory;
import de._13ducks.spacebatz.server.data.Inventory;
import de._13ducks.spacebatz.shared.DefaultSettings;
import de._13ducks.spacebatz.shared.Item;

/**
 *
 * @author House of Nikolouse
 */
public class HudOverlay extends Overlay {

    private DynamicTexturedRectangleVAO healthbarBackground;
    private DynamicTexturedRectangleVAO weaposlotsBackground;
    private DynamicTileVAO weapon1;
    private DynamicTileVAO weapon2;
    private DynamicTileVAO weapon3;
    private DynamicRectangleVAO selectedWeaponBackground;
    private DynamicRectangleVAO healthbarRect;

    public HudOverlay() {
        int x, y, width, height;
        x = 0;
        y = 0;
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.2f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.1f);
        healthbarBackground = VAOFactory.createDynamicTexturedRectangleVAO(x, y, width, height, "hud1.png", 0, 380, 396, 132);

        x = 0;
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.42f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.09f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.42f);
        weaposlotsBackground = VAOFactory.createDynamicTexturedRectangleVAO(x, y, width, height, "hud1.png", 0, 0, 118, 332);

        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.08f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.08f);
        selectedWeaponBackground = VAOFactory.createDynamicRectangleVAO(0, 0, width, height, new float[]{0.9f, 0.3f, 0.1f, 1f});

        x = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.0265f);
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.049f);
        width = 0;
        height = 0;
        healthbarRect = VAOFactory.createDynamicRectangleVAO(x, y, width, height, new float[]{0.7f, 0.0f, 0.0f, 1f});

        x = 0;
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.72f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.08f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.08f);
        weapon1 = VAOFactory.IOnlyWantToDrawATile(x, y, width, height, "item.png", 0, 32);

        x = 0;
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.60f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.08f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.08f);
        weapon2 = VAOFactory.IOnlyWantToDrawATile(x, y, width, height, "item.png", 0, 32);

        x = 0;
        y = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.48f);
        width = (int) (DefaultSettings.CLIENT_GFX_RES_X * 0.08f);
        height = (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.08f);
        weapon3 = VAOFactory.IOnlyWantToDrawATile(x, y, width, height, "item.png", 0, 32);

    }

    @Override
    public void render() {
        if (GameClient.player == null) {
            return;
        }


        healthbarBackground.render();
        weaposlotsBackground.render();

        switch (GameClient.player.inventory.getActiveWeaponSlot()) {
            case Inventory.WEAPONSLOT1:
                selectedWeaponBackground.setRenderPosition(0, (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.72f));
                break;
            case Inventory.WEAPONSLOT2:
                selectedWeaponBackground.setRenderPosition(0, (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.60f));
                break;
            case Inventory.WEAPONSLOT3:
                selectedWeaponBackground.setRenderPosition(0, (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.48f));
                break;
            default:
                selectedWeaponBackground.setRenderPosition(0, (int) (DefaultSettings.CLIENT_GFX_RES_Y * 0.72f));
        }
        selectedWeaponBackground.render();

        Item weapon = GameClient.player.inventory.getItem(Inventory.WEAPONSLOT1);
        if (weapon != null) {
            weapon1.setSourceTile(weapon.getPic());
            weapon1.render();
        }
        weapon = GameClient.player.inventory.getItem(Inventory.WEAPONSLOT2);
        if (weapon != null) {
            weapon2.setSourceTile(weapon.getPic());
            weapon2.render();
        }
        weapon = GameClient.player.inventory.getItem(Inventory.WEAPONSLOT3);
        if (weapon != null) {
            weapon3.setSourceTile(weapon.getPic());
            weapon3.render();
        }

        // Lebensenergie-Balken im HUD zeichnen
        int maxhp = Math.max(1, GameClient.player.getHealthpointsmax());
        int hp = Math.min(GameClient.player.getHealthpoints(), maxhp);
        hp = Math.max(hp, 0);
        if (GameClient.logicPlayer.isDead()) {
            hp = 0;
        }
        healthbarRect.setRenderSize((int) (0.1645f * ((float) hp / maxhp) * DefaultSettings.CLIENT_GFX_RES_X), (int) (0.018f * DefaultSettings.CLIENT_GFX_RES_Y));
        healthbarRect.render();

//        // HUD-Hintergrund:
//        glColor3f(1.0f, 1.0f, 1.0f);
//        glEnable(GL_TEXTURE_2D);
//
//        hud1.bind();
//
//        // HUD-Bild bei HP
//        float width1 = (0.475f / 16.0f * 9.0f) * LegacyRenderer.tilesX;
//        float height1 = 0.475f * LegacyRenderer.tilesY * 0.262f;
//        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
//        glTexCoord2f(0, 1);
//        glVertex3f(0, 0, 0.0f);
//        glTexCoord2f(1, 1);
//        glVertex3f(width1, 0, 0.0f);
//        glTexCoord2f(1, 0.738f);
//        glVertex3f(width1, height1, 0.0f);
//        glTexCoord2f(0, 0.738f);
//        glVertex3f(0, height1, 0.0f);
//        glEnd(); // Zeichnen des QUADs fertig } }
//
//        // HUD-Bild bei Waffen
//        float height2 = 0.42f * LegacyRenderer.tilesY;
//        float height3 = 0.77f * LegacyRenderer.tilesY;
//        float width2 = width1 * 0.234f;
//        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
//        glTexCoord2f(0, 0.738f);
//        glVertex3f(0, height2, 0.0f);
//        glTexCoord2f(0.234f, 0.738f);
//        glVertex3f(width2, height2, 0.0f);
//        glTexCoord2f(0.234f, 0.0f);
//        glVertex3f(width2, height3, 0.0f);
//        glTexCoord2f(0, 0.0f);
//        glVertex3f(0, height3, 0.0f);
//        glEnd(); // Zeichnen des QUADs fertig } }
//
//
//        if (GameClient.logicPlayer.isDead()) {
//            glDisable(GL_TEXTURE_2D);
//            glColor4f(1.0f, 0.1f, 0.0f, 0.5f);
//            glRectf(0.0f * LegacyRenderer.tilesX, 0.0f * LegacyRenderer.tilesY, 1.0f * LegacyRenderer.tilesX, 1.0f * LegacyRenderer.tilesY);
//            glEnable(GL_TEXTURE_2D);
//            if (GameClient.frozenGametick >= GameClient.player.getRespawntick()) {
//                TextWriter.renderText("Press <Fire> to respawn", 0.5f * LegacyRenderer.tilesX, 0.5f * LegacyRenderer.tilesY);
//            } else {
//                int seconds = (int) Math.ceil((GameClient.player.getRespawntick() - GameClient.frozenGametick) * GameClient.getNetwork2().getLogicTickDelay() / 1000.0);
//                TextWriter.renderText("Respawn in " + seconds + " Seconds", 0.5f * LegacyRenderer.tilesX, 0.5f * LegacyRenderer.tilesY);
//            }
//        }
//
//        // Lebensenergie-Balken im HUD zeichnen
//        int maxhp = Math.max(1, GameClient.player.getHealthpointsmax());
//        int hp = Math.min(GameClient.player.getHealthpoints(), maxhp);
//        hp = Math.max(hp, 0);
//        if (GameClient.logicPlayer.isDead()) {
//            hp = 0;
//        }
//
//        glDisable(GL_TEXTURE_2D);
//        // roter HP-Balken, Länge anhängig von HP
//        glColor3f(0.7f, 0.0f, 0.0f);
//        glRectf(0.0295f * LegacyRenderer.tilesX, 0.028f * LegacyRenderer.tilesY, (0.0295f + 0.1655f * ((float) hp / maxhp)) * LegacyRenderer.tilesX, 0.04f * LegacyRenderer.tilesY);
//        glEnable(GL_TEXTURE_2D);
//
//        // angelegte Waffen in Hud zeichnen
//        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {
//
//            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];
//
//            if (item != null) {
//                float x = 0.016f;
//                float y = 0.705f - 0.1f * j;
//                float width = 0.05f / 16.0f * 9.0f;
//                float height = 0.05f;
//
//                RenderUtils.setTilemap(itemTiles);
//                RenderUtils.setTileSize(32, 32);
//                RenderUtils.setScreenMapping(0, 1, 0, 1);
//                RenderUtils.drawTile(item.getPic(), x, y, width, height);
//                RenderUtils.restoreScreenMapping();
//            }
//        }
//
//        // Rahmen um angelegte Waffen in Hud zeichnen
//        glDisable(GL_TEXTURE_2D);
//        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {
//
//            glColor3f(0.0f, 0.0f, 0.0f);
//
//            float x1 = 0.014f * LegacyRenderer.tilesX;
//            float y1 = (0.705f - 0.1f * j) * LegacyRenderer.tilesY;
//            float x2 = (0.018f + 0.05f / 16.0f * 9.0f) * LegacyRenderer.tilesX;
//            float y2 = (0.7505f - 0.1f * j) * LegacyRenderer.tilesY;
//
//            glBegin(GL_LINE_LOOP);
//            glVertex2d(x1, y1);
//            glVertex2d(x2, y1);
//            glVertex2d(x2, y2);
//            glVertex2d(x1, y2);
//
//            glEnd();
//        }
//
//        // Waffen-Overheat in Hud zeichnen
//        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {
//            float height = 0.69f - 0.1f * j;
//
//            Item item = GameClient.getEquippedItems().getEquipslots()[1][j];
//            int maxOverheat = 0;
//            if (item != null) {
//                maxOverheat = (int) (item.getWeaponAbility().getWeaponStats().getMaxoverheat() * (1 + item.getWeaponAbility().getWeaponStats().getMaxoverheatMultiplicatorBonus()));
//            }
//
//            if (item != null && maxOverheat > 0) {
//
//                float overheatpermax = (float) (item.getOverheat() / maxOverheat);
//                if (overheatpermax > 1) {
//                    overheatpermax = 1.0f;
//                } else if (overheatpermax < 0) {
//                    overheatpermax = 0.0f;
//                }
//
//                // weißer Hintergrund
//                glColor3f(1.0f, 1.0f, 1.0f);
//                glRectf(0.005f * LegacyRenderer.tilesX, height * LegacyRenderer.tilesY, 0.055f * LegacyRenderer.tilesX, (height + 0.01f) * LegacyRenderer.tilesY);
//                // roter Overheat-Balken
//                glColor3f(0.7f, 0.0f, 0.0f);
//                glRectf(0.007f * LegacyRenderer.tilesX, (height + 0.002f) * LegacyRenderer.tilesY, (0.007f + 0.046f * overheatpermax) * LegacyRenderer.tilesX, (height + 0.008f) * LegacyRenderer.tilesY);
//            } else {
//                // grauer Hintergrund
//                glColor3f(0.6f, 0.6f, 0.6f);
//                glRectf(0.005f * LegacyRenderer.tilesX, height * LegacyRenderer.tilesY, 0.055f * LegacyRenderer.tilesX, (height + 0.01f) * LegacyRenderer.tilesY);
//            }
//            glColor3f(1f, 1f, 1f);
//        }
//
//        // Markierung an angelegte Waffe:
//        glColor3f(0.7f, 0.0f, 0.0f);
//        float height = 0.7f - 0.1f * GameClient.player.getSelectedattack();
//        glRectf(0.0f * LegacyRenderer.tilesX, height * LegacyRenderer.tilesY, 0.005f * LegacyRenderer.tilesX, (height + 0.05f) * LegacyRenderer.tilesY);
//        glEnable(GL_TEXTURE_2D);
//        glColor3f(1f, 1f, 1f);
    }
}
