package de._13ducks.spacebatz.server.data.abilities;

import de._13ducks.spacebatz.server.data.Char;
import de._13ducks.spacebatz.server.data.Entity;
import de._13ducks.spacebatz.shared.Properties;

/**
 * Superklasse für alle Fähigkeiten.
 * Fähigkeiten sind alle Aktionen, wie z.B. Schießen, Bohren und Sprinten
 *
 * @author michael
 */
public abstract class Ability {

    /**
     * Die Grundeigenschaften der Fähigkeit.
     * z.B. Schaden und Reichweite bei einer Schießen-Fähigkeit.
     */
    private Properties baseProperties;
    /**
     * Die Eigenschaften der Fähigkeit *plus* die Boni des Trägers.
     * Muss neu berechnet werden, wenn refreshProperties aufgerufen wird.
     */
    private Properties actualProperties;
    /**
     * Der Charakter, der diese Fähigkeit "besitzt".
     * @TODO Abilitys sollten keinen Verweis auf ihren besitzer haben müssen, das ist kein Schönes Design
     */
    protected Char owner;

    /**
     * Erzeugt eine neue Fähigkeit.
     */
    public Ability() {
        baseProperties = new Properties();
        actualProperties = new Properties();
    }

    /**
     * Setzt den Besitzer dieser Fähigkeit.
     * Der Besitzer muss gesetzt werden, bevor die Fähigkeit eingesetzt werden kann!
     *
     * @param owner der Char, der die Fähigkeit besitzt
     */
    public void setOwner(Char owner) {
        this.owner = owner;
    }

    /**
     * Liest die Eigenschaften des BesitzerChars neu ein.
     * Muss immer aufgerufen werden, wenn sich die Eigenschaften des BesitzerChars ändern oder die Fähigkeit einem
     * (anderen) Char zugewiesen wird.
     *
     * @param properties die Werte des Besitzers der Fähigkeit
     */
    public abstract void refreshProperties(Properties properties);

    /**
     * Benutzt die Fähigkeit.
     */
    public abstract void use();

    /**
     * Benutzt die Fähigkeit auf eine Position
     *
     * @param targetX die X-Koordinate des Ziels
     * @param targetY die Y-Koordinate des Ziels
     */
    public abstract void useOnPosition(double targetX, double targetY);

    /**
     * Benutzt die Fähigkeit auf eine Entity
     *
     * @param target die Entity auf die die Fähigkeit benutzt werden soll
     */
    public abstract void useOnTarget(Entity target);

    /**
     * Benutzt die Fähigkeit in eine Richtung
     *
     * @param angle der Winkel, in den die Fähigkeit benutzt werden soll
     */
    public abstract void useInAngle(double angle);

    /**
     * Gibt true zurück, wenn alle Bedingungen erfüllt sind um diese Fähigkeit zu verwenden.
     * (z.B. Cooldown, Energiekosten, ...)
     *
     * @return true, wenn die Fähigkeit benutzt werden kann
     */
    public abstract boolean isReady();

    /**
     * Addiert Werte zu den Grundwerten der Fähigkeit.
     *
     * @param weaposStats die Werte, die addiert werden
     */
    public void addBaseProperties(Properties weaposStats) {
        baseProperties.addProperties(baseProperties);
    }

    /**
     * Subtrahiert Werte von den Grundwerten der Fähigkeit.
     *
     * @param weaposStats die Werte, die subtrahiert werden
     */
    public void removeBaseProperties(Properties weaposStats) {
        baseProperties.removeProperties(baseProperties);
    }

    /**
     * Gibt den Grundwert mit dem nagegebenen Namen zurück.
     *
     * @param name der Name des Gesuchten Grundwertes
     * @return der Wert des gesuchten Grundwertes
     */
    protected double getBaseProperty(String name) {
        return baseProperties.getProperty(name);
    }

    /**
     * Gibt den aktuellen (also Grundwert + Boni) Wert mit dem agegebenen Namen zurück.
     *
     * @param name der Name des Gesuchten Wertes
     * @return der Wert des gesuchten Wertes
     */
    protected double getProperty(String name) {
        return actualProperties.getProperty(name);
    }

    /**
     * Addiert den gegebenen Wert zum Grundwert dazu.
     *
     * @param name der Name des zu setzenden Grundwertes
     * @param value der Wert der addiert wird
     */
    protected void addBaseProperty(String name, double value) {
        double newValue = getBaseProperty(name) + value;
        baseProperties.setProperty(name, newValue);
    }

    /**
     * Setzt den aktuellen (also Grundwert + Boni) Wert mit dem agegebenen Namen.
     *
     * @param name der Name des zu setzenden Wertes
     * @return der Wert des zu setzenden Wertes
     */
    protected void setProperty(String name, double value) {
        actualProperties.setProperty(name, value);
    }
}
