package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.Coffee;
import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.arathok.wurmunlimited.coffee.Config;
import com.wurmonline.communication.SocketConnection;
import com.wurmonline.server.FailedException;
import com.wurmonline.server.Items;
import com.wurmonline.server.Players;
import com.wurmonline.server.Server;
import com.wurmonline.server.behaviours.Action;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemFactory;
import com.wurmonline.server.items.NoSuchTemplateException;
import com.wurmonline.server.skills.Skill;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.server.sounds.SoundPlayer;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import org.gotti.wurmunlimited.modsupport.actions.ActionEntryBuilder;
import org.gotti.wurmunlimited.modsupport.actions.ActionPerformer;
import org.gotti.wurmunlimited.modsupport.actions.ActionPropagation;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

public class PlantCoffeeBushPerformer implements ActionPerformer {


    public final ActionEntry actionEntry;
    public static HashMap<Long,Long> activeCoffeeShrubs = new HashMap<>();

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
            performer.sendActionControl(action.getActionString(), true, 30);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        if (action.currentSecond() >= 3) {

            Skill farming = performer.getSkills().getSkillOrLearn(SkillList.FARMING);
            double power = farming.skillCheck(1.0f + source.getDamage(), source.getCurrentQualityLevel(), false, counter);
            if (power>0) {
                if (performer.hasLink()) {
                    long nextTendTime = System.currentTimeMillis() + 86400000L;
                    target.setData1(0); // Set Age to 0
                    target.setData2(0); // number of times watered

                    target.setAuxData((byte) 1); // has sprout
                    target.setExtra1(0);// hasWater

                    activeCoffeeShrubs.put(target.getWurmId(),System.currentTimeMillis()+ Config.tendingDuration);
                    SoundPlayer.playSound("sound.forest.branchsnap", performer, 1.6F);
                    performer.getCommunicator().sendSafeServerMessage("You succesfully plant the coffee in the planter, you should water it so it can start to grow.");
                    VolaTile vt = Zones.getOrCreateTile(target.getTilePos(), target.isOnSurface());
                    vt.makeInvisible(target);
                    vt.makeVisible(target);
                    target.setName(source.getTemplate().getName()+" - young");
                    Items.destroyItem(source.getWurmId());
                }
            }
            else {
                SoundPlayer.playSound("sound.forest.branchsnap", performer, 1.6F);
                performer.getCommunicator().sendSafeServerMessage("Sadly, you make a few mistakes and the sprout does not survive.");
                Items.destroyItem(source.getWurmId());
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


