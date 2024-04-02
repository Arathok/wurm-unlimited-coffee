package com.arathok.wurmunlimited.coffee;

import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modsupport.items.ModelNameProvider;

public class CoffeePlanterModelProvider implements ModelNameProvider {
    @Override
    public String getModelName(Item item) {
        StringBuilder sb = new StringBuilder(item.getTemplate().getModelName());

        if (item.getAuxData()==(byte)0)
            sb.append("dirt");
        else {


            if (item.getData1() <= 2)
                sb.append("young.");

            if (item.getData1() > 2 && item.getData1() < 5)
                sb.append("growing.");

            if (item.getData1() >= 5 && item.getData1() <= 7)
                sb.append("sprouting.");

            if (item.getData1() == 8)
                sb.append("ripe.");

            if (item.getData1() > 8)
                sb.append("wilted.");

        }
        return sb.toString();
    }
}
