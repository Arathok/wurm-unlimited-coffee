package com.arathok.wurmunlimited.coffee;

import com.wurmonline.server.behaviours.Methods;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.villages.Village;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import javassist.*;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;

import java.util.logging.Level;

public class CaveCrystalHook {

    //static final boolean drainCoffers(Creature performer, Village village, float counter, Item token, Action act) {
    Methods test;
    public static void insert() {
        ClassPool classPool = HookManager.getInstance().getClassPool();
        CtClass methods;

        {
            try {
                methods = classPool.getCtClass("com.wurmonline.server.behaviours.Methods");
                CtMethod drainCoffers = methods.getMethod("drainCoffers",
                        "(Lcom/wurmonline/server/creatures/Creature;" +
                                "Lcom/wurmonline/server/villages/Village;" +
                                "F" +
                                "Lcom/wurmonline/server/items/Item;" +
                                "Lcom/wurmonline/server/behaviours/Action;)" +
                                "Z");

                drainCoffers.insertBefore(
                        "if (com.arathok.wurmunlimited.mods.caveCrystal.CaveCrystalHook.checkForCrystal($1,$4,$2))" +
                                "return true;");
            } catch (NotFoundException e) {
                CaveCrystal.logger.log(Level.WARNING, "Class  Methods not found", e);
                e.printStackTrace();

            } catch (CannotCompileException e) {
                CaveCrystal.logger.log(Level.WARNING, "Code for hook is incorrect", e);
                e.printStackTrace();

            }
        }
    }
    public static boolean checkForCrystal(Creature performer, Item token, Village village)
    {

        int radius= village.getDiameterX();
        VolaTile[] tiles = Zones.getTilesSurrounding(token.getTileX(),token.getTileY(),false,radius);
        boolean crystalFound = false;
        for (VolaTile tile : tiles)
        {
            for(Item item: tile.getItems())
            {
                if (item.getTemplateId()== CoffeeItem.caveCrystalId)
                {
                    crystalFound=true;
                    performer.getCommunicator().sendAlertServerMessage("Something stops you from draining that deed, you seem to hear a faint hum from somewhere below!");
                    break;
                }

            }
            if (crystalFound)
                break;
        }
        return crystalFound;
    }
}
