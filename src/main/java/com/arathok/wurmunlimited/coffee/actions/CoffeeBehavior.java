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
    private final CoffePerformer acoffeePerformer;


    public CoffeeBehavior() {
        this.acoffeePerformer = new CoffePerformer();

        this.takeaSip = Collections.singletonList(acoffeePerformer.actionEntry);

        ModActions.registerActionPerformer(acoffeePerformer);


    }

    //, , , , ,
    //, , , , ;

    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item target) {

        if (!target.isTraded()&&target.getOwnerId()==performer.getWurmId()&&target.getTemplateId()== CoffeeItem.coffeeId) {

                return new ArrayList<>(takeaSip);
        }
       return null;
    }
    @Override
    public List<ActionEntry> getBehavioursFor(Creature performer, Item source, Item target) {
        return getBehavioursFor(performer, source);
    }

}
