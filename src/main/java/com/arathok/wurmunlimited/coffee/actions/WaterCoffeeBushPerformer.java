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
import java.util.logging.Level;

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

                    try {
                            long id = source.getWurmId();
                            byte[] temp = (source.getModelName()+"young.").getBytes("UTF-8");
                        SocketConnection performerConnection = performer.getCommunicator().getConnection();
                          ByteBuffer bb =performerConnection.getBuffer();
                           bb.put((byte)-48);
                           bb.putLong(id);
                            bb.put((byte)temp.length);
                           bb.put(temp);
                           performerConnection.flush();
                          }
                     catch (Exception ex) {

                            Coffee.logger.log(Level.WARNING,"Failed to change model for item: " + performer.getName() + ':' + source.getWurmId() + ", " + ex
             .getMessage(), ex);
                         Players.getInstance().getPlayerOrNull(performer.getWurmId()).setLink(false);
                          }
                    }
            source.setData1(0);
            if (source.getData2()>0) {
                performer.getCommunicator().sendSafeServerMessage("You Harvest a bunch of coffeeBeans");
                for (int i = 0; i < source.getData2(); i = +2) // How much watering done
                {
                    try {
                        Item coffeeBean = ItemFactory.createItem(CoffeeItem.coffeeBeanId, (Math.min(100, Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20)), performer.getName());
                        performer.getInventory().insertItem(coffeeBean);
                    } catch (FailedException e) {
                        throw new RuntimeException(e);
                    } catch (NoSuchTemplateException e) {
                        Coffee.logger.log(Level.WARNING, "no such id! " + e.getMessage(), e);
                        throw new RuntimeException(e);
                    }

                }
            }
            else
            {
                performer.getCommunicator().sendSafeServerMessage("The Coffee Shrub looks very dry. It seems it did not have enough water to produce any beans.");
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


