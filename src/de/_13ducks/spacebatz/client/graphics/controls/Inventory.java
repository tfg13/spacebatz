package de._13ducks.spacebatz.client.graphics.controls;

import static de._13ducks.spacebatz.shared.DefaultSettings.*;
import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.client.graphics.Camera;
import de._13ducks.spacebatz.client.graphics.Control;
import de._13ducks.spacebatz.client.graphics.Renderer;
import de._13ducks.spacebatz.client.graphics.TextWriter;
import de._13ducks.spacebatz.shared.Item;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_DELETE_ITEM;
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

        // Bild für die Inventar-Slots
        for (int i = 0; i <= 9; i++) {
            for (int j = 0; j <= 2; j++) {
                float xa = x1 + i * 0.08f * camera.getTilesX();
                float xb = x1 + (i + 1) * 0.08f * camera.getTilesX();
                float ya = y1 + j * 0.1f * camera.getTilesY();
                float yb = y1 + (j + 1) * 0.1f * camera.getTilesY();

                glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
                glTexCoord2f(273.0f / 512, 307.0f / 512);
                glVertex3f(xa, ya, 0.0f);
                glTexCoord2f(419.0f / 512, 307.0f / 512);
                glVertex3f(xb, ya, 0.0f);
                glTexCoord2f(419.0f / 512, 206.0f / 512);
                glVertex3f(xb, yb, 0.0f);
                glTexCoord2f(273.0f / 512, 206.0f / 512);
                glVertex3f(xa, yb, 0.0f);
                glEnd(); // Zeichnen des QUADs fertig } }
            }
        }

        float x01 = 0.815125f * camera.getTilesX();
        float x02 = 0.655125f * camera.getTilesX();
        float x03 = 0.335125f * camera.getTilesX();
        float ya = 0.48f * camera.getTilesY();
        float yb = ya + 0.1f * camera.getTilesY();

        // Bild für Mülleimer
        glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
        glTexCoord2f(125.0f / 512, 101.0f / 512);
        glVertex3f(x01, ya, 0.0f);
        glTexCoord2f(271.0f / 512, 101.0f / 512);
        glVertex3f(x01 + 0.08f * camera.getTilesX(), ya, 0.0f);
        glTexCoord2f(271.0f / 512, 0.0f / 512);
        glVertex3f(x01 + 0.08f * camera.getTilesX(), yb, 0.0f);
        glTexCoord2f(125.0f / 512, 0.0f / 512);
        glVertex3f(x01, yb, 0.0f);
        glEnd(); // Zeichnen des QUADs fertig } }

        // Armor-Slots:
        for (int i = 0; i <= 4; i++) {
            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(125.0f / 512, 307.0f / 512);
            glVertex3f(x03, ya + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(271.0f / 512, 307.0f / 512);
            glVertex3f(x03 + 0.08f * camera.getTilesX(), ya + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(271.0f / 512, 206.0f / 512);
            glVertex3f(x03 + 0.08f * camera.getTilesX(), yb + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(125.0f / 512, 206.0f / 512);
            glVertex3f(x03, yb + 0.1f * camera.getTilesY() * i, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }

        // Waffen-Slots:
        for (int i = 0; i <= 2; i++) {
            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(125.0f / 512, 204.0f / 512);
            glVertex3f(x02, ya + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(271.0f / 512, 204.0f / 512);
            glVertex3f(x02 + 0.08f * camera.getTilesX(), ya + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(271.0f / 512, 103.0f / 512);
            glVertex3f(x02 + 0.08f * camera.getTilesX(), yb + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(125.0f / 512, 103.0f / 512);
            glVertex3f(x02, yb + 0.1f * camera.getTilesY() * i, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }

        // Werkzeug-Slots:
        for (int i = 3; i <= 4; i++) {
            glBegin(GL_QUADS); // QUAD-Zeichenmodus aktivieren
            glTexCoord2f(125.0f / 512, 204.0f / 512);
            glVertex3f(x02, ya + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(271.0f / 512, 204.0f / 512);
            glVertex3f(x02 + 0.08f * camera.getTilesX(), ya + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(271.0f / 512, 103.0f / 512);
            glVertex3f(x02 + 0.08f * camera.getTilesX(), yb + 0.1f * camera.getTilesY() * i, 0.0f);
            glTexCoord2f(125.0f / 512, 103.0f / 512);
            glVertex3f(x02, yb + 0.1f * camera.getTilesY() * i, 0.0f);
            glEnd(); // Zeichnen des QUADs fertig } }
        }


        // Items im Inventory zeichnen
        // Anzahl der Materialien:
        textWriter.renderText(String.valueOf(GameClient.getMaterial(0)), 0.6f * camera.getTilesX(), 0.64f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(1)), 0.6f * camera.getTilesX(), 0.59f * camera.getTilesY());
        textWriter.renderText(String.valueOf(GameClient.getMaterial(2)), 0.6f * camera.getTilesX(), 0.54f * camera.getTilesY());
        glColor4f(1f, 1f, 1f, 1f);

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
            Item item2 = GameClient.getItems()[selecteditemslot];
            if (item2 != null) {
                itemTiles.bind();

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
        glColor4f(1f, 1f, 1f, 1f);
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

                    float x01 = 0.815125f;
                    float x02 = 0.655125f;
                    float x03 = 0.335125f;
                    float ya = 0.48f;

                    // Hut-Slot
                    if (x > x03 && x < x03 + 0.08f) {
                        for (int i = 1; i <= 5; i++) {
                            if (y > ya + (i - 1) * 0.1f && y < ya + i * 0.1f) {
                                if (selecteditemslot != -1) {
                                    Item selecteditem = GameClient.getItems()[selecteditemslot];
                                    if (i != 1 && (int) selecteditem.getItemClass() == i) {
                                        CTS_EQUIP_ITEM.sendEquipItem(selecteditemslot, (i == 1) ? (byte) 1 : (byte) 0);
                                    } else if (i == 1 && (int) selecteditem.getItemClass() == 2) {
                                        CTS_EQUIP_ITEM.sendEquipItem(selecteditemslot, (byte) 0);
                                    }
                                    selecteditemslot = -1;
                                } else {
                                    if (i > 2) {
                                        if (GameClient.getEquippedItems().getEquipslots()[i][0] != null) {
                                            CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(i, (byte) 0);
                                        }
                                    } else if (i == 2) {
                                        if (GameClient.getEquippedItems().getEquipslots()[2][1] != null) {
                                            CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(2, (byte) 1);
                                        }
                                    } else if (i == 1) {
                                        if (GameClient.getEquippedItems().getEquipslots()[2][0] != null) {
                                            CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(2, (byte) 0);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Waffenslot
                    if (x > x02 && x < x02 + 0.08f) {
                        byte weaponslot = -1;
                        if (y > ya && y < ya + 0.1f) {
                            weaponslot = 0;
                        } else if (y > ya + 0.1f && y < ya + 0.2f) {
                            weaponslot = 1;
                        } else if (y > ya + 0.2f && y < ya + 0.3f) {
                            weaponslot = 2;
                        }
                        System.out.println("Weaponslot " + weaponslot);
                        if (weaponslot != -1) {
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

                    // Werkzeugslot
                    if (x > x02 && x < x02 + 0.08f) {
                        byte toolslot = -1;
                        if (y > ya + 0.3f && y < ya + 0.4f) {
                            toolslot = 6;
                        } else if (y > ya + 0.4f && y < ya + 0.5f) {
                            toolslot = 7;
                        }
                        if (toolslot != -1) {
                            if (selecteditemslot != -1) {
                                Item selecteditem = GameClient.getItems()[selecteditemslot];
                                if ((int) selecteditem.getItemClass() == toolslot) {
                                    CTS_EQUIP_ITEM.sendEquipItem(selecteditemslot, (byte) 0); // Slotnummer, zum Auseinanderhalten von den Slots
                                    selecteditemslot = -1;
                                }
                            } else {
                                if (GameClient.getEquippedItems().getEquipslots()[toolslot][0] != null) {
                                    CTS_REQUEST_ITEM_DEQUIP.sendDequipItem(toolslot, (byte) 0);
                                }
                            }
                        }
                    }

                    //Müll-Slot
                    if (x > 0.815125f && x < 0.895125f) {
                        if (y > 0.53 && y < 0.63) {
                            CTS_DELETE_ITEM.sendDeleteItem(selecteditemslot);
                            selecteditemslot = -1;
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
