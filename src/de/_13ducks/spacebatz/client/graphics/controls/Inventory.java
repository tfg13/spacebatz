package de._13ducks.spacebatz.client.graphics.controls;

import static de._13ducks.spacebatz.Settings.*;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_EQUIP_ITEM;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_INV_ITEM_MOVE;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_ITEM_DEQUIP;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_SWITCH_WEAPON;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

/**
 * Das Inventar.
 *
 * @author michael
 */
public class Inventory implements Control {

    /**
     * Sagt, ob Inventar gerade geöffnet ist (Taste i)
     */
    private boolean lmbpressed; // linke maustaste gedrückt
    private int inventorypage; // aktuelle Inventarseite
    private int selecteditemslot; // zuletzt angeklickter Inventarslot
    private Texture inventoryPic;
    private Texture itemTiles;

    public Inventory(Renderer renderer) {
        inventoryPic = renderer.getTextureByName("inventory2.png");
        itemTiles = renderer.getTextureByName("item.png");
        selecteditemslot = -1;
    }

    @Override
    public void render(Renderer renderer) {
        Camera camera = renderer.getCamera();
        TextWriter textWriter = renderer.getTextWriter();
        // Inventory-Hintergrund zeichnen
        inventoryPic.bind();

        glBegin(GL_QUADS);
        glTexCoord2f(0, 1);
        glVertex3f(0, 0, 0);
        glTexCoord2f(1, 1);
        glVertex3f(camera.getTilesX(), 0, 0);
        glTexCoord2f(1, 0);
        glVertex3f(camera.getTilesX(), camera.getTilesY(), 0);
        glTexCoord2f(0, 0);
        glVertex3f(0, camera.getTilesY(), 0);
        glEnd();


        // Items im Inventory zeichnen
        // Anzahl der Materialien:
        textWriter.renderText(String.valueOf(GameClient.getMaterial(0)), 0.12f * camera.getTilesX(), 0.44f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(1)), 0.45f * camera.getTilesX(), 0.44f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(2)), 0.75f * camera.getTilesX(), 0.44f * camera.getTilesY());

        for (int i = 12 * inventorypage; i < 12 * inventorypage + 12; i++) {

            if (GameClient.getItems()[i] == null || i == selecteditemslot) {
                // Slot leer oder gerade selected -> nicht zeichnen
                continue;
            }
            itemTiles.bind();

            Item item = GameClient.getItems()[i];

            float x = (0.1075f + 0.133f * (i % 6)) * camera.getTilesX();

            float y;
            if (i % 12 < 6) {
                y = 0.191f * camera.getTilesY();
            } else {
                y = 0.061f * camera.getTilesY();
            }

            float width = 0.11f * camera.getTilesX();
            float height = 0.11f * camera.getTilesY();

            float v = 0.0625f * (int) item.getPic();
            float w = 0.0625f * ((int) item.getPic() / 16);

            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(v, w + 0.0625f);
            glVertex3f(x, y, 0.0f);
            glTexCoord2f(v + 0.0625f, w + 0.0625f);
            glVertex3f(x + width, y, 0.0f);
            glTexCoord2f(v + 0.0625f, w);
            glVertex3f(x + width, y + height, 0.0f);
            glTexCoord2f(v, w);
            glVertex3f(x, y + height, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }


        // ausgewählten Waffenslot im Inventar markieren:
        glDisable(GL_TEXTURE_2D);
        float wx = 0.227f + 0.172f * GameClient.getPlayer().getSelectedattack();

        glColor3f(0.7f, 0.0f, 0.0f);
        glRectf(wx * camera.getTilesX(), 0.59f * camera.getTilesY(), (wx + 0.14f) * camera.getTilesX(), 0.6f * camera.getTilesY());
        glColor3f(1f, 1f, 1f);
        glEnable(GL_TEXTURE_2D);


        // angelegte Items in ihre Slots im Inventar zeichnen
        itemTiles.bind();
        for (int i = 1; i <= 2; i++) {
            for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[i].length; j++) {
                Item item = GameClient.getEquippedItems().getEquipslots()[i][j];
                if (item != null) {
                    // Item zeichnen;
                    float x;
                    if (i == 1) {
                        x = (0.24f + 0.17f * j) * camera.getTilesX();
                    } else {
                        x = 0.41f * camera.getTilesX();
                    }
                    float y = (0.61f + 0.2f * (i - 1)) * camera.getTilesY();

                    float width = 0.11f * camera.getTilesX();
                    float height = 0.11f * camera.getTilesY();

                    float v = 0.0625f * (int) item.getPic();
                    float w = 0.0625f * ((int) item.getPic() / 16);

                    glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                    glTexCoord2f(v, w + 0.0625f);
                    glVertex3f(x, y, 0.0f);
                    glTexCoord2f(v + 0.0625f, w + 0.0625f);
                    glVertex3f(x + width, y, 0.0f);
                    glTexCoord2f(v + 0.0625f, w);
                    glVertex3f(x + width, y + height, 0.0f);
                    glTexCoord2f(v, w);
                    glVertex3f(x, y + height, 0.0f);
                    glEnd(); // Zeichnen des QUADs fertig } }
                }
            }
        }


        // selected Item zum Mauszeiger zeichnen
        if (selecteditemslot != -1) {
            itemTiles.bind();
            Item item = GameClient.getItems()[selecteditemslot];
            float x = (float) Mouse.getX() / CLIENT_GFX_RES_X * camera.getTilesX();
            float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y * camera.getTilesY();

            float size = 0.08f;

            float v = 0.0625f * (int) item.getPic();
            float w = 0.0625f * ((int) item.getPic() / 16);

            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(v, w + 0.0625f);
            glVertex3f(x - camera.getTilesX() * size / 2, y - camera.getTilesX() * size / 2, 0.0f);
            glTexCoord2f(v + 0.0625f, w + 0.0625f);
            glVertex3f(x + camera.getTilesX() * size / 2, y - camera.getTilesX() * size / 2, 0.0f);
            glTexCoord2f(v + 0.0625f, w);
            glVertex3f(x + camera.getTilesX() * size / 2, y + camera.getTilesX() * size / 2, 0.0f);
            glTexCoord2f(v, w);
            glVertex3f(x - camera.getTilesX() * size / 2, y + camera.getTilesX() * size / 2, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }

        // Mousehover über Item zeichnen

        float x = (float) Mouse.getX() / CLIENT_GFX_RES_X;
        float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y;

        // Maus über Item im Inventar?
        int slothovered = -1;
        if (y > 0.1812 && y <= 0.3156) {
            for (int i = 0; i < 6; i++) {
                if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                    slothovered = i + inventorypage * 12;
                    break;
                }
            }
        } else if (y > 0.05156 && y <= 0.1813) {
            for (int i = 0; i < 6; i++) {
                if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                    slothovered = i + 6 + inventorypage * 12;
                    break;
                }
            }
        }

        Item item = null;
        if (slothovered != -1 && slothovered != selecteditemslot) {
            if (GameClient.getItems()[slothovered] != null) {
                item = GameClient.getItems()[slothovered];
            }
            // Einer der Ausrüstungsslots?
        } else if (x > 0.4 && x < 0.54) {
            if (y > 0.8 && y < 0.92) {
                item = GameClient.getEquippedItems().getEquipslots()[2][0];
            } else if (y > 0.61 && y < 0.74) {
                item = GameClient.getEquippedItems().getEquipslots()[1][1];
            }
        } else if (y > 0.8 && y < 0.92) {
            if (x > 0.4 && x < 0.54) {
                // Hutslot
                item = GameClient.getEquippedItems().getEquipslots()[2][0];
            }
        } else if (y > 0.61 && y < 0.74) {
            // ein Waffenslot?
            if (x > 0.22 && x < 0.36) {
                item = GameClient.getEquippedItems().getEquipslots()[1][0];
            } else if (x > 0.4 && x < 0.54) {
                item = GameClient.getEquippedItems().getEquipslots()[1][1];
            } else if (x > 0.58 && x < 0.72) {
                item = GameClient.getEquippedItems().getEquipslots()[1][2];
            }
        }

        if (item != null) {
            // Item gefunden, jetzt Mousehover rendern
            glDisable(GL_TEXTURE_2D);
            glColor3f(0.9f, 0.9f, 0.9f);
            glRectf((x - 0.01f) * camera.getTilesX(), (y - 0.01f) * camera.getTilesY(), (x + 0.3f) * camera.getTilesX(), (y - 0.015f + 0.05f * item.getItemAttributes().size()) * camera.getTilesY());
            glColor3f(1f, 1f, 1f);
            glEnable(GL_TEXTURE_2D);
            // Namen von Item und Itemattributen, umgekehrte Reihenfolge damit Name oben ist
            float yadd = 0.0f;
            for (int i = item.getItemAttributes().size() - 1; i >= 0; i--) {
                textWriter.renderText(String.valueOf(item.getItemAttributes().get(i).getName()), x * camera.getTilesX(), (y + yadd) * camera.getTilesY());
                yadd += 0.05f;
            }
        }
    }

    @Override
    public void input() {
        // Mausklick suchen
        {
            if (Mouse.isButtonDown(0)) {

                if (!lmbpressed) {
                    lmbpressed = true;
                    float x = (float) Mouse.getX() / CLIENT_GFX_RES_X;
                    float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y;
                    //System.out.println("x " + x + ",y " + y);

                    // Equipslot angeklickt?

                    if (y > 0.8 && y < 0.92) {
                        if (x > 0.4 && x < 0.54) {
                            // Hut-Slot
                            if (selecteditemslot != -1) {
                                Item selecteditem = GameClient.getItems()[selecteditemslot];
                                if ((int) selecteditem.getItemClass() == 2) {
                                    CTS_EQUIP_ITEM.sendEquipItem(selecteditemslot, (byte) 0); // 2 = Hut-Slot
                                    selecteditemslot = -1;
                                }
                            } else {
                                if (GameClient.getEquippedItems().getEquipslots()[2][0] != null) {
                                    CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(2, (byte) 0); // 2 = Hut-Slot
                                }
                            }
                        }
                    }
                    if (y > 0.61 && y < 0.74) {
                        byte weaponslot = -1;
                        if (x > 0.22 && x < 0.36) {
                            weaponslot = 0;
                        } else if (x > 0.4 && x < 0.54) {
                            weaponslot = 1;
                        } else if (x > 0.58 && x < 0.72) {
                            weaponslot = 2;
                        }
                        if (weaponslot != -1) {
                            // Waffenslot
                            if (selecteditemslot != -1) {
                                Item selecteditem = GameClient.getItems()[selecteditemslot];
                                if ((int) selecteditem.getItemClass() == 1) {
                                    CTS_EQUIP_ITEM.sendEquipItem(selecteditemslot, weaponslot); // Slotnummer, zum Auseinanderhalten von den 3 Waffenslots
                                    selecteditemslot = -1;
                                }
                            } else {
                                if (GameClient.getEquippedItems().getEquipslots()[1][weaponslot] != null) {
                                    CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(1, weaponslot); // 1 = Waffen-Slot
                                }
                            }
                        }
                    }

                    // Inventarslot angeklickt?
                    int slotklicked = -1;
                    if (y > 0.1812 && y <= 0.3156) {
                        for (int i = 0; i < 6; i++) {
                            if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                                slotklicked = i + inventorypage * 12;
                                break;
                            }
                        }
                    } else if (y > 0.05156 && y <= 0.1813) {
                        for (int i = 0; i < 6; i++) {
                            if (x > 0.0975 + i * 0.133 && x <= 0.0975 + (i + 1) * 0.133) {
                                slotklicked = i + 6 + inventorypage * 12;
                                break;
                            }
                        }
                    }

                    if (slotklicked != -1) {
                        // gültiger Inventar-Slot angeklickt

                        if (selecteditemslot == -1) {
                            // zur Zeit war kein Slot ausgewählt -> der hier wird
                            if (GameClient.getItems()[slotklicked] != null) {
                                // nur wenn hier ein item drin ist
                                selecteditemslot = slotklicked;
                            }
                        } else {
                            // es war bereits ein Slot ausgewählt
                            CTS_REQUEST_INV_ITEM_MOVE.sendInvItemMove(selecteditemslot, slotklicked);
                            selecteditemslot = -1;
                        }
                    }

                    // nächste / vorherige Seite
                    if (y > 0.14 && y < 0.22) {
                        if (x < 0.1) {
                            if (inventorypage <= 0) {
                                inventorypage = 0;
                            } else {
                                inventorypage--;
                            }
                        } else if (x > 0.9) {
                            if (inventorypage >= 7) {
                                inventorypage = 7;
                            } else {
                                inventorypage++;
                            }
                        }
                    }
                }

            } else {
                lmbpressed = false;

            }
        }

        // Inventar wieder abschalten wenn I gedrückt wird:
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                switch (Keyboard.getEventKey()) {
                    case Keyboard.KEY_I:
                        GameClient.getEngine().getGraphics().toggleInventory();
                        break;
                    case Keyboard.KEY_ESCAPE:
                        GameClient.getEngine().getGraphics().toggleInventory();
                        break;
                    case Keyboard.KEY_1:
                        CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 0);
                        break;
                    case Keyboard.KEY_2:
                        CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 1);
                        break;
                    case Keyboard.KEY_3:
                        CTS_REQUEST_SWITCH_WEAPON.sendSwitchWeapon((byte) 2);
                        break;
                }
            }

        }
    }
}
