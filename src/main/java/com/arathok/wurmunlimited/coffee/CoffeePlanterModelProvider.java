package com.arathok.wurmunlimited.coffee;

import com.wurmonline.server.items.Item;
import org.gotti.wurmunlimited.modsupport.items.ModelNameProvider;

public class CoffeePlanterModelProvider implements ModelNameProvider {
    @Override
    public String getModelName(Item item) {
        StringBuilder sb = new StringBuilder(item.getTemplate().getModelName());

        if (item.getAuxData()==(byte)0)
            sb.append("dirt");

        if (item.getData()>0&&item.getData1()<7)
            sb.append("growing.");

        if (item.getData()>7)
            sb.append("ripe.");

        if (item.getData()>8)
            sb.append("wilted.");

        return sb.toString();
    }
}
