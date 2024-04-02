package com.arathok.wurmunlimited.coffee;


import com.arathok.wurmunlimited.coffee.actions.CoffeeBehavior;
import com.arathok.wurmunlimited.coffee.actions.CoffeeTargetedBehavior;
import com.arathok.wurmunlimited.coffee.actions.PlantCoffeeBushPerformer;
import com.arathok.wurmunlimited.coffee.hooks.CoffeeForageHook;
import com.arathok.wurmunlimited.coffee.hooks.CoffeeHook;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.spells.Caffeinated;

import com.wurmonline.server.spells.Spells;
import com.wurmonline.server.zones.VolaTile;
import com.wurmonline.server.zones.Zones;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.classhooks.HookManager;
import org.gotti.wurmunlimited.modloader.interfaces.*;

import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coffee implements WurmServerMod, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener, ServerPollListener, PlayerMessageListener, PlayerLoginListener {
    public static Logger logger = Logger.getLogger("Coffee");
    public static Connection dbconn;
    public static boolean finishedReadinglist = false;
    public static boolean finishedDbReadingEnchantments = false;
    private long coffeePoller = 0L;
    private ArrayList<Long> toRemove = new ArrayList<>();


   static { CtClass.debugDump = "F:/SteamLibrary/steamapps/common/Wurm Unlimited Dedicated Server/mods/debug"; }

    @Override
    public void configure(Properties properties) {

        Config.isPlantable=Boolean.parseBoolean(properties.getProperty("isPlantable","true"));
        Config.isForageable=Boolean.parseBoolean(properties.getProperty("isForageable","true"));
        Config.chanceToFindCoffeeOneinX=Integer.parseInt(properties.getProperty("chanceToFindCoffeeOneinX", "1000"));
        Config.tendingDuration=Long.parseLong(properties.getProperty("tendingDuration", "86400000"));


    }


    @Override
    public void preInit() {


        logger.log(Level.INFO, "Hooking Coffee Behaviour");
        CoffeeForageHook.insertForage();
        CoffeeHook.insert();


    }

    @Override
    public boolean onPlayerMessage(Communicator communicator, String message) {

        return false;
    }

    @Override
    public void onItemTemplatesCreated() {

        try {
            CoffeeItem.register();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void onServerStarted() {
        logger.log(Level.INFO, "Registering Spells");
        Caffeinated caffeinated = new Caffeinated(0, 0, 1, 10, 0);
        try {
            ReflectionUtil.callPrivateMethod(Spells.class, ReflectionUtil.getMethod(Spells.class, "addSpell"), caffeinated);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        logger.log(Level.INFO, "Done Registering Spells");
        ModActions.registerBehaviourProvider(new CoffeeBehavior());
        ModActions.registerBehaviourProvider(new CoffeeTargetedBehavior());
    }


    @Override
    public void init() {


    }

    @Override
    public void onServerPoll() {

        if (!finishedReadinglist)
        {
            long duration=System.currentTimeMillis();
            logger.log(Level.INFO,"Coffee is reading all planters.");
            for (Item oneItem : Items.getAllItems())
            {
                if (oneItem.getTemplateId()==CoffeeItem.coffeeShrubId&&oneItem.getAuxData()==1)
                    PlantCoffeeBushPerformer.activeCoffeeShrubs.put(oneItem.getWurmId(),0L);
            }
            finishedReadinglist=true;
            duration=System.currentTimeMillis()-duration;
            logger.log(Level.INFO,"Coffee is done reading all planters. this took: "+duration+" millis");
        }

        if (coffeePoller < System.currentTimeMillis()) {
            for (Map.Entry<Long,Long> onePlanter : PlantCoffeeBushPerformer.activeCoffeeShrubs.entrySet())

            {
                Long itemToEdit= onePlanter.getKey();
                Optional<Item> maybeRealItem= Items.getItemOptional(itemToEdit);
                if (!maybeRealItem.isPresent()) {
                    toRemove.add(itemToEdit);
                    logger.log(Level.WARNING,"CoffeePlanter "+itemToEdit+" was not there! Removing from poller.");
                }

                else
                {
                    Item realItem= maybeRealItem.get();
                    VolaTile vt = Zones.getOrCreateTile(realItem.getTilePos(), realItem.isOnSurface());
                    vt.makeInvisible(realItem);
                    if (realItem.getExtra()<System.currentTimeMillis()&&realItem.getExtra1()==1)
                    {
                        realItem.setExtra1(0);
                        realItem.setData1(realItem.getData1()+1);


                        if (realItem.getData1()<=2)
                            realItem.setName(realItem.getTemplate().getName()+" - young");

                        if (realItem.getData1()>2&&realItem.getData1()<5)
                            realItem.setName(realItem.getTemplate().getName()+" - growing");

                        if (realItem.getData1()>=5&&realItem.getData1()<=7)
                            realItem.setName(realItem.getTemplate().getName()+" - sprouting");

                        if (realItem.getData1()==8)
                            realItem.setName(realItem.getTemplate().getName()+" - ripe");




                    }
                    if (realItem.getData1()>8)
                        realItem.setName(realItem.getTemplate().getName()+" - wilted");
                    vt.makeVisible(realItem);
                }

            }
            for (long oneEntry : toRemove)
            {
                PlantCoffeeBushPerformer.activeCoffeeShrubs.remove(oneEntry);
            }
            coffeePoller = System.currentTimeMillis() + 15000L;
        }

    }


    @Override
    public void onPlayerLogin(Player player) {


    }
}
