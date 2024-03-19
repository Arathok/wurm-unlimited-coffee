package com.arathok.wurmunlimited.coffee;


import com.arathok.wurmunlimited.coffee.actions.CoffeeBehavior;
import com.arathok.wurmunlimited.coffee.actions.CoffeeTargetedBehavior;
import com.arathok.wurmunlimited.coffee.actions.PlantCoffeeBushPerformer;
import com.wurmonline.server.Items;
import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.players.Player;
import com.wurmonline.server.spells.Hyperfocus;
import com.wurmonline.server.spells.Spells;
import org.gotti.wurmunlimited.modloader.ReflectionUtil;
import org.gotti.wurmunlimited.modloader.interfaces.*;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coffee implements WurmServerMod, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener, ServerPollListener, PlayerMessageListener,PlayerLoginListener{
    public static Logger logger= Logger.getLogger("CaveCrystal");
    public static Connection dbconn;
    public static boolean finishedReadingDB = false;
    public static boolean finishedDbReadingEnchantments =false;
    private long coffeePoller=0L;


    @Override
    public void configure (Properties properties) {

    }


    @Override
    public void preInit() {


    logger.log(Level.INFO,"Hooking Cave Crystal Behaviour");
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
        Hyperfocus hyperfocus = new Hyperfocus(0, 0, 1, 10, 0);
        try {
            ReflectionUtil.callPrivateMethod(Spells.class, ReflectionUtil.getMethod(Spells.class, "addSpell"), hyperfocus);
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

        if (coffeePoller < System.currentTimeMillis())
        {
            for (Map.Entry<Long, Long> oneItem : PlantCoffeeBushPerformer.activeCoffeeShrubs.entrySet())
            {
                if(oneItem.getValue()<System.currentTimeMillis())
                {
                    Optional<Item> maybeItem = Items.getItemOptional(oneItem.getKey());
                    if (maybeItem.isPresent())
                    {
                        Item realPlanter = maybeItem.get();
                        if (realPlanter.getData1()>0)
                        realPlanter.setData1(realPlanter.getData1()+1);

                    }
                }
            }
            coffeePoller = System.currentTimeMillis()+3600000L;
        }


    }


    @Override
    public void onPlayerLogin(Player player) {


    }
}
