package com.arathok.wurmunlimited.coffee;

import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.shared.constants.IconConstants;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;
import org.gotti.wurmunlimited.modsupport.items.ModItems;
import org.gotti.wurmunlimited.modsupport.items.ModelNameProvider;

import java.io.IOException;

public class CoffeeItem {

    public static ItemTemplate coffeeShrub,coffeeBean, coffee, groundCoffee;
    public static int coffeeShrubId;
    public static int coffeeBeanId;
    public static int coffeeId,groundCoffeeId;


    private static void registerCoffeeBean() throws IOException {
        coffeeBean = new ItemTemplateBuilder("arathok.Coffee.Coffeebean").name("coffee bean",
                        "coffee Beans",  "A bean of a coffee shrub. If brewed with water it might turn into a malty source of energy for your body!"
                )
                .modelName("model.resource.cocoabean.")
                .imageNumber((short) IconConstants.ICON_FOOD_COCOA_BEAN)
                .itemTypes(new short[]{

                        //ItemTypes.ITEM_TYPE_BULK,

                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_TILE_ALIGNED,
                        ItemTypes.ITEM_TYPE_DESTROYABLE,

                })
                .decayTime(Long.MAX_VALUE)
                .dimensions(1, 1, 1)
                .weightGrams(10)
                .material(Materials.MATERIAL_WOOD_CHERRY)
                .behaviourType((short) 1)
                .primarySkill(SkillList.FARMING).difficulty(50) // no hard lock
                .build();

        coffeeBeanId = coffeeBean.getTemplateId();



    }

    private static void registerCoffeePlanter() throws IOException {
        coffeeShrub = new ItemTemplateBuilder("arathok.Coffee.Coffeeshrub").name("coffee planter",
                        "coffee Shrub",  "A coffee planter that is suited to grow coffee plants, which bear coffee beans every now and then if watered Daily"
                )
                .modelName("model.arathok.coffee.planter.")
                .imageNumber((short) IconConstants.ICON_LARGE_CRATE)
                .itemTypes(new short[]{

                        //ItemTypes.ITEM_TYPE_BULK,

                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_TILE_ALIGNED,
                        ItemTypes.ITEM_TYPE_DESTROYABLE,
                        ItemTypes.ITEM_TYPE_HASDATA,
                        ItemTypes.ITEM_TYPE_HAS_EXTRA_DATA,

                })
                .decayTime(Long.MAX_VALUE)
                .dimensions(40, 40, 60)
                .weightGrams(40000)
                .material(Materials.MATERIAL_WOOD_CHERRY)
                .behaviourType((short) 1)
                .primarySkill(SkillList.FARMING).difficulty(50) // no hard lock
                .build();

        coffeeShrubId = coffeeShrub.getTemplateId();
        if (Config.isPlantable)
        CreationEntryCreator
                .createAdvancedEntry(SkillList.FARMING, ItemList.nailsIronLarge, ItemList.plank, coffeeShrubId, false,
        false, 0f, false, true, 0, 40, CreationCategories.MAGIC)
                .addRequirement(new CreationRequirement(1, ItemList.nailsIronLarge, 16, true))
                .addRequirement(new CreationRequirement(1, ItemList.plank, 14, true))
                .addRequirement(new CreationRequirement(1, ItemList.dirtPile, 1, true));

    }

    private static void registerCoffeeCup() throws IOException {
        coffee = new ItemTemplateBuilder("arathok.Coffee.Coffee").name("coffee",
                        "coffee",  "A delicious liquid making you go hyperfocussed beyond the power of sleep!"
                )
                .modelName("model.arathok.coffee")
                .imageNumber((short) IconConstants.ICON_LIQUID_GRAVY)
                .itemTypes(new short[]{
                        //26, 88, 90, 113, 212
                        //ItemTypes.ITEM_TYPE_BULK,

                        ItemTypes.ITEM_TYPE_LIQUID_DRINKABLE,
                        Item.ITEM_TYPE_LIQUID,
                        ItemTypes.ITEM_TYPE_BULK,

                })
                .decayTime(Long.MAX_VALUE)
                .dimensions(1, 1, 1)
                .weightGrams(100)
                .material(Materials.MATERIAL_WOOD_CHERRY)
                .behaviourType((short) 1)
                .primarySkill(SkillList.FARMING).difficulty(50) // no hard lock
                .build();

        coffeeId = coffee.getTemplateId();



    }

    private static void registerCoffeePowder() throws IOException {
        groundCoffee = new ItemTemplateBuilder("arathok.Coffee.GroundCoffee").name("ground coffee",
                        "ground coffee",  "A bitter powder which might be brewable into something else"
                )
                .modelName("model.arathok.groundcoffee")
                .imageNumber((short) IconConstants.ICON_FOOD_COCOA)
                .itemTypes(new short[]{
                        //26, 88, 90, 113, 212
                        //ItemTypes.ITEM_TYPE_BULK,

                        ItemTypes.ITEM_TYPE_LIQUID_DRINKABLE,
                        Item.ITEM_TYPE_LIQUID,
                        ItemTypes.ITEM_TYPE_BULK,

                })
                .decayTime(Long.MAX_VALUE)
                .dimensions(1, 1, 1)
                .weightGrams(10)
                .material(Materials.MATERIAL_WOOD_CHERRY)
                .behaviourType((short) 1)
                .primarySkill(SkillList.FARMING).difficulty(50) // no hard lock
                .build();

        groundCoffeeId = groundCoffee.getTemplateId();
        CreationEntryCreator.createSimpleEntry(SkillList.MILLING,coffeeBeanId,ItemList.grindstone,groundCoffeeId,true,false,0F,false,false,0,30.0,CreationCategories.FOOD);


    }



    public static void register() throws IOException {

        registerCoffeeBean();
        registerCoffeePlanter();
        registerCoffeeCup();
        registerCoffeePowder();
        ModelNameProvider modelNameProvider = new CoffeePlanterModelProvider();
        ModItems.addModelNameProvider(coffeeShrubId,modelNameProvider);
    }

}
