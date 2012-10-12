package de._13ducks.spacebatz.server.data.skilltree;

import de._13ducks.spacebatz.server.data.skilltree.skills.MightyFireballSkill;

/**
 *
 * @author michael
 */
public class MarsroverSkilltree extends SkillTree {
    
    public MarsroverSkilltree() {
        super();
        addSkill(new MightyFireballSkill());
    }
}
