package com.arathok.wurmunlimited.coffee.actions;

import com.arathok.wurmunlimited.coffee.CoffeeItem;
import com.wurmonline.server.behaviours.ActionEntry;
import com.wurmonline.server.creatures.Creature;
import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modsupport.actions.BehaviourProvider;
import org.gotti.wurmunlimited.modsupport.actions.ModActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoffeeBehavior implements BehaviourProvider {


    private final List<ActionEntry> takeaSip;
    private final List<ActionEntry> harvest;
    private final CoffePerformer acoffeePerformer;
    private final HarvestCoffeeBushPerformer harvestPerformer;


    public CoffeeBehavior() {
        this.acoffeePerformer = new CoffePerformer();
        this.harvestPerformer = new HarvestCoffeeBushPerformer();
        this.takeaSip = Collections.singletonList(acoffeePerformer.actionEntry);
        this.harvest = Collections.singletonList(harvestPerformer.actionEntry);

        ModActions.registerActionPerformer(acoffeePerformer);
        ModActions.registerActionPerformer(harvestPerformer);

    }

    //, , , , ,
    //, , , , ;

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {

        if (!target.isTraded()&&target.getOwnerId()==performer.getWurmId()&&target.getTemplateId()== CoffeeItem.coffeeId) {

                return new ArrayList<>(takeaSip);
        }
        if (!target.isTraded()&&target.getTemplateId()== CoffeeItem.coffeeShrubId&&target.getAuxData()==1) {

            return new ArrayList<>(harvest);
        }
       return null;
    }
    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        return getBehavioursFor(performer, target);
    }

}
