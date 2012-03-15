package de._13ducks.spacebatz.client.network;

import de._13ducks.spacebatz.client.Client;
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
	if (inputLine.length() > 2) {
	    inputLine.deleteCharAt(inputLine.length() - 1);
	}
    }

    /**
     * Enter, Befehl ausführen.
     */
    public void enter() {
	String input = inputLine.toString();
	resetInput();
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
				if (num == 0 || num == 1) {
				    NetStats.netGraph = num == 1;
				    break;
				}
			    } catch (NumberFormatException ex) {
			    }
			}
			outln("usage: net_graph MODE (0=off, 1=on)");
			break;
		    case "resync":
			Client.getMsgSender().sendRequestResync();
			outln("request for resyncing was sent");
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
			outln("help");
			outln("net_graph");
			outln("-------------------");
			break;
		    default:
			outln("unknown command. try help");
			break;
		}
	    }
	}
    }

    private void outln(String s) {
	outbuffer.add(s);
    }

    private void resetInput() {
	inputLine = new StringBuffer("> ");
    }

    public String getCurrentLine() {
	return inputLine.toString();
    }

    public String getHistory(int i) {
	if (outbuffer.size() > i) {
	    return outbuffer.get(outbuffer.size() - i - 1);
	}
	return "";
    }

    /**
     * Gibt die Zeile auf der Konsole aus.
     *
     * @param s die Zeile, die Ausgegeben werden soll.
     */
    public void info(String s) {
	outln(s);
    }
}
