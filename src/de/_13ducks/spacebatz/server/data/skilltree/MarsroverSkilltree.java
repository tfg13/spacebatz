package de._13ducks.spacebatz.server.data.skilltree;

import de._13ducks.spacebatz.server.data.skilltree.skills.MassSummonEnemySkill;
import de._13ducks.spacebatz.server.data.skilltree.skills.SummonEnemySkill;

/**
 *
 * @author michael
 */
public class MarsroverSkilltree extends SkillTree {
    
    public MarsroverSkilltree() {
        super();
        addSkill(new SummonEnemySkill());
        addSkill(new MassSummonEnemySkill());
    }
}
