package axelbremer.axelbremerpset6;


/**
 * The Volume class contains information for 1 Volume.
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
