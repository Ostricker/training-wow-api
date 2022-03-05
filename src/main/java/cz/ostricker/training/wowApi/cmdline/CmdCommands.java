package cz.ostricker.training.wowApi.cmdline;

public enum CmdCommands
{
  CREATURE("CREATURE", "creature", "Získání informací o příšeře"),
  CLEAR("CLEAR", "clear", "\tVyčistí terminál"),
  HELP("HELP", "help", "\tVypíše help název všech příkazů"),
  QUIT("QUIT", "quit", "\tUkončí program");

  public final String text;
  public final String command;
  public final String helpText;

  CmdCommands(String text, String command, String helpText)
  {
    this.text     = text;
    this.command  = command;
    this.helpText = helpText;
  }

  /**
   * Parse nazev pro získání typu CmdCommands
   *
   * @param command - String param
   * @return - CmdCommands
   */
  public static CmdCommands parseCommand(String command)
  {
    for (CmdCommands cmdCommands : values())
    {
      if (cmdCommands.command.equalsIgnoreCase(command))
      {
        return cmdCommands;
      }
    }
    return null;
  }

  public String getText()
  {
    return text;
  }

  public String getCommand()
  {
    return command;
  }

  public String getHelpText()
  {
    return helpText;
  }
}
