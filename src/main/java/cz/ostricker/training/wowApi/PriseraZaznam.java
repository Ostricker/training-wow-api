package cz.ostricker.training.wowApi;

public class PriseraZaznam
{
  // SEARCH CREATURE -> "key" -> value -> "href" -> value
  // INFO CREATURE -> "link"
  private String urlCreature;

  // INFO CREATURE -> "creature_displays" -> (list.get(0) != null) tak pokračuj -> "key" -> "href" -> value
  private String urlCreatureDisplays;

  // INFO CREATURE -> "type" -> "key" -> "href"
  private String urlType;

  private boolean tameable;
  private String name;
  private int id;
  private String typeName;
  private int typeID;

  public boolean isTameable()
  {
    return tameable;
  }

  public void setTameable(boolean tameable)
  {
    this.tameable = tameable;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public int getId()
  {
    return id;
  }

  public void setId(int id)
  {
    this.id = id;
  }

  public String getTypeName()
  {
    return typeName;
  }

  public void setTypeName(String typeName)
  {
    this.typeName = typeName;
  }

  public int getTypeID()
  {
    return typeID;
  }

  public void setTypeID(int typeID)
  {
    this.typeID = typeID;
  }

  public String getUrlCreature()
  {
    return urlCreature;
  }

  public void setUrlCreature(String urlCreature)
  {
    this.urlCreature = urlCreature;
  }

  public String getUrlCreatureDisplays()
  {
    return urlCreatureDisplays;
  }

  public void setUrlCreatureDisplays(String urlCreatureDisplays)
  {
    this.urlCreatureDisplays = urlCreatureDisplays;
  }

  public String getUrlType()
  {
    return urlType;
  }

  public void setUrlType(String urlType)
  {
    this.urlType = urlType;
  }
}
