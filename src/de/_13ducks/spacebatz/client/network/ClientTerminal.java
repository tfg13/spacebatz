/*
 * Copyright 2011, 2012:
 *  Tobias Fleig (tobifleig[AT]googlemail[DOT]com)
 *  Michael Haas (mekhar[AT]gmx[DOT]de)
 *  Johannes Kattinger (johanneskattinger[AT]gmx[DOT]de
 *
 * - All rights reserved -
 *
 * 13ducks PROPRIETARY/CONFIDENTIAL - do not distribute
 */
package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.GameClient;
import de._13ducks.spacebatz.server.network.CTS_REQUEST_RCON;
import de._13ducks.spacebatz.shared.network.MessageIDs;
import de._13ducks.spacebatz.shared.network.OutgoingCommand;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_INVEST_SKILLPOINT;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_MAP_ABILITY;
import de._13ducks.spacebatz.shared.network.messages.CTS.CTS_REQUEST_USE_ABILITY;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Das Client-Terminal
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class ClientTerminal {

    /**
     * Die eingegangen Zeilen, die angezeigt werden.
     */
    private ArrayList<String> outbuffer;
    /**
     * Die aktuelle Eingabezeile.
     */
    private StringBuffer inputLine;
    /**
     * Um wie viele Zeilen nach oben gescrollt wurde.
     */
    private int scroll = 0;
    /**
     * Das Socket für Rcon-Verbindungen.
     */
    private Socket rconSock;
    /**
     * Der Writer für Rcon.
     */
    private PrintStream rconOut;
    /**
     * Der Reader für Rcon.
     */
    private BufferedReader rconRead;
    /**
     * Ist rcon aktiv?.
     */
    private boolean rcon = false;

    /**
     * Neues ClientTerminal erstellen
     */
    public ClientTerminal() {
        outbuffer = new ArrayList<>();
        resetInput();
    }

    /**
     * Eingabe eines Zeichens.
     *
     * @param c Das eingegeben Zeichen
     */
    public void input(char c) {
        inputLine.append(c);
    }

    /**
     * Löscht ein Zeichen.
     */
    public void backspace() {
        if (inputLine.length() > (rcon ? 3 : 2)) {
            inputLine.deleteCharAt(inputLine.length() - 1);
        }
    }

    /**
     * Enter, Befehl ausführen.
     */
    public void enter() {
        String input = inputLine.toString();
        resetInput();
        if (!rcon) {
            outln(input);
            // Die Promt absägen (das "> " am anfang);
            input = input.substring(2);
            if (!input.isEmpty()) {
                String[] words = input.split("\\s+");
                if (words.length >= 1) {
                    String cmd = words[0].toLowerCase();
                    switch (cmd) {
                        case "net_graph":
                            if (words.length == 2) {
                                try {
                                    int num = Integer.parseInt(words[1]);
                                    if (num >= 0 || num <= 2) {
                                        NetStats.netGraph = num;
                                        break;
                                    }
                                } catch (NumberFormatException ex) {
                                }
                            }
                            outln("usage: net_graph MODE (0=off, 1=on)");
                            break;
                        case "clear":
                            outbuffer.clear();
                            break;
                        case "rcon":
                            if (rconOut != null) {
                                // Rcon ist schon an, wieder aktivieren
                                rcon = true;
                                resetInput();
                            } else {
                                // Anfragen
                                CTS_REQUEST_RCON.sendRconRequest();
                                outln("rcon: request sent");
                            }
                            break;
                        case "zoom":
                            if (words.length == 2) {
                                try {
                                    int fact = Integer.parseInt(words[1]);
                                    if (fact > 0) {
                                        GameClient.getEngine().getGraphics().setZoomFact(fact);
                                        break;
                                    }
                                } catch (NumberFormatException ex) {
                                }
                            }
                            outln("usage: zoom (>0)");
                            break;
                        case "mobmaster":
                            try {
                                int count = Integer.parseInt(words[1]);
                                for (int i = 0; i < count; i++) {
                                    GameClient.getEngine().getGraphics().sendAbilityRequest();
                                }
                            } catch (Exception ex) {
                                outln("usage: mobmaster [count] (Will spawn [count] mobs at the cursor)");
                            }
                            break;
                        case "sendsevens":
                            try {
                                int length = Integer.parseInt(words[1]);
                                byte msg[] = new byte[length * 100];

                                for (int i = 0; i < length * 100; i++) {
                                    msg[i] = (byte) 7;
                                }
                                msg[0] = (byte) length;
                                GameClient.getNetwork2().queueOutgoingCommand(new OutgoingCommand(MessageIDs.NET_CTS_DEBUG, msg));
                            } catch (Exception ex) {
                                outln("usage: sendsevens x (100*x sevens will be sent)");
                            }
                            break;
                        case "mapskill":
                            try {
                                byte slot = Byte.parseByte(words[1]);
                                String ability = words[2];
                                CTS_REQUEST_MAP_ABILITY.sendMapAbility(slot, ability);

                            } catch (Exception ex) {
                                outln("usage: mapskill slot ability (slot is 0,1,2,... and ability is the abilityname.)");
                            }
                            break;
                        case "skill":
                            try {
                                String ability = words[1];
                                CTS_INVEST_SKILLPOINT.sendInvestSkillPoint(ability);
                            } catch (Exception ex) {
                                outln("usage: skill skillname (skillname is the name of the skill to skill to kill)");
                            }
                            break;
                        case "cast":
                            try {
                                Byte ability = Byte.parseByte(words[1]);
                                CTS_REQUEST_USE_ABILITY.sendAbilityUseRequest(ability, 0, 1);
                            } catch (Exception ex) {
                                outln("usage: cast skill (skillname is the skill ocnstant, e.g. 0 for shiftskill)");
                            }
                            break;
                        case "lerp":
                            try {
                                int lerp = Integer.parseInt(words[1]);
                                GameClient.getNetwork2().setLerp(lerp);
                            } catch (NumberFormatException ex) {
                                outln("usage: lerp [int] (sets client-lerp (in serverticks) to given value)");
                            }
                            break;
                        case "about":
                            outln("spacebatz aurora");
                            outln("13ducks PROPRIETARY/CONFIDENTIAL");
                            outln("internal testing only. DO NOT DISTRIBUTE");
                            outln("");
                            outln("Copyright 2012 13ducks");
                            outln("All rights reserved.");
                            break;
                        case "help":
                            outln("available commands:");
                            outln("-------------------");
                            outln("about");
                            outln("clear");
                            outln("help");
                            outln("lerp");
                            outln("net_graph");
                            outln("rcon");
                            outln("sendsevens");
                            outln("zoom");
                            outln("-------------------");
                            break;
                        default:
                            outln("unknown command. try help");
                            break;
                    }
                }
            }
        } else {
            input = input.substring(3);
            switch (input) {
                case "exit":
                    try {
                        // Rcon ganz abschalten
                        rconOut.close();
                        rconRead.close();
                        rconSock.close();
                        rcon = false;
                        resetInput();
                        outln("rcon: disconnected");
                    } catch (IOException ex) {
                    }
                    return;
                case "bg":
                    // Rcon verstecken
                    rcon = false;
                    resetInput();
                    return;
            }
            outln(">r " + input);
            rconOut.println(input);
        }
    }

    private void outln(String s) {
        outbuffer.add(s);
        if (scroll != 0) {
            scroll++; // Autoscroll nur, wenn wir ganz unten sind
        }
    }

    private void resetInput() {
        inputLine = new StringBuffer(rcon ? "r> " : "> ");
    }

    public String getCurrentLine() {
        return inputLine.toString();
    }

    public String getHistory(int i) {
        if (outbuffer.size() > i + scroll) {
            return outbuffer.get(outbuffer.size() - i - 1 - scroll);
        }
        return "";
    }

    public void scrollForward() {
        if (scroll > 0) {
            scroll--;
        }
    }

    /**
     * Scrollt weiter nach hinten
     */
    public void scrollBack() {
        if (scroll < outbuffer.size() - 1) {
            scroll++;
        }
    }

    /**
     * Gibt die Zeile auf der Konsole aus.
     *
     * @param s die Zeile, die Ausgegeben werden soll.
     */
    public void info(String s) {
        outln(s);
    }

    /**
     * Gibt einen String mit rcon-Markierung aus.
     *
     * @param s
     */
    private void rcOut(String s) {
        outln(" r " + s);
    }

    /**
     * Baut eine Rcon-Verbindung auf.
     *
     * @param port
     */
    void rcon(int port) {
        try {
            rconSock = new Socket(GameClient.getNetwork2().getServerAdr(), port);
            rconOut = new PrintStream(rconSock.getOutputStream());
            rconRead = new BufferedReader(new InputStreamReader(rconSock.getInputStream()));
            Thread rconReader = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            rcOut(rconRead.readLine());
                        }
                    } catch (Exception ex) {
                    }
                }
            });
            rconReader.setName("CLIENT_RCON_READER");
            rconReader.setDaemon(true);
            rconReader.start();
            rcon = true;
            resetInput();
            outln("rcon: connection established");
        } catch (Exception ex) {
            ex.printStackTrace();
            outln("rcon: connecting failed");
        }
    }
}
