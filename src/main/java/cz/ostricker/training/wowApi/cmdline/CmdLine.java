package cz.ostricker.training.wowApi.cmdline;

import cz.ostricker.training.wowApi.cmdline.API.BlizzardAPI;
import cz.ostricker.training.wowApi.cmdline.API.Namespace;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.ArrayList;

public class CmdLine
{
  // Statické proměnné
  private final static String BOOKMARK_MAINLOOP = "BOOKMARK_MAINLOOP";

  private final TextIO textIO;
  private CmdCommands currentCommand;

  /**
   * Konstruktor CmdLine
   */
  public CmdLine()
  {
    // Vytvoření terminálu
    textIO = TextIoFactory.getTextIO();

    // Iniciální nastavení terminálu
    setupProperties();
  }

  /**
   * Spuštění programu
   */
  public void runProgram()
  {
    // Aplikace spuštěna -> informace
    textIO.getTextTerminal().println("Aplikace 'Training WoW API' úspěšně spuštěna");

    // Informace o Helpu
    textIO.getTextTerminal().println("Pro výpis příkazů použijte 'help'");

    // Nastavení bookmarku pro reset
    textIO.getTextTerminal().setBookmark(BOOKMARK_MAINLOOP);

    // Obsluha terminálu
    while (currentCommand != CmdCommands.QUIT)
    {
      // Začátek řádku
      String lineStart = ">";

      // Parse aktuálního příkazu -- Zde se čeká většinu času na příkaz
      currentCommand = CmdCommands.parseCommand(textIO.newStringInputReader().withDefaultValue("creature").read(lineStart));

      // Pokud je currentCommand null
      if (currentCommand == null)
      {
        textIO.getTextTerminal().println("Neznámý příkaz. Pro výpis příkazů použijte 'help'");
      }
      else
      {
        // Jinak spouštím switch podle aktuálního příkazu
        switch (currentCommand)
        {
          case SEARCH_CREATURE:
            zpracujSEARCH_CREATURE();
            break;
          case QUIT:
            break;
          case HELP:
            printCommands();
            break;
          case CLEAR:
            textIO.getTextTerminal().resetToBookmark(BOOKMARK_MAINLOOP);
            break;
          default:
            textIO.getTextTerminal().print("Chyba při parsu commandu. Kontaktujte správce aplikace\n");
        }
      }
    }

    // Zavření aplikace
    textIO.dispose();
    System.exit(0);
  }

  /**
   * Metoda pro vypsání použitelných příkazů
   */
  private void printCommands()
  {
    textIO.getTextTerminal().print("Použtelné příkazy:\n");

    for (CmdCommands command: CmdCommands.values())
    {
      textIO.getTextTerminal().print(command.command +"\t" + command.helpText + "\n");
    }
  }

  /**
   * Nastavení grafiky properties
   */
  private void setupProperties()
  {
    // Barva pozadí
    Color backgroundColor = Color.BLACK;
    textIO.getTextTerminal().getProperties().setPaneBackgroundColor(backgroundColor);
    textIO.getTextTerminal().getProperties().setInputBackgroundColor(backgroundColor);
    textIO.getTextTerminal().getProperties().setPromptBackgroundColor(backgroundColor);

    // Barva textu
    textIO.getTextTerminal().getProperties().setInputColor(Color.LIGHT_GRAY);
    textIO.getTextTerminal().getProperties().setPromptColor(Color.WHITE);
  }

  /**
   * Volání blizzard API
   */
  private void zpracujSEARCH_CREATURE()
  {
    // Získání názvu příšery
    String nazevPrisery = textIO.newStringInputReader().withDefaultValue("dragon").read("Název příšery>");

    // Vytvoření URL
    String baseURL = BlizzardAPI.BASE_URL;
    String path = "/data/wow/search/creature";
    String suffix = "&name.en_GB=" + nazevPrisery + "&orderby=id&_page=1";

    // Zavolání URL
    String result = BlizzardAPI.GET(baseURL + path, suffix, Namespace.STATIC_EU);
    if (result == null)
    {
      textIO.getTextTerminal().print("Příšera nebyla nalezena!\n");
      return;
    }

    try
    {
      textIO.getTextTerminal().print("Příšera nalezena - zpracování\n");

      // Zpracování objekt
      JSONObject object = new JSONObject(result);

      // Získání objektu Results
      JSONArray listResults = object.getJSONArray("results");

      // Vytvoření prázdného listu příšer
      ArrayList<PriseraZaznam> listPriser = new ArrayList<>();

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
        // Zapsání získaných dat z JSONObjektů do instance listPriser
        listPriser.add(zaznam);
      }

      // Cyklus ve kterem vypiseme ziskane informace z JSOBObjektů za kazdou priseru v listPriser
      for (PriseraZaznam prisera : listPriser)
      {
        textIO.getTextTerminal().print(
          "Jmeno: " + prisera.getName() + ", typ: " + prisera.getTypeName() + ", jeTameable:" + prisera.isTameable() + ", idPrisery: "
          + prisera.getId() + "\n");
      }
    }
    catch (JSONException ex)
    {
      ex.printStackTrace();
    }
  }
}
