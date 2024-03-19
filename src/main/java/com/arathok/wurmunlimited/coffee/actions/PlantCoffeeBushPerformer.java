package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.Coffee;
import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.sounds.SoundPlayer;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.logging.Level;

public class PlantCoffeeBushPerformer implements ActionPerformer {


    public final ActionEntry actionEntry;
    public static HashMap<Long, Long> activeCoffeeShrubs = new HashMap<>();

    public PlantCoffeeBushPerformer() {


        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Plant Coffee Plant", "sowing", new int[]{
                6 /* ACTION_TYPE_NOMOVE */,
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                36 /*  CARE SOURECE AND TARET */,

        }).range(4).build();

        ModActions.registerAction(actionEntry);
    }


    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }


    public static boolean canUse(Creature performer, Item source, Item target) {

        return performer.isPlayer() && source.getOwnerId() == performer.getWurmId() && !source.isTraded() && source.getTemplateId() == CoffeeItem.coffeeBeanId && target.getTemplateId() == CoffeeItem.coffeeShrubId;
    }


    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) { // Since we use target and source this time, only need that override
		/*if (target.getTemplateId() != AlchItems.weaponOilDemiseAnimalId)

			return propagate(action,
					ActionPropagation.SERVER_PROPAGATION,
					ActionPropagation.ACTION_PERFORMER_PROPAGATION);*/
        if (!canUse(performer, source, target)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


// EFFECT STUFF GOES HERE
        if (counter == 1.0F) {
            performer.getCommunicator().sendNormalServerMessage("You start planting the coffee shrub");
            SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
            action.setTimeLeft(30);
            performer.getCommunicator().sendActionControl(performer.getWurmId(), action.getActionString(), true, 30);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        if (action.currentSecond() >= 3) {

            if (performer.hasLink()) {
                long nextTendTime = System.currentTimeMillis()+86400000L;
                target.setData1(1); // Set Age to 1
                target.setData2(0); // number of times watered
                target.setAuxData((byte) 1); // has sprout
                target.setExtra(nextTendTime);
                activeCoffeeShrubs.put(target.getWurmId(), nextTendTime);
                SoundPlayer.playSound("sound.forest.branchsnap", performer, 1.6F);
            }


            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


        return propagate(action,
                ActionPropagation.CONTINUE_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
    }

    private void addToDB(Item target) {
    }

    private void readFromDB(Item target) {
    }

    private void deleteFromDB(Item target) {
    }

}


