package de._13ducks.spacebatz.server.data.skilltree;

import de._13ducks.spacebatz.server.data.abilities.Ability;

/**
 * Superklasse für Skills im Skilltree.
 *
 * @author michael
 */
public abstract class SkillTreeEntry {

    private Ability ability;
    /**
     * Der Name des Skills.
     */
    private String name;
    /**
     * Das Level des Skills.
     */
    private byte level;

    public SkillTreeEntry(String name) {
        this.name = name;
        level = 0;
    }

    /**
     * Gibt das Level dieses Skills zurück.
     *
     * @return
     */
    public byte getLevel() {
        return level;
    }

    public String getName() {
        return name;
    }

    /**
     * Gibt an ob die Vorraussetzungen für diesen Skill erfüllt sind.
     *
     * @param tree
     * @return
     */
    public abstract boolean canInvestPoint(SkillTree tree);

    /**
     * Wird gerufen wenn der Skill verbessert wird.
     */
    public abstract void onInvestPoint();

    /**
     * Verbessert diesen Skill.
     */
    protected final void investPoint() {
        level++;
        onInvestPoint();
    }

    /**
     * Gibt die Fähigkeit dieses Skills zurück.
     *
     * @return
     */
    public Ability getAbility() {
        return ability;
    }

    /**
     * @param ability the ability to set
     */
    public void setAbility(Ability ability) {
        this.ability = ability;
    }
}
