package plc.type;

import arc.struct.Seq;
import mindustry.content.Liquids;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.Planet;

public class PLCPlanet extends Planet {

    public final Seq<LiquidStack> atmosphereGases = new Seq<>();
    public final Seq<Item> items = new Seq<>();
    public final Seq<Liquid> liquids = new Seq<>();
    private Seq<LiquidStack> fallbackGases;

    public PLCPlanet(String name, Planet parent, float radius){
        super(name, parent, radius);
    }

    public PLCPlanet(String name, Planet parent, float radius, int sectorSize){
        super(name, parent, radius, sectorSize);
    }

    public PLCPlanet addAtmosphereGas(Liquid liquid, float concentration){
        atmosphereGases.add(new LiquidStack(liquid, concentration / 1));
        return this;
    }

    public PLCPlanet addItems(Seq<Item> itemsSeq){
        this.items.addAll(itemsSeq);
        return this;
    }

    public PLCPlanet addLiquids(Liquid... liquidsArray){
        this.liquids.addAll(liquidsArray);
        return this;
    }
    public PLCPlanet addOres(Seq<Item> itemsSeq){
        for(Item item : itemsSeq){
            if(item.hardness > 0){
                this.items.add(item);
            }
        }
        return this;
    }
    public Seq<LiquidStack> getAtmosphereGases(){
        if(atmosphereGases.isEmpty()){
            if(fallbackGases == null){
                fallbackGases = Seq.with(new LiquidStack(Liquids.hydrogen, 1f));
            }
            return fallbackGases;
        }
        return atmosphereGases;
    }
}