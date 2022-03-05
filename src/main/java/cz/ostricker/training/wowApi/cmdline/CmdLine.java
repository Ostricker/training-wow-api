package cz.ostricker.training.wowApi.cmdline;

import cz.ostricker.training.wowApi.cmdline.API.BlizzardAPI;
import cz.ostricker.training.wowApi.cmdline.API.Namespace;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;

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
          case CREATURE:
            zpracujCREATURE();
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
  private void zpracujCREATURE()
  {
    // Získání názvu příšery
    String nazevPrisery = textIO.newStringInputReader().withDefaultValue("dragon").read("Název příšery>");

    // Vytvoření URL
    String baseURL = BlizzardAPI.BASE_URL;
    String path = "/data/wow/search/creature";
    String extra = "&name.en_GB=" + nazevPrisery + "&orderby=id&_page=1";

    // Zavolání URL
    String result = BlizzardAPI.GET(baseURL + path, Namespace.STATIC_EU, extra);
    if (result == null)
    {
      textIO.getTextTerminal().print("Příšera nebyla nalezena!\n");
      return;
    }

    try
    {
      textIO.getTextTerminal().print("Příšera nalezena - zpracování\n");
      // Zpracování objektu
      JSONObject object = new JSONObject(result);

      // 1) Z JSON objektu získej jména všech příšer v en_GB lokalizaci
      // 2) tyto jména vypiš do aplikace

      System.out.println(baseURL + path + " ===> " + object.toString(2));
    }
    catch (JSONException ex)
    {
      ex.printStackTrace();
    }
  }
}
