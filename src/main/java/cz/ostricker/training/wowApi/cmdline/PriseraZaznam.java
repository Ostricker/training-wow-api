package cz.ostricker.training.wowApi.cmdline;

public class PriseraZaznam {
    private boolean tameable;
    private String name;
    private int id;
    private String typeName;
    private int typeID;

    public boolean isTameable() {
        return tameable;
    }

    public void setTameable(boolean tameable) {
        this.tameable = tameable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }
}
