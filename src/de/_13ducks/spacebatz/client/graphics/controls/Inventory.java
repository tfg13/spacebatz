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
    private int selecteditemslot; // zuletzt angeklickter Inventarslot
    private Texture itemTiles;

    public Inventory(Renderer renderer) {
        itemTiles = renderer.getTextureByName("item.png");
        selecteditemslot = -1;
    }

    @Override
    public void render(Renderer renderer) {
        Camera camera = renderer.getCamera();
        TextWriter textWriter = renderer.getTextWriter();

        glDisable(GL_TEXTURE_2D);
        // dunkler Hintergrund
        glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
        glRectf(0, 0, camera.getTilesX(), camera.getTilesY());

        // waagrechte Linien
        float x1 = 0.095125f * camera.getTilesX();
        float x2 = 0.895125f * camera.getTilesX();
        float bottom1 = 0.07f * camera.getTilesY();

        // senkrechte Linien
        float y1 = 0.07f * camera.getTilesY();
        float y2 = 0.37f * camera.getTilesY();
        float left1 = 0.095125f * camera.getTilesX();

        // Hintergrund  für Inventar-Items zeichnen
        glColor3f(1.0f, 1.0f, 1.0f);
        glRectf(x1, y1, x2, y2);

        // Linien für Inventar-Items zeichnen
        glColor3f(0.0f, 0.0f, 0.0f);

        glBegin(GL_LINES);
        for (int i = 0; i < 4; i++) {
            glVertex2d(x1, bottom1 + 0.1f * i * camera.getTilesY());
            glVertex2d(x2, bottom1 + 0.1f * i * camera.getTilesY());
        }
        for (int i = 0; i < 11; i++) {
            glVertex2d(left1 + 0.08f * i * camera.getTilesX(), y1);
            glVertex2d(left1 + 0.08f * i * camera.getTilesX(), y2);
        }
        glEnd();

        // ausgewählten Waffenslot im Inventar markieren:
        float wx = 0.227f + 0.172f * GameClient.getPlayer().getSelectedattack();

        glColor3f(0.7f, 0.0f, 0.0f);
        glRectf(wx * camera.getTilesX(), 0.59f * camera.getTilesY(), (wx + 0.14f) * camera.getTilesX(), 0.6f * camera.getTilesY());

        glEnable(GL_TEXTURE_2D);

        // Items im Inventory zeichnen
        // Anzahl der Materialien:
        textWriter.renderText(String.valueOf(GameClient.getMaterial(0)), 0.12f * camera.getTilesX(), 0.44f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(1)), 0.45f * camera.getTilesX(), 0.44f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(2)), 0.75f * camera.getTilesX(), 0.44f * camera.getTilesY());

        itemTiles.bind();
        for (int i = 0; i < 30; i++) {

            if (GameClient.getItems()[i] == null || i == selecteditemslot) {
                // Slot leer oder gerade selected -> nicht zeichnen
                continue;
            }

            Item item = GameClient.getItems()[i];

            float x = (0.107f + 0.08f * (i % 10)) * camera.getTilesX();

            float y;
            if (i < 10) {
                y = 0.27f * camera.getTilesY();
            } else if (i < 20) {
                y = 0.17f * camera.getTilesY();
            } else {
                y = 0.07f * camera.getTilesY();
            }

            float width = (0.1f / 16.0f * 9.0f) * camera.getTilesX();
            float height = 0.1f * camera.getTilesY();

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

        glColor3f(1.0f, 1.0f, 1.0f);

        // angelegte Items in ihre Slots im Inventar zeichnen
        itemTiles.bind();
        for (int i = 1; i < GameClient.getEquippedItems().getEquipslots().length; i++) {
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

                    float width = 0.1f / 16 * 9 * camera.getTilesX();
                    float height = 0.1f * camera.getTilesY();

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
        double width = 0.08;
        double left = 0.107;

        if (y > 0.27 && y <= 0.37) {
            for (int i = 0; i < 10; i++) {
                if (x > left + i * width && x <= left + (i + 1) * width) {
                    slothovered = i;
                    break;
                }
            }
        } else if (y > 0.17 && y <= 0.27) {
            for (int i = 0; i < 10; i++) {
                if (x > left + i * width && x <= left + (i + 1) * width) {
                    slothovered = i + 10;
                    break;
                }
            }
        } else if (y > 0.07 && y <= 0.17) {
            for (int i = 00; i < 10; i++) {
                if (x > left + i * width && x <= left + (i + 1) * width) {
                    slothovered = i + 20;
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
                    double width = 0.08;
                    double left = 0.107;

                    if (y > 0.27 && y <= 0.37) {
                        for (int i = 0; i < 10; i++) {
                            if (x > left + i * width && x <= left + (i + 1) * width) {
                                slotklicked = i;
                                break;
                            }
                        }
                    } else if (y > 0.17 && y <= 0.27) {
                        for (int i = 0; i < 10; i++) {
                            if (x > left + i * width && x <= left + (i + 1) * width) {
                                slotklicked = i + 10;
                                break;
                            }
                        }
                    } else if (y > 0.07 && y <= 0.17) {
                        for (int i = 0; i < 10; i++) {
                            if (x > left + i * width && x <= left + (i + 1) * width) {
                                slotklicked = i + 20;
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
