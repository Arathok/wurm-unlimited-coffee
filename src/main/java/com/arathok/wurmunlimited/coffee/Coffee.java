package com.arathok.wurmunlimited.coffee;


import com.wurmonline.server.creatures.Communicator;
import com.wurmonline.server.players.Player;
import org.gotti.wurmunlimited.modloader.interfaces.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Coffee implements WurmServerMod, Initable, PreInitable, Configurable, ItemTemplatesCreatedListener, ServerStartedListener, ServerPollListener, PlayerMessageListener,PlayerLoginListener{
    public static Logger logger= Logger.getLogger("CaveCrystal");
    public static Connection dbconn;
    public static boolean finishedReadingDB = false;
    public static boolean finishedDbReadingEnchantments =false;





    @Override
    public void configure (Properties properties) {

    }


    @Override
    public void preInit() {


    logger.log(Level.INFO,"Hooking Cave Crystal Behaviour");
    CaveCrystalHook.insert();


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

    }


    @Override
    public void init() {


    }

    @Override
    public void onServerPoll() {



    }


    @Override
    public void onPlayerLogin(Player player) {


    }
}
