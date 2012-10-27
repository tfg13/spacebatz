package de._13ducks.spacebatz.server.data.skilltree;

import de._13ducks.spacebatz.server.data.Client;
import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.shared.network.messages.STC.STC_UPDATE_SKILLTREE;
import java.util.HashMap;

/**
 * Ein klassischer RPG-Skilltree.
 *
 * @author michael
 */
public class SkillTree {

    /**
     * Die verteilbaren Skillpunkte.
     */
    private int availablePoints;
    /**
     * Liste der Skills.
     */
    private HashMap<String, SkillTreeEntry> skills;

    public SkillTree() {
        availablePoints = 10;
        skills = new HashMap<>();
    }

    public void investPoint(String skillName) {
        SkillTreeEntry skill = skills.get(skillName);
        if (skill != null) {
            if (skills.get(skillName).canInvestPoint(this) && availablePoints > 0) {
                skill.investPoint();
                availablePoints--;

            }
        } else {
            throw new IllegalArgumentException("Skill " + skillName + " ist nicht vorhanden!");
        }
    }

    public void sendSkillTreeUpdates(Client client) {
        for (SkillTreeEntry entry : skills.values()) {
            STC_UPDATE_SKILLTREE.sendUpdateSkillTree(client, entry.getName(), entry.getLevel());
        }

    }

    /**
     * Gibt an ob ein Skill verfügbar ist, d.h. er existiert und es wurde mindestens ein Punkt hineininvestiert.
     *
     * @param name
     * @return
     */
    public boolean isSkillAvailable(String name) {
        return skills.containsKey(name) && skills.get(name).getLevel() > 0;
    }

    protected void addSkill(SkillTreeEntry skill) {
        if (skills.containsKey(skill.getName())) {
            throw new IllegalArgumentException("Skill mit diesem Namen existiert bereits!");
        } else {
            skills.put(skill.getName(), skill);
        }

    }

    /**
     * Gibt die Fähigkeit, die ein Skill gibt, zurück.
     *
     * @param name
     * @return
     */
    public Ability getSkillAbility(String name) {
        if (isSkillAvailable(name)) {
            return skills.get(name).getAbility();
        } else {
            throw new IllegalArgumentException("Skill " + name + " nicht verfügbar!");
        }
    }
}
