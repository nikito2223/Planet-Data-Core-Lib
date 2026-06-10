package plc.ui.dialogs;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.type.*;
import mindustry.gen.*;
import mindustry.graphics.Pal;
import plc.type.PLCPlanet;

public class PlanetInfoDialog extends BaseDialog {

    public PlanetInfoDialog(){
        super("@planet-info");
        addCloseButton();
    }

    private Seq<Item> getPlanetItems(Planet planet) {
        Seq<Item> rawList = new Seq<>();

        if(planet instanceof PLCPlanet lp) {
            rawList.addAll(lp.items);
        } else if(planet.name.equals("serpulo")) {
            rawList.addAll(Items.copper, Items.lead, Items.coal, Items.scrap, Items.sand, Items.titanium, Items.thorium);
        } else if(planet.name.equals("erekir")) {
            rawList.addAll(Items.beryllium, Items.graphite, Items.tungsten, Items.thorium);
        }

        Seq<Item> filteredList = new Seq<>();
        for(Item item : rawList) {
            if(item == null) continue;
            if((item.hardness > 0 || item == Items.sand || item == Items.scrap) && !filteredList.contains(item)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    private Seq<Liquid> getPlanetLiquids(Planet planet) {
        Seq<Liquid> list = new Seq<>();

        if(planet instanceof PLCPlanet lp) {
            list.addAll(lp.liquids);
        } else if(planet.name.equals("serpulo")) {
            list.addAll(Liquids.water, Liquids.oil, Liquids.slag);
        } else if(planet.name.equals("erekir")) {
            list.addAll(Liquids.slag);
        }
        return list;
    }

    public void showFor(Planet planet){
        cont.clear();

        // Автоматически достаем мод, к которому принадлежит планета
        mindustry.mod.Mods.LoadedMod sourceMod = planet.minfo != null ? planet.minfo.mod : null;

        Table mainTable = new Table();
        mainTable.margin(16f).top();
        mainTable.table(titleTable -> {
            titleTable.left();
            
            TextureRegion iconRegion = null;
            if(planet.icon != null && !planet.icon.isEmpty()){
                iconRegion = Core.atlas.find(planet.icon);
            }
            if(iconRegion == null || !iconRegion.found()){
                iconRegion = Core.atlas.find(planet.name, Core.atlas.find("nowhere"));
            }

            if(iconRegion != null && iconRegion.found()){
                titleTable.image(iconRegion).size(40f).padRight(12f);
            }
            
            titleTable.add("[accent]" + planet.localizedName)
                .style(Styles.outlineLabel)
                .fontScale(1.2f);
                
            // Если планета из мода, аккуратно добавляем его displayName в заголовок
            if(sourceMod != null && sourceMod.meta != null) {
                titleTable.add("[gray] | [lightgray]" + sourceMod.meta.displayName)
                    .fontScale(0.9f)
                    .padLeft(8f);
            }
        }).growX().padBottom(12f).row();

        mainTable.table(descTable -> {
            StringBuilder desc = new StringBuilder();
            
            if(planet.name.equals("serpulo")) {
                desc.append("[lightgray]Заброшенный индустриальный мир, покрытый шрамами от войн прошлого. ")
                    .append("Изобилует классическими ресурсами: медью, свинцом и титаном. ")
                    .append("Идеальное место для развертывания масштабных конвейерных сетей.");
            } else if(planet.name.equals("erekir")) {
                desc.append("[lightgray]Расколотая вулканическая планета с экстремальными условиями. ")
                    .append("Вместо привычных конвейеров здесь используются плазменные буры и трубопроводы. ")
                    .append("Атмосфера токсична, а производство завязано на тепловой энергии и газах.");
            } else if(planet instanceof PLCPlanet) {
                desc.append(planet.description != null ? planet.description : "[scarlet]ДАННЫЕ ПОВРЕЖДЕНЫ: [lightgray]Информация о планете не обнаружена в архивах Ядра. \nВозможно, файлы конфигурации были удалены.\n[accent]Рекомендуется экстренный перелет на планету 4546B для синхронизации базы данных!");
            } else {
                desc.append(planet.description != null ? planet.description : Core.bundle.get("planet.no-description"));
            }
            
            // Если планета кастомная (из мода) — добавляем красивую сноску внизу, используя игровой бандл
            if(sourceMod != null && sourceMod.meta != null && !(planet.name.equals("serpulo") || planet.name.equals("erekir"))) {
                desc.append("\n\n[lightgray]")
                    .append(Core.bundle.format("mod.display", sourceMod.meta.displayName))
                    .append(" [gray](v").append(sourceMod.meta.version).append(")");
            }
            
            descTable.add(desc.toString()).wrap().growX().left();
        }).growX().padBottom(16f).row();

        mainTable.image().color(Color.lightGray).height(3f).growX().padBottom(16f).row();

        mainTable.table(stats -> {
            stats.left().defaults().left().padBottom(4f);

            stats.add("[lightgray]" + Core.bundle.get("planet.stats.radius") + ": [white]" + (int)planet.radius);
            stats.row();

            if(planet.parent != null){
                stats.add("[lightgray]" + Core.bundle.get("planet.stats.parent") + ": [accent]" + planet.parent.localizedName);
                stats.row();
            }

            if(planet.sectors != null && planet.sectors.size > 0){
                String threatColor = planet.visible ? "[scarlet]" : "[orange]";
                stats.add("[lightgray]" + Core.bundle.get("planet.stats.sectors") + ": " + threatColor + planet.sectors.size);
                stats.row();
            }
        }).growX().padBottom(16f).row();

        mainTable.table(atmosphereTable -> {
            atmosphereTable.left();
            atmosphereTable.add("@planet.gases-title")
                .style(Styles.outlineLabel)
                .color(Pal.accent)
                .left().row();

            if(planet instanceof PLCPlanet lp){
                if(lp.getAtmosphereGases() == null || lp.getAtmosphereGases().isEmpty()){
                    atmosphereTable.add("@planet.no-gases").color(Color.gray).left().padTop(4f);
                } else {
                    atmosphereTable.table(gTable -> {
                        gTable.left().defaults().left().padTop(4f);
                        
                        lp.getAtmosphereGases().each(g -> {
                            int percent = (int)(g.amount * 100f);
                            
                            if(g.liquid != null && g.liquid.uiIcon != null){
                                gTable.image(g.liquid.uiIcon).size(20f).padRight(6f);
                            }
                            
                            String gasRow = Core.bundle.format("planet.gas-row", g.liquid != null ? g.liquid.localizedName : "Unknown", percent);
                            gTable.add(gasRow).row();
                        });
                    }).padLeft(10f);
                }
            } else {
                atmosphereTable.add("@planet.gases-unavailable").color(Color.gray).left().padTop(4f);
            }
        }).growX().padBottom(16f).row();

        Seq<Item> planetItems = getPlanetItems(planet);
        if(planetItems != null && planetItems.size > 0){
            mainTable.image().color(Color.lightGray).height(1f).growX().padBottom(12f).row();

            mainTable.table(resTable -> {
                resTable.left();
                resTable.add("@planet.resources-title")
                    .style(Styles.outlineLabel)
                    .color(Pal.accent)
                    .left().padBottom(6f).row();

                resTable.table(grid -> {
                    grid.left();
                    int index = 0;
                    
                    for(Item item : planetItems){
                        if(item == null) continue;
                        
                        Table itemNode = new Table();
                        itemNode.left();
                        
                        if(item.uiIcon != null) {
                            itemNode.image(item.uiIcon).size(22f).padRight(6f);
                        }
                        itemNode.add(item.localizedName).fontScale(0.9f).left();

                        grid.add(itemNode).pad(6f).left();

                        index++;
                        if(index % 3 == 0) {
                            grid.row();
                        }
                    }
                }).left();
            }).growX().padBottom(12f).row();
        }

        Seq<Liquid> planetLiquids = getPlanetLiquids(planet);
        if(planetLiquids != null && planetLiquids.size > 0){
            mainTable.image().color(Color.lightGray).height(1f).growX().padBottom(12f).row();

            mainTable.table(liqTable -> {
                liqTable.left();
                liqTable.add(Core.bundle.get("planet.liquids-title", "Жидкости на поверхности:"))
                    .style(Styles.outlineLabel)
                    .color(Pal.accent)
                    .left().padBottom(6f).row();

                liqTable.table(grid -> {
                    grid.left();
                    int index = 0;

                    for(Liquid liquid : planetLiquids){
                        if(liquid == null) continue;

                        Table liquidNode = new Table();
                        liquidNode.left();

                        if(liquid.uiIcon != null) {
                            liquidNode.image(liquid.uiIcon).size(22f).padRight(6f);
                        }
                        liquidNode.add(liquid.localizedName).fontScale(0.9f).left();

                        grid.add(liquidNode).pad(6f).left();

                        index++;
                        if(index % 3 == 0) {
                            grid.row();
                        }
                    }
                }).left();
            }).growX().padBottom(16f).row();
        }

        ScrollPane pane = new ScrollPane(mainTable);
        pane.setFadeScrollBars(false);
        pane.setOverscroll(false, false);

        cont.add(pane).grow().width(500f).maxHeight(600f);

        pack(); 
        show();
    }
}