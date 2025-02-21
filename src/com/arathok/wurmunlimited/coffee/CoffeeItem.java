package com.arathok.wurmunlimited.coffee;

import com.wurmonline.server.items.CreationCategories;
import com.wurmonline.server.items.CreationEntryCreator;
import com.wurmonline.server.items.CreationRequirement;
import com.wurmonline.server.items.ItemTemplate;
import java.io.IOException;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.items.ModItems;
import org.gotti.wurmunlimited.modsupport.items.ModelNameProvider;

public class CoffeeItem {
    public static ItemTemplate coffeeShrub;

    public static ItemTemplate coffeeBean;

    public static ItemTemplate coffee;

    public static ItemTemplate groundCoffee;

    public static ItemTemplate coffeeMix;

    public static int coffeeShrubId;

    public static int coffeeBeanId;

    public static int coffeeId;

    public static int groundCoffeeId;

    public static int coffeeMixId;

    private static void registerCoffeeBean() throws IOException {
        coffeeBean = (new ItemTemplateBuilder("arathok.Coffee.Coffeebean")).name("coffee beans", "coffee beans", "A bunch of beans of a coffee shrub originating from Mt. Arathok. The Monks up there brewed it into a coffee to heighten their senses.").modelName("model.resource.cocoabean.").imageNumber((short)518).itemTypes(new short[] {(short) (Config.isBulk ? 146 : 187), 5, 20, 51, 55, 86 }).decayTime(Long.MAX_VALUE).dimensions(1, 1, 1).weightGrams(10).material((byte)45).behaviourType((short)1).primarySkill(10049).difficulty(50.0F).build();
        coffeeBeanId = coffeeBean.getTemplateId();
    }

    private static void registerCoffeePlanter() throws IOException {
        coffeeShrub = (new ItemTemplateBuilder("arathok.Coffee.Coffeeshrub")).name("coffee planter", "coffee Shrub", "A coffee planter that is suited to grow coffee plants, which bear coffee beans every now and then if watered Daily").modelName("model.arathok.coffee.planter.").imageNumber((short)311).itemTypes(new short[] { 199, 52, 51, 195, 86, 48, 255, 31 }).decayTime(Long.MAX_VALUE).dimensions(40, 40, 60).weightGrams(40000).material((byte)45).behaviourType((short)1).primarySkill(10049).difficulty(50.0F).build();
        coffeeShrubId = coffeeShrub.getTemplateId();
        if (Config.isPlantable)
            CreationEntryCreator.createAdvancedEntry(10049, 217, 22, coffeeShrubId, true, false, 0.0F, true, true, 0, 40.0D, CreationCategories.FOOD)

                    .addRequirement(new CreationRequirement(1, 217, 16, true))
                    .addRequirement(new CreationRequirement(2, 22, 14, true))
                    .addRequirement(new CreationRequirement(3, 26, 1, true));
    }

    private static void registerCoffeeCup() throws IOException {
        coffee = (new ItemTemplateBuilder("arathok.Coffee.Coffee")).name("coffee", "coffee", "A delicious liquid making you go hyperfocussed beyond the power of sleep!").modelName("model.arathok.coffee").imageNumber((short)568).itemTypes(new short[] { 26, 88, 214, 146, 212 }).decayTime(Long.MAX_VALUE).dimensions(1, 1, 1).weightGrams(10).material((byte)45).behaviourType((short)1).primarySkill(10049).difficulty(50.0F).build();
        coffeeId = coffee.getTemplateId();
    }

    private static void registerCoffeePowder() throws IOException {
        groundCoffee = (new ItemTemplateBuilder("arathok.Coffee.GroundCoffee")).name("ground coffee", "ground coffee", "A bitter powder which might be brewable into something else").modelName("model.arathok.groundcoffee.").imageNumber((short)518).itemTypes(new short[] { 55, 51, 5, (short) (Config.isBulk ? 146 : 187), 86 }).decayTime(Long.MAX_VALUE).dimensions(1, 1, 1).weightGrams(100).material((byte)0).behaviourType((short)1).primarySkill(10049).difficulty(50.0F).build();
        groundCoffeeId = groundCoffee.getTemplateId();
        CreationEntryCreator.createSimpleEntry(10040, coffeeBeanId, 202, groundCoffeeId, true, false, 100.0F, false, false, 0, 30.0D, CreationCategories.FOOD);
    }

    private static void registerCoffeeMix() throws IOException {
        coffeeMix = (new ItemTemplateBuilder("arathok.Coffee.CoffeeMix")).name("coffee mix", "coffee mix", "A mix of water and ground coffee, needs to be passed through a still for the water to take up the effects of the coffee").modelName("model.arathok.coffeeMix").imageNumber((short)568).itemTypes(new short[] { 5, 26, 88, 214, 146 }).decayTime(Long.MAX_VALUE).dimensions(1, 1, 1).weightGrams(1100).material((byte)0).behaviourType((short)1).primarySkill(10049).difficulty(50.0F).build();
        coffeeMixId = coffeeMix.getTemplateId();
        CreationEntryCreator.createSimpleEntry(10083, 128, groundCoffeeId, coffeeMixId, true, true, 0.0F, false, false, 0, 30.0D, CreationCategories.FOOD);
    }

    public static void register() throws IOException {
        registerCoffeeBean();
        registerCoffeePlanter();
        registerCoffeeCup();
        registerCoffeePowder();
        registerCoffeeMix();
        ModelNameProvider modelNameProvider = new CoffeePlanterModelProvider();
        ModItems.addModelNameProvider(coffeeShrubId, modelNameProvider);
    }
}
