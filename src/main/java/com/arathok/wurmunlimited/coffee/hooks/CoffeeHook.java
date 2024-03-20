package com.arathok.wurmunlimited.coffee.hooks;

import com.arathok.wurmunlimited.coffee.Coffee;
import com.wurmonline.server.Players;
import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import javassist.*;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;

public class CoffeeHook {
    //alterSkill
    //static final boolean drainCoffers(Creature performer, Village village, float counter, Item token, Action act) {
    Methods test;
    public static void insert() {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass methods;

        {
            try {
                // protected void alterSkill(double advanceMultiplicator, boolean decay, float times, boolean useNewSystem, double skillDivider) {
                Coffee.logger.log(Level.INFO, "new CoffeeSKillgain");
                CtClass ctSkill = classPool.get("com.wurmonline.server.skills.Skill");
                CtMethod ctAlterSkill = ctSkill.getMethod("alterSkill","DZFZD()V");
                ctAlterSkill.insertBefore("advanceMultiplicator*=com.arathok.wurmunlimited.coffee.hooks.CoffeeHook.checkForCoffee(this.parentId);");


            } catch (NotFoundException e) {
                Coffee.logger.log(Level.WARNING, "Class  Methods not found", e);
                e.printStackTrace();

            } catch (CannotCompileException e) {
                Coffee.logger.log(Level.WARNING, "Code for hook is incorrect", e);
                e.printStackTrace();

            }
        }
    }
    public static double checkForCoffee(long performerId) {
        double advanceMultiplicatorNew =1;
        Player playerToTest = Players.getInstance().getPlayerOrNull(performerId);
        if (playerToTest!=null&&!(playerToTest.getSaveFile().frozenSleep)&&playerToTest.getSpellEffects().getSpellEffect((byte) 101)!=null)
        {
            advanceMultiplicatorNew=1*(1+(playerToTest.getSpellEffects().getSpellEffect((byte)101).power)/10);
        }
        return advanceMultiplicatorNew;
    }
}
