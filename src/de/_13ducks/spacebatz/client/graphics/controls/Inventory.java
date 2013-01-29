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
    private Texture hud1;

    public Inventory(Renderer renderer) {
        itemTiles = renderer.getTextureByName("item.png");
        hud1 = renderer.getTextureByName("hud1.png");
        selecteditemslot = -1;
    }

    @Override
    public void render(Renderer renderer) {
        Camera camera = renderer.getCamera();
        TextWriter textWriter = renderer.getTextWriter();


        // dunkler Hintergrund
//        glColor4f(0.0f, 0.0f, 0.0f, 0.5f);
//        glRectf(0, 0, camera.getTilesX(), camera.getTilesY());

        // Koordinaten für waagrechte Linien
        float x1 = 0.095125f * camera.getTilesX();
        float x2 = 0.895125f * camera.getTilesX();
        float bottom1 = 0.07f * camera.getTilesY();

        // Koordinaten für senkrechte Linien
        float y1 = 0.17f * camera.getTilesY();
        float y2 = 0.47f * camera.getTilesY();
        float left1 = 0.095125f * camera.getTilesX();

        glDisable(GL_TEXTURE_2D);
        // Hintergrund  für Inventar-Items zeichnen
        glColor3f(0.243f, 0.243f, 0.243f);
        glRectf(x1, y1, x2, y2);

        glEnable(GL_TEXTURE_2D);
        hud1.bind();
        glColor3f(1.0f, 1.0f, 1.0f);

        // Bilder für Inventar-Slots
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 2; j++) {
                float xa = x1 + i * 0.08f * camera.getTilesX();
                float xb = x1 + (i + 1) * 0.08f * camera.getTilesX();
                float ya = y1 + j * 0.1f * camera.getTilesY();
                float yb = y1 + (j + 1) * 0.1f * camera.getTilesY();

                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(122.0f / 512, 374.0f / 512);
                glVertex3f(xa, ya, 0.0f);
                glTexCoord2f(274.0f / 512, 374.0f / 512);
                glVertex3f(xb, ya, 0.0f);
                glTexCoord2f(274.0f / 512, 268.0f / 512);
                glVertex3f(xb, yb, 0.0f);
                glTexCoord2f(122.0f / 512, 268.0f / 512);
                glVertex3f(xa, yb, 0.0f);
                glEnd(); // Zeichnen des QUADs fertig } }
            }
        }

        float ya = 0.53f * camera.getTilesY();
        float yb = ya + 0.1f * camera.getTilesY();

        // Hut-Slot:
        float xa = x1;
        float xb = x1 + 0.08f * camera.getTilesX();
        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
        glTexCoord2f(122.0f / 512, 374.0f / 512);
        glVertex3f(xa, ya, 0.0f);
        glTexCoord2f(274.0f / 512, 374.0f / 512);
        glVertex3f(xb, ya, 0.0f);
        glTexCoord2f(274.0f / 512, 268.0f / 512);
        glVertex3f(xb, yb, 0.0f);
        glTexCoord2f(122.0f / 512, 268.0f / 512);
        glVertex3f(xa, yb, 0.0f);
        glEnd(); // Zeichnen des QUADs fertig } }

        // Waffen-Slots:
        for (int i = 0; i <= 2; i++) {
            float xw1 = x1 + (0.1f * i + 0.15f) * camera.getTilesX();
            float xw2 = xw1 + 0.08f * camera.getTilesX();
            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(122.0f / 512, 374.0f / 512);
            glVertex3f(xw1, ya, 0.0f);
            glTexCoord2f(274.0f / 512, 374.0f / 512);
            glVertex3f(xw2, ya, 0.0f);
            glTexCoord2f(274.0f / 512, 268.0f / 512);
            glVertex3f(xw2, yb, 0.0f);
            glTexCoord2f(122.0f / 512, 268.0f / 512);
            glVertex3f(xw1, yb, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }


        // Items im Inventory zeichnen
        // Anzahl der Materialien:
        textWriter.renderText(String.valueOf(GameClient.getMaterial(0)), 0.6f * camera.getTilesX(), 0.64f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(1)), 0.6f * camera.getTilesX(), 0.59f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(2)), 0.6f * camera.getTilesX(), 0.54f * camera.getTilesY());

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
                y = 0.37f * camera.getTilesY();
            } else if (i < 20) {
                y = 0.27f * camera.getTilesY();
            } else {
                y = 0.17f * camera.getTilesY();
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

        // Hut:
        Item itemx = GameClient.getEquippedItems().getEquipslots()[2][0];
        if (itemx != null) {
            // Item zeichnen;
            float x = 0.11f * camera.getTilesX();
            float y = 0.53f * camera.getTilesY();

            float width = 0.1f / 16 * 9 * camera.getTilesX();
            float height = 0.1f * camera.getTilesY();

            float v = 0.0625f * (int) itemx.getPic();
            float w = 0.0625f * ((int) itemx.getPic() / 16);

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


        // Waffen:
        for (int j = 0; j < GameClient.getEquippedItems().getEquipslots()[1].length; j++) {
            Item item1 = GameClient.getEquippedItems().getEquipslots()[1][j];
            if (item1 != null) {
                // Item zeichnen;
                float x = (0.26f + 0.1f * j) * camera.getTilesX();
                float y = 0.53f * camera.getTilesY();

                float width = 0.1f / 16 * 9 * camera.getTilesX();
                float height = 0.1f * camera.getTilesY();

                float v = 0.0625f * (int) item1.getPic();
                float w = 0.0625f * ((int) item1.getPic() / 16);

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


        // selected Item zum Mauszeiger zeichnen
        if (selecteditemslot != -1) {
            itemTiles.bind();
            Item item2 = GameClient.getItems()[selecteditemslot];
            float x = (float) Mouse.getX() / CLIENT_GFX_RES_X * camera.getTilesX();
            float y = (float) Mouse.getY() / CLIENT_GFX_RES_Y * camera.getTilesY();

            float size = 0.08f;

            float v = 0.0625f * (int) item2.getPic();
            float w = 0.0625f * ((int) item2.getPic() / 16);

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

        if (y > 0.37 && y <= 0.47) {
            for (int i = 0; i < 10; i++) {
                if (x > left + i * width && x <= left + (i + 1) * width) {
                    slothovered = i;
                    break;
                }
            }
        } else if (y > 0.27 && y <= 0.37) {
            for (int i = 0; i < 10; i++) {
                if (x > left + i * width && x <= left + (i + 1) * width) {
                    slothovered = i + 10;
                    break;
                }
            }
        } else if (y > 0.17 && y <= 0.27) {
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
        } else if (x > 0.095125f && x < 0.175125f) {
            if (y > 0.53 && y < 0.63) {
                // Hutslot
                item = GameClient.getEquippedItems().getEquipslots()[2][0];
            }
        } else if (y > 0.53 && y < 0.63) {
            // ein Waffenslot?
            if (x > 0.245125f && x < 0.325125f) {
                item = GameClient.getEquippedItems().getEquipslots()[1][0];
            } else if (x > 0.345125f && x < 0.425125f) {
                item = GameClient.getEquippedItems().getEquipslots()[1][1];
            } else if (x > 0.445125f && x < 0.525125f) {
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


                    // Hut-Slot
                    if (x > 0.095125f && x < 0.175125f) {
                        if (y > 0.53 && y < 0.63) {
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

                    if (y > 0.53 && y < 0.63) {
                        byte weaponslot = -1;
                        if (x > 0.245125f && x < 0.325125f) {
                            weaponslot = 0;
                        } else if (x > 0.345125f && x < 0.425125f) {
                            weaponslot = 1;
                        } else if (x > 0.445125f && x < 0.525125f) {
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

                    if (y > 0.37 && y <= 0.47) {
                        for (int i = 0; i < 10; i++) {
                            if (x > left + i * width && x <= left + (i + 1) * width) {
                                slotklicked = i;
                                break;
                            }
                        }
                    } else if (y > 0.27 && y <= 0.37) {
                        for (int i = 0; i < 10; i++) {
                            if (x > left + i * width && x <= left + (i + 1) * width) {
                                slotklicked = i + 10;
                                break;
                            }
                        }
                    } else if (y > 0.17 && y <= 0.27) {
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
