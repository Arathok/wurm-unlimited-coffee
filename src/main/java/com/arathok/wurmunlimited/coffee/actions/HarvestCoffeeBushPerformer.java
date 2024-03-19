package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.Coffee;
import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Features;
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
import com.wurmonline.server.spells.CreatureEnchantment;
import com.wurmonline.server.spells.Spell;
import com.wurmonline.server.spells.Spells;
import com.wurmonline.shared.constants.ItemMaterials;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.logging.Level;

public class HarvestCoffeeBushPerformer implements ActionPerformer {


    public final ActionEntry actionEntry;

    public HarvestCoffeeBushPerformer() {


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

        return performer.isPlayer() && source.getOwnerId() == performer.getWurmId() && !source.isTraded() && source.getTemplateId() == CoffeeItem.coffeeShrubId;
    }


    @Override
    public boolean action(Action action, Creature performer, Item source, short num, float counter) { // Since we use target and source this time, only need that override
		/*if (target.getTemplateId() != AlchItems.weaponOilDemiseAnimalId)

			return propagate(action,
					ActionPropagation.SERVER_PROPAGATION,
					ActionPropagation.ACTION_PERFORMER_PROPAGATION);*/
        if (!canUse(performer, source)) {
            performer.getCommunicator().sendAlertServerMessage("You are not allowed to do that");
            return propagate(action,
                    ActionPropagation.FINISH_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }


// EFFECT STUFF GOES HERE
        if (counter == 1.0F) {

            performer.getCommunicator().sendNormalServerMessage("You start harvesting the coffee shrub");
            performer.playAnimation("create", false,source.getWurmId());
            SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
            action.setTimeLeft(300);
            performer.getCommunicator().sendActionControl(performer.getWurmId(), action.getActionString(), true, 30);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        if (action.currentSecond() >= 3&&action.currentSecond()<6) {

            if (performer.hasLink()) {

                if (source.getData1() < 8) {
                    performer.getCommunicator().sendNormalServerMessage("You noticed you harvested the plant way too early and destroy the Plant");
                    source.setData1(0);  // no age
                    source.setData2(0);  // no water
                    source.setExtra(0);  // no time to harvest
                    source.setAuxData((byte) 0); // no bean
                    PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check


                    return propagate(action,
                            ActionPropagation.FINISH_ACTION,
                            ActionPropagation.NO_SERVER_PROPAGATION,
                            ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

                }

                if (source.getData1() >8)
                {
                    source.setData1(0);  // no age
                    source.setData2(0);  // no water
                    source.setExtra(0);  // no time to harvest
                    source.setAuxData((byte) 0); // no bean
                    PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check
                    performer.getCommunicator().sendNormalServerMessage("You noticed you harvested the plant too late. You are not able to get anything from its wilted stalks.");
                    return propagate(action,
                            ActionPropagation.FINISH_ACTION,
                            ActionPropagation.NO_SERVER_PROPAGATION,
                            ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                }
                if (source.getData2()==0)
                {
                    source.setData1(0);  // no age
                    source.setData2(0);  // no water
                    source.setExtra(0);  // no time to harvest
                    source.setAuxData((byte) 0); // no bean
                    PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check
                    performer.getCommunicator().sendSafeServerMessage("The Coffee Shrub looks very dry. It seems it did not have enough water to produce any beans.");
                    return propagate(action,
                            ActionPropagation.FINISH_ACTION,
                            ActionPropagation.NO_SERVER_PROPAGATION,
                            ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

                }




                if (action.currentSecond()>=6&& action.currentSecond()<=12 )
                {
                    if (source.getData2()>0)
                    {
                        performer.playAnimation("create", false,source.getWurmId());
                        SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
                        Item coffeeBean = null;
                        try {
                            coffeeBean = ItemFactory.createItem(CoffeeItem.coffeeBeanId, (Math.min(100, Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20)), performer.getName());
                        } catch (FailedException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchTemplateException e) {
                            throw new RuntimeException(e);
                        }
                        performer.getInventory().insertItem(coffeeBean);
                        performer.getCommunicator().sendSafeServerMessage("You manage to find another bean!");
                        int newData2 = Math.max(source.getData2()-2,0);
                        // TODO let it go again!
                    }
                }

                    source.setData1(0);  // no age
                source.setData2(0);  // no water
                source.setExtra(0);  // no time to harvest
                source.setAuxData((byte) 0); // no bean
                PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check
                return propagate(action,
                        ActionPropagation.FINISH_ACTION,
                        ActionPropagation.NO_SERVER_PROPAGATION,
                        ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
            }


        }
        return propagate(action,
                ActionPropagation.CONTINUE_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

    }



}


