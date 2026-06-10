package plc;

import arc.util.*;
import mindustry.mod.*;

public class PLCLib extends Mod {

    public PLCLib(){
        // Лог в консоль, чтобы при запуске игры было видно, что библиотека успешно подгрузилась
        Log.info("[PLC] PlanetDataCore library initialized.");
    }

    @Override
    public void loadContent(){
        // Сюда можно ничего не писать, если весь контент (планеты) 
        // будет создаваться внутри твоих основных модов (LAI, FoS)
        Log.info("[PLC] Loading PlanetDataCore content structures.");
    }
}