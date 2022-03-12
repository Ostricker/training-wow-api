package cz.ostricker.training.wowApi;

import cz.ostricker.training.wowApi.cmdline.API.BlizzardAPI;
import cz.ostricker.training.wowApi.cmdline.API.Namespace;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WoWAPI
{
  // Paměť kde se drží jediná isntance této třídy
  private static WoWAPI wowAPI;

  // Aktuální list příšer
  private final ObservableList<PriseraZaznam> listPriser = FXCollections.observableArrayList();

  // Chybový kod
  private String textChyba;

  /**
   * Konstruktor
   */
  private WoWAPI()
  {
  }

  /**
   * Vrací jedinou instanci třídy WoWAPI
   * @return
   */
  public static WoWAPI getInstance()
  {
    // Kontrola, jestli existuje instance v paměti
    if (wowAPI == null)
    {
      wowAPI = new WoWAPI();
    }

    return wowAPI;
  }

  /**
   * Volání blizzard API
   */
  public boolean creatureSEARCH(String nazevPrisery)
  {
    // Vyčištění listu na začátku zpracování searche
    listPriser.clear();

    // Vytvoření URL
    String baseURL = BlizzardAPI.BASE_URL;
    String path = "/data/wow/search/creature";
    String suffix = "&name.en_GB=" + nazevPrisery + "&orderby=id&_page=1";

    // Zavolání URL
    String result = BlizzardAPI.GET(baseURL + path, suffix, Namespace.STATIC_EU);
    if (result == null)
    {
      textChyba = "Příšera nebyla nalezena!";
      return false;
    }

    try
    {
      System.out.println("WoWAPI.zpracujSEARCH_CREATURE: Příšera nalezena - zpracování");

      // Zpracování objekt
      JSONObject object = new JSONObject(result);

      // Získání objektu Results
      JSONArray listResults = object.getJSONArray("results");

      // For loop objektem "results" (JSONArray)
      for (int i = 1; i < listResults.length(); i++)
      {
        // Získání aktálního objektu z listu
        JSONObject prisera = listResults.getJSONObject(i);
        // Získání objektu data
        JSONObject data = prisera.getJSONObject("data");

        // Vytvoření našeho nového objektu PriseraZaznam
        PriseraZaznam zaznam = new PriseraZaznam();
        // Získání objektu name
        JSONObject name = data.getJSONObject("name");
        // Získání název z JSONObject name podle klíče engb a vkládáme do setName (set metoda pro promenou ve tride PriseraZaznam)
        zaznam.setName(name.getString("en_GB"));
        // Získání is_tameable z JSONObject data podle klíče is_tameable a vkládáme do setTameable (set metoda pro promenou ve tride PriseraZaznam)
        zaznam.setTameable(data.getBoolean("is_tameable"));
        // Získání Id z JSONObject data podle klíče id a vkládáme do setTameable (set metoda pro promenou ve tride PriseraZaznam)
        zaznam.setId(data.getInt("id"));
        // Získání objektu type
        JSONObject type = data.getJSONObject("type");
        // Získání obektu typename
        JSONObject typename = type.getJSONObject("name");
        // Získání Typename z JSONObject typeNme podle klíče EN_GB a vkládáme do setTypename (set metoda pro promenou ve tride PriseraZaznam)
        zaznam.setTypeName(typename.getString("en_GB"));
        // Získání TypeID z JSONObject type podle klíče id a vkládáme do setTypeID (set metoda pro promenou ve tride PriseraZaznam)
        zaznam.setTypeID(type.getInt("id"));

        // TODO doplnit zaznam.setUrlCreature a naplnit hodnotou z prisera -> "key" -> value -> "href" -> value

        // Zapsání získaných dat z JSONObjektů do instance listPriser
        listPriser.add(zaznam);
      }

      return true;
    }
    catch (JSONException ex)
    {
      ex.printStackTrace();
      textChyba = ex.getMessage();
      return false;
    }
  }

  /**
   * Volání blizzard API
   */
  public PriseraZaznam creatureINFO(String idPrisery)
  {
    // Vytvoření nového záznamu příšery
    PriseraZaznam priseraZaznam = new PriseraZaznam();

    // Vytvoření URL volání
    String baseURL = BlizzardAPI.BASE_URL;
    String path = "/data/wow/creature/" + idPrisery;

    // Volábní blizzard API
    String result = BlizzardAPI.GET(baseURL + path, Namespace.STATIC_EU);
    if (result == null)
    {
      // Pokud je prázdný result, tak vracím null
      textChyba = "Příšera nebyla nalezena!";
      return null;
    }

    // Zpracování objekt
    JSONObject object = new JSONObject(result);
    System.out.println(object);

    // TODO Zpracovat objekt a naplnit priseraZaznam viz. předchozí volání
    // TODO včetně všech URL

    return priseraZaznam;
  }

  public String getTextChyba()
  {
    return textChyba;
  }

  public ObservableList<PriseraZaznam> getListPriser()
  {
    return listPriser;
  }
}
