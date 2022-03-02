package cz.ostricker.training.wowApi.cmdline;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
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
      currentCommand = CmdCommands.parseCommand(textIO.newStringInputReader().read(lineStart));

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
          case BLIZ:
            callBlizzardAPI();
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
  private void callBlizzardAPI()
  {
    final String namespace = "static-eu";
    final String locale = "en_US";
    final String baseURL = "https://eu.api.blizzard.com";
    final String[] testPaths = {"/data/wow/creature/30"};

    try
    {

      for (final String path : testPaths)
      {
        final String uri = baseURL + path;
        final String result = BlizzardAPI.get(uri, namespace, locale);
        final JSONObject object = new JSONObject(result);
        textIO.getTextTerminal().print(uri + " ===> " + object.toString(2));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
