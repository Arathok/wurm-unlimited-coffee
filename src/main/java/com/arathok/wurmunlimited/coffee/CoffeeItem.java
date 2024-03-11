package com.arathok.wurmunlimited.coffee;

import com.wurmonline.server.items.*;
import com.wurmonline.server.skills.SkillList;
import com.wurmonline.shared.constants.IconConstants;
import org.gotti.wurmunlimited.modsupport.ItemTemplateBuilder;

import java.io.IOException;

public class CoffeeItem {

    private static ItemTemplate coffeeShrub,coffeeBean;
    private static int coffeeShrubId,coffeeBeanId;


    private static void registerCoffeeBean() throws IOException {
        coffeeBean = new ItemTemplateBuilder("arathok.Coffee.Coffeebean").name("coffee bean",
                        "coffee Beans",  "A bean of a coffee shrub. If brewed with water it might turn into a malty source of energy for your body!"
                )
                .modelName("model.resource.cocoabean.")
                .imageNumber((short) IconConstants.ICON_FOOD_SEED)
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

    private static void registerCoffeeShrub() throws IOException {
        coffeeShrub = new ItemTemplateBuilder("arathok.Coffee.Coffeeshrub").name("coffee shrub",
                        "coffee Shrub",  "A coffee shrub in a planter that bears coffee beans every now and then if watered Daily"
                )
                .modelName("model.bush.lingonberry.")
                .imageNumber((short) IconConstants.ICON_FOOD_SALAD)
                .itemTypes(new short[]{

                        //ItemTypes.ITEM_TYPE_BULK,

                        ItemTypes.ITEM_TYPE_PLANTABLE,
                        ItemTypes.ITEM_TYPE_DECORATION,
                        ItemTypes.ITEM_TYPE_TURNABLE,
                        ItemTypes.ITEM_TYPE_TILE_ALIGNED,
                        ItemTypes.ITEM_TYPE_DESTROYABLE,

                })
                .decayTime(Long.MAX_VALUE)
                .dimensions(40, 40, 60)
                .weightGrams(10000)
                .material(Materials.MATERIAL_WOOD_CHERRY)
                .behaviourType((short) 1)
                .primarySkill(SkillList.FARMING).difficulty(50) // no hard lock
                .build();

        coffeeShrubId = coffeeShrub.getTemplateId();

        CreationEntryCreator
                .createAdvancedEntry(SkillList.FARMING, ItemList.nailsIronLarge, ItemList.plank, coffeeShrubId, false,
        false, 0f, false, true, 0, 40, CreationCategories.MAGIC)
                .addRequirement(new CreationRequirement(1, ItemList.nailsIronLarge, 16, true))
                .addRequirement(new CreationRequirement(1, ItemList.plank, 14, true))
                .addRequirement(new CreationRequirement(1, CoffeeItem.coffeeBeanId, 1, true));

    }

    private static void registerCoffeeCup() throws IOException {
        coffeeBean = new ItemTemplateBuilder("arathok.Coffee.Coffee").name("coffee",
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
                .weightGrams(10)
                .material(Materials.MATERIAL_WOOD_CHERRY)
                .behaviourType((short) 1)
                .primarySkill(SkillList.FARMING).difficulty(50) // no hard lock
                .build();

        coffeeBeanId = coffeeBean.getTemplateId();



    }



    public static void register() throws IOException {

        registerCoffeeBean();
        registerCoffeeShrub();
    }

}
