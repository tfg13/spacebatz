package de._13ducks.spacebatz.server.data.skilltree.skills;

import de._13ducks.spacebatz.server.data.abilities.Ability;
import de._13ducks.spacebatz.server.data.abilities.MassSummonEnemyAbility;
import de._13ducks.spacebatz.server.data.skilltree.SkillTree;
import de._13ducks.spacebatz.server.data.skilltree.SkillTreeEntry;

/**
 * Testskill. Von Summon abhängig.
 *
 * @author michael
 */
public class MassSummonEnemySkill extends SkillTreeEntry {

    private MassSummonEnemyAbility ability;

    public MassSummonEnemySkill() {
        super("masssummon");
        ability = new MassSummonEnemyAbility();
        setAbility(ability);
    }

    @Override
    public boolean canInvestPoint(SkillTree tree) {
        // masssummon geht erst wenn man summon hat:
        return tree.isSkillAvailable("summon");
    }

    @Override
    public void onInvestPoint() {
        // ability.increaseDamage(100);
    }

    @Override
    public Ability getAbility() {
        return ability;
    }
}
