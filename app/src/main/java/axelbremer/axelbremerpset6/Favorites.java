package axelbremer.axelbremerpset6;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel Bremer on 11-12-2017.
 */

public class Favorites {
    List<String> nameList;
    List<String> idList;

    public Favorites(){
        nameList = new ArrayList<>();
        idList = new ArrayList<>();
        idList.add("XjYQCwAAQBAJ");
        nameList.add("Harry Potter en de Relieken van de Dood");
    }

    public Favorites(List<String> aNameList, List<String> anIdList){
        nameList = aNameList;
        idList = anIdList;
    }

    public List<String> getNameList(){
        return nameList;
    }

    public List<String> getIdList(){
        return idList;
    }

    public void addVolume(String name, String id){
        nameList.add(name);
        idList.add(id);
    }
}
