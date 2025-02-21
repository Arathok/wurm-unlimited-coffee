package com.arathok.wurmunlimited.coffee.hooks;

import com.arathok.wurmunlimited.coffee.Coffee;
import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.arathok.wurmunlimited.coffee.Config;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import javassist.*;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;

public class CoffeeForageHook {

    public static void insertForage() {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass methods;

        {
            try {
                // protected void alterSkill(double advanceMultiplicator, boolean decay, float times, boolean useNewSystem, double skillDivider) {
                Coffee.logger.log(Level.INFO, "new CoffeeForagePerformer");
                CtClass ctForage = classPool.get("com.wurmonline.server.behaviours.Forage");
                CtMethod ctCheckForCoffee = ctForage.getDeclaredMethod("getRandomForage");
                ctCheckForCoffee.insertAfter("if($_!=null)com.arathok.wurmunlimited.coffee.hooks.CoffeeForageHook.checkForCoffee($1.getWurmId());");


            } catch (NotFoundException e) {
                Coffee.logger.log(Level.WARNING, "Class  Methods not found", e);
                e.printStackTrace();

            } catch (CannotCompileException e) {
                Coffee.logger.log(Level.WARNING, "Code for hook is incorrect", e);
                e.printStackTrace();

            }
        }
    }
    public static void checkForCoffee(long performerId) throws NoSuchTemplateException, FailedException {
        int random = Server.rand.nextInt(Config.chanceToFindCoffeeOneinX);
        if (Config.isForageable) {
            if (random == 0) {
                Player player = Players.getInstance().getPlayerOrNull(performerId);
                if (player != null) {
                    player.getCommunicator().sendSafeServerMessage("You also find a coffee bean!");
                    Skill foraging = player.getSkills().getSkillOrLearn(SkillList.FORAGING);
                    float quality = Math.abs(Skill.rollGaussian((float) foraging.getKnowledge(), 50F, player.getWurmId(), "quality"));
                    Item coffBean = ItemFactory.createItem(CoffeeItem.coffeeBeanId, quality, player.getName());
                    player.getInventory().insertItem(coffBean);

                }
            }
        }
    }
}



