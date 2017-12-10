package axelbremer.axelbremerpset6;

import java.util.List;

/**
 * Created by Axel Bremer on 10-12-2017.
 */

public class Volume {
    String title;
    String authors;
    String id;
    String desc;
    String imageUrl;

    public Volume(String aTitle, String anAuthors, String anId, String aDesc, String anImageUrl){
        title = aTitle;
        authors = anAuthors;
        id = anId;
        desc = aDesc;
        imageUrl = anImageUrl;
    }

    public String getTitle(){
        return title;
    }

    public String getAuthors(){
        return authors;
    }

    public String getId(){
        return id;
    }

    public String getDesc(){
        return desc;
    }

    public String getImageUrl(){
        return imageUrl;
    }
}
