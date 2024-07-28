package qa.ozhegov.model;

public class Recipe {

    private String id;
    private String type;
    private String name;
    private BattersInner batters;

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public BattersInner getBatters() {
        return batters;
    }

}
