package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import com.wurmonline.server.items.ItemList;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoffeeTargetedBehavior implements BehaviourProvider {


    private final List<ActionEntry> water;
    private final List<ActionEntry> plant;
    private final PlantCoffeeBushPerformer plantCoffeeBushPerformer;
    private final WaterCoffeeBushPerformer waterCoffeeBushPerformer;


    public CoffeeTargetedBehavior() {
        this.plantCoffeeBushPerformer = new PlantCoffeeBushPerformer();
        this.waterCoffeeBushPerformer = new WaterCoffeeBushPerformer();
        this.plant = Collections.singletonList(plantCoffeeBushPerformer.actionEntry);
        this.water = Collections.singletonList(waterCoffeeBushPerformer.actionEntry);

        ModActions.registerActionPerformer(plantCoffeeBushPerformer);
        ModActions.registerActionPerformer(waterCoffeeBushPerformer);


    }

    //, , , , ,
    //, , , , ;

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {

        if (!target.isTraded() && target.getLastOwnerId() == performer.getWurmId() && target.getTemplateId() == CoffeeItem.coffeeShrubId && target.getAuxData() == 0) {

            return new ArrayList<>(plant);
        }
        if (source.getTemplateId()== ItemList.water&&!target.isTraded() && target.getLastOwnerId() == performer.getWurmId() && target.getTemplateId() == CoffeeItem.coffeeShrubId && target.getData1() <= 7 && target.getAuxData() == 1) {


                return new ArrayList<>(water);
        }
        return null;
    }


}
