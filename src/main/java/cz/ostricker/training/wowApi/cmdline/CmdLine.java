package cz.ostricker.training.wowApi.cmdline;

import cz.ostricker.training.wowApi.PriseraZaznam;
import cz.ostricker.training.wowApi.WoWAPI;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;

import java.awt.*;

public class CmdLine
{
  // Statické proměnné
  private final static String BOOKMARK_MAINLOOP = "BOOKMARK_MAINLOOP";

  private final TextIO textIO;
  private CmdCommands currentCommand;
  private final WoWAPI wowAPI;

  /**
   * Konstruktor CmdLine
   */
  public CmdLine()
  {
    // Vytvoření terminálu
    textIO = TextIoFactory.getTextIO();

    // Iniciální nastavení terminálu
    setupProperties();

    this.wowAPI = WoWAPI.getInstance();
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
      currentCommand = CmdCommands.parseCommand(textIO.newStringInputReader().withDefaultValue("infocr").read(lineStart));

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
          case INFO_CREATURE:
            zpracujINFO_CREATURE();
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
    if (!wowAPI.creatureSEARCH(nazevPrisery))
    {
      textIO.getTextTerminal().print(wowAPI.getTextChyba());
      return;
    }

    // Cyklus ve kterem vypiseme ziskane informace z JSOBObjektů za kazdou priseru v listPriser
    for (PriseraZaznam priseraZaznam : wowAPI.getListPriser())
    {
      // Vypsání informací o příšeře
      textIO.getTextTerminal().print(
        "----------- ID: " + priseraZaznam.getId() + " | " + priseraZaznam.getName() + " -----------\n"
        + "Typ: " + priseraZaznam.getTypeName() + "\n"
        + "Je tameable: " + priseraZaznam.isTameable() + "\n");
    }
  }

  /**
   * Volání blizzard API
   */
  private void zpracujINFO_CREATURE()
  {
    // Získání názvu příšery
    String idPrisery = textIO.newStringInputReader().read("ID příšery>");
    PriseraZaznam priseraZaznam = wowAPI.creatureINFO(idPrisery);
    if (priseraZaznam == null)
    {
      textIO.getTextTerminal().print(wowAPI.getTextChyba());
      return;
    }

    // Vypsání informací o příšeře
    textIO.getTextTerminal().print(
      "----------- ID: " + priseraZaznam.getId() + " | " + priseraZaznam.getName() + " -----------\n"
      + "Typ: " + priseraZaznam.getTypeName() + "\n"
      + "Je tameable: " + priseraZaznam.isTameable() + "\n");
  }

  public WoWAPI getWowAPI()
  {
    return wowAPI;
  }

  public static void main(String[] args)
  {
    // Spouští třídu CmdLine
    CmdLine cmdLineProgram = new CmdLine();
    cmdLineProgram.runProgram();
  }
}


