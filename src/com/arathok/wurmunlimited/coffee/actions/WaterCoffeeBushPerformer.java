package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.arathok.wurmunlimited.coffee.Config;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.sounds.SoundPlayer;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class WaterCoffeeBushPerformer implements ActionPerformer {


    public final ActionEntry actionEntry;

    public WaterCoffeeBushPerformer() {


        actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Water Coffee Plant", "watering", new int[]{
                6 /* ACTION_TYPE_NOMOVE */,
                48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                36 /* DONT CARE SOURECE AND TARET */,

        }).range(4).build();

        ModActions.registerAction(actionEntry);
    }


    @Override
    public short getActionId() {
        return actionEntry.getNumber();
    }



    public static boolean canUse(Creature performer, Item target) {

        return performer.isPlayer() && target.getTemplateId() == CoffeeItem.coffeeShrubId;
    }


    @Override
    public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) { // Since we use target and source this time, only need that override
		/*if (target.getTemplateId() != AlchItems.weaponOilDemiseAnimalId)

			return propagate(action,
					ActionPropagation.SERVER_PROPAGATION,
					ActionPropagation.ACTION_PERFORMER_PROPAGATION);*/
        if (!canUse(performer, target)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


// EFFECT STUFF GOES HERE
        if (counter == 1.0F) {
            if (target.getExtra1()==1)
            {
                performer.getCommunicator().sendNormalServerMessage("The plant does not need water now, come back later!");
                return propagate(action,
                        ActionPropagation.FINISH_ACTION,
                        ActionPropagation.NO_SERVER_PROPAGATION,
                        ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }

            performer.getCommunicator().sendNormalServerMessage("You start watering the coffee shrub");
            SoundPlayer.playSound("sound.liquid.fillcontainer", performer, 1.6F);
            action.setTimeLeft(30);
            performer.sendActionControl( action.getActionString(), true, 30);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }

        if (action.currentSecond() >= 3) {

            if (performer.hasLink()) {


                long nextTendAt=System.currentTimeMillis()+ Config.tendingDuration;


                target.setData2(target.getData2()+1); // how many times watered +1
                target.setExtra1(1);                   // watered
                source.setWeight(source.getWeightGrams()-1000,true);
                PlantCoffeeBushPerformer.activeCoffeeShrubs.put(target.getWurmId(),nextTendAt);
                performer.getCommunicator().sendNormalServerMessage("The plant seems to be happy.");
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


}


