package de._13ducks.spacebatz.client.network;

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
	    inputLine.deleteCharAt(inputLine.length() -1 );
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
			outln("usage: net_graph MODE (0 = Off, 1 = On)");
			break;
		    case "help":
			outln("available commands:");
			outln("-------------------");
			outln("net_graph");
			outln("help");
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
}
