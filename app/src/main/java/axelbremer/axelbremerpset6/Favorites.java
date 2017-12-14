package axelbremer.axelbremerpset6;

import java.util.ArrayList;
import java.util.List;

/**
 * This class makes a Favorites object. Favorites consist of a list of names for showing the list
 * and an id list to access the api with.
 */

public class Favorites {
    List<String> nameList;
    List<String> idList;


    /**
     * Makes a new Favorites list with a book to start. Because firebase doesn't allow an empty
     * list to be saved.
     */
    public Favorites(){
        nameList = new ArrayList<>();
        idList = new ArrayList<>();
        idList.add("XjYQCwAAQBAJ");
        nameList.add("Harry Potter en de Relieken van de Dood");
    }

    /**
     * Makes a new Favorites object with a given name and id list.
     */
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

    /**
     * Adds a volume to the favorites given a name and an id.
     */
    public void addVolume(String name, String id){
        nameList.add(name);
        idList.add(id);
    }
}
