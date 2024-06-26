package com.wurmonline.server.spells;

import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class Caffeinated extends CreatureEnchantment {



    public Caffeinated(int aCastingTime, int aCost, int aDifficulty, int aLevel, long aCooldown)
    {

        super("Caffeinated - better focus while learning things", ModActions.getNextActionId(), aCastingTime, aCost, aDifficulty, aLevel, aCooldown);
        this.targetCreature=true;
        this.description = "Makes it possible to learn skills beyond your natural understanding";
        this.enchantment = 101; // 100-128 is open
        this.effectdesc = "Deeper understanding. You feel like you can learn anything!";
        this.type = 2;
    }

}