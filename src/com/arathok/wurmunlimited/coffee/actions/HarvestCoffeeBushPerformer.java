package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.Coffee;
import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.arathok.wurmunlimited.coffee.Config;
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
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
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

    boolean firstTry = true;
    boolean secondTry = true;
    boolean thirdTry = true;
    boolean fourthTry = true;

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

        return performer.isPlayer() && source.getLastOwnerId() == performer.getWurmId() && !source.isTraded() && source.getTemplateId() == CoffeeItem.coffeeShrubId;
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
            firstTry=true;
            secondTry=true;
            thirdTry=true;
            fourthTry=true;
            performer.getCommunicator().sendNormalServerMessage("You start harvesting the coffee shrub");
            performer.playAnimation("create", false, source.getWurmId());
            SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
            action.setTimeLeft(300);
            performer.sendActionControl( action.getActionString(), true, 300);
            return propagate(action,
                    ActionPropagation.CONTINUE_ACTION,
                    ActionPropagation.NO_SERVER_PROPAGATION,
                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
        }
        else {
            if (action.currentSecond() >= 3 && action.currentSecond() < 6) {

                if (performer.hasLink()) {

                    if (source.getData1() < 8) {
                        performer.getCommunicator().sendNormalServerMessage("You noticed you harvested the plant way too early and destroy the Plant");
                        source.setData1(0);  // no age
                        source.setData2(0);  // no water
                        source.setExtra(0);  // no time to harvest
                        source.setData1(0); // not watered
                        source.setAuxData((byte) 0); // no bean
                        PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check


                        return propagate(action,
                                ActionPropagation.FINISH_ACTION,
                                ActionPropagation.NO_SERVER_PROPAGATION,
                                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

                    }

                    if (source.getData1() > 8) {
                        source.setData1(0);  // no age
                        source.setData2(0);  // no water
                        source.setExtra(0);  // no time to harvest
                        source.setData1(0); // not watered
                        source.setAuxData((byte) 0); // no bean
                        PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check
                        performer.getCommunicator().sendNormalServerMessage("You noticed you harvested the plant too late. You are not able to get anything from its wilted stalks.");
                        return propagate(action,
                                ActionPropagation.FINISH_ACTION,
                                ActionPropagation.NO_SERVER_PROPAGATION,
                                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                    }
                }
            }


                    if (action.currentSecond() >= 6 && action.currentSecond() < 12) {
                        if (source.getData2() > 0&&firstTry) {
                            performer.playAnimation("create", false, source.getWurmId());
                            SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
                            Item coffeeBean = null;
                            int chance = Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20;
                            if (chance > 1) {
                                try {
                                    coffeeBean = ItemFactory.createItem(CoffeeItem.coffeeBeanId, (Math.min(100, Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20)), performer.getName());
                                    for (int i=0; i<Config.harvestmultiplicator;i++)
                                    performer.getInventory().insertItem(coffeeBean);
                                    performer.getCommunicator().sendSafeServerMessage("You manage to find a good coffee bean!");
                                } catch (FailedException | NoSuchTemplateException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                performer.getCommunicator().sendNormalServerMessage("You manage to pluck a bean but you find it to be not suited for further processing and throw it away.");
                            }
                            int newData2 = Math.max(source.getData2() - Server.rand.nextInt(4) + 1, 0);
                            source.setData2(newData2);

                            firstTry=false;

                        }
                    }

                    if (action.currentSecond() >= 12 && action.currentSecond() < 18) {
                        if (source.getData2() > 0 &&secondTry) {
                            performer.playAnimation("create", false, source.getWurmId());
                            SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
                            Item coffeeBean = null;
                            int chance = Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20;
                            if (chance > 40) {
                                try {
                                    coffeeBean = ItemFactory.createItem(CoffeeItem.coffeeBeanId, (Math.min(100, Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20)), performer.getName());
                                    for (int i=0; i<Config.harvestmultiplicator;i++)
                                        performer.getInventory().insertItem(coffeeBean);
                                    performer.getCommunicator().sendSafeServerMessage("You manage to find a good coffee bean!");
                                } catch (FailedException | NoSuchTemplateException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                performer.getCommunicator().sendNormalServerMessage("You manage to pluck a bean but you find it to be not suited for further processing and throw it away.");
                            }
                            int newData2 = Math.max(source.getData2() - Server.rand.nextInt(4) + 1, 0);
                            source.setData2(newData2);
                            secondTry=false;


                        }

                    }

                    if (action.currentSecond() >= 18 && action.currentSecond() < 24) {
                        if (source.getData2() > 0 && thirdTry) {
                            performer.playAnimation("create", false, source.getWurmId());
                            SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
                            Item coffeeBean = null;
                            int chance = Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()+20) ;
                            if (chance > 35) {
                                try {
                                    coffeeBean = ItemFactory.createItem(CoffeeItem.coffeeBeanId, (Math.min(100, Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20)), performer.getName());
                                    for (int i=0; i<Config.harvestmultiplicator;i++)
                                        performer.getInventory().insertItem(coffeeBean);
                                    performer.getInventory().insertItem(coffeeBean);
                                    performer.getCommunicator().sendSafeServerMessage("You manage to find a good coffee bean!");
                                } catch (FailedException | NoSuchTemplateException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                performer.getCommunicator().sendNormalServerMessage("You manage to pluck a bean but you find it to be not suited for further processing and throw it away.");
                            }
                            int newData2 = Math.max(source.getData2() - Server.rand.nextInt(4) + 1, 0);
                            source.setData2(newData2);
                            thirdTry=false;


                        } else {
                            performer.getCommunicator().sendNormalServerMessage("You dont see any more beans.");
                            source.setData1(0);  // no age
                            source.setData2(0);  // no water
                            source.setExtra(0);  // no time to harvest
                            source.setData1(0); // not watered
                            source.setAuxData((byte) 0); // no bean
                            source.setName(source.getTemplate().getName());
                            PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check
                            VolaTile vt = Zones.getOrCreateTile(source.getTilePos(), source.isOnSurface());
                            vt.makeInvisible(source);
                            vt.makeVisible(source);
                            return propagate(action,
                                    ActionPropagation.FINISH_ACTION,
                                    ActionPropagation.NO_SERVER_PROPAGATION,
                                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);
                        }
                    }

                    if (action.currentSecond() >= 24 && action.currentSecond() < 30) {
                        if (source.getData2() > 0 && fourthTry) {
                            performer.playAnimation("create", false, source.getWurmId());
                            SoundPlayer.playSound("sound.work.foragebotanize", performer, 1.6F);
                            Item coffeeBean = null;
                            int chance = Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20;
                            if (chance > 40) {
                                try {
                                    coffeeBean = ItemFactory.createItem(CoffeeItem.coffeeBeanId, (Math.min(100, Server.rand.nextInt((int) performer.getSkills().getSkillOrLearn(SkillList.FARMING).getKnowledge()) + 20)), performer.getName());
                                    for (int i=0; i<Config.harvestmultiplicator;i++)
                                        performer.getInventory().insertItem(coffeeBean);
                                    performer.getInventory().insertItem(coffeeBean);
                                    performer.getCommunicator().sendSafeServerMessage("You manage to find a good coffee bean!");
                                } catch (FailedException | NoSuchTemplateException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                performer.getCommunicator().sendNormalServerMessage("You manage to pluck a bean but you find it to be not suited for further processing and throw it away.");
                            }
                            performer.getCommunicator().sendNormalServerMessage("You dont see any more beans.");
                            source.setData1(0);  // no age
                            source.setData2(0);  // no water
                            source.setExtra(0);  // no time to harvest
                            source.setData1(0); // not watered
                            source.setAuxData((byte) 0); // no bean
                            source.setName(source.getTemplate().getName());
                            PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(source.getWurmId()); // remove from list to check
                            VolaTile vt = Zones.getOrCreateTile(source.getTilePos(), source.isOnSurface());
                            vt.makeInvisible(source);
                            vt.makeVisible(source);
                            fourthTry=false;
                            return propagate(action,
                                    ActionPropagation.FINISH_ACTION,
                                    ActionPropagation.NO_SERVER_PROPAGATION,
                                    ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);





                        }
                    }

                }


        return propagate(action,
                ActionPropagation.CONTINUE_ACTION,
                ActionPropagation.NO_SERVER_PROPAGATION,
                ActionPropagation.NO_ACTION_PERFORMER_PROPAGATION);

    }


}


