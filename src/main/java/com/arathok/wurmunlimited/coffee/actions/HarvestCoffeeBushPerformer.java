package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.spells.CreatureEnchantment;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.spells.Spells;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

public class HarvestCoffeeBushPerformer implements ActionPerformer {


    public final ActionEntry actionEntry;

    public HarvestCoffeeBushPerformer(){



            actionEntry = new ActionEntryBuilder((short) ModActions.getNextActionId(), "Harvest Coffee", "harvesting", new int[]{
                    6 /* ACTION_TYPE_NOMOVE */,
                    48 /* ACTION_TYPE_ENEMY_ALWAYS */,
                    37 /* DONT CARE SOURECE AND TARET */,

            }).range(4).build();

            ModActions.registerAction(actionEntry);
        }





        @Override
        public short getActionId() {
            return actionEntry.getNumber();
        }
        public boolean action(Action action, Creature performer, Item source, Item target, short num, float counter) {
            return action(action, performer, target, num, counter);
        }
        public static boolean canUse(Creature performer, Item source) {

            return performer.isPlayer() && source.getOwnerId() == performer.getWurmId() && !source.isTraded()&& source.getTemplateId()== CoffeeItem.coffeeShrubId;
        }


        @Override
        public boolean action(Action action, Creature performer, Item source, short num, float counter) { // Since we use target and source this time, only need that override
		/*if (target.getTemplateId() != AlchItems.weaponOilDemiseAnimalId)

			return propagate(action,
					ActionPropagation.SERVER_PROPAGATION,
					ActionPropagation.ACTION_PERFORMER_PROPAGATION);*/
            if (!canUse(performer,source)) {
                performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
                return propagate(action,
                        ActionPropagation.FINISH_ACTION,
                        ActionPropagation.NO_SERVER_PROPAGATION,
                        ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }


// EFFECT STUFF GOES HERE
            if (counter ==1.0F&&source.getAuxData()==10)
            {
                SoundPlayer.playSound("sound.liquid.drink",performer,1.6F);
                action.setTimeLeft(30);
                performer.getCommunicator().sendActionControl(performer.getWurmId(),action.getActionString(),true,30);
                return propagate(action,
                        ActionPropagation.CONTINUE_ACTION,
                        ActionPropagation.NO_SERVER_PROPAGATION,
                        ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }
            if (action.currentSecond()>=3)
            {
                source.setWeight(source.getWeightGrams()-100,true);
                performer.getCommunicator().sendSafeServerMessage("As you drink from the coffee you feel a heightened ability to focus on your tasks");
                Spell toApply = Spells.getEnchantment((byte) 101);
                CreatureEnchantment.doImmediateEffect(toApply.number, 300, 100, performer);

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

}
