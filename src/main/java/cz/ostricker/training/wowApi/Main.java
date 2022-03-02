package cz.ostricker.training.wowApi;

import cz.ostricker.training.wowApi.cmdline.CmdLine;

public class Main
{
  public static void main(String[] args)
  {
    // Spouští třídu CmdLine
    CmdLine cmdLineProgram = new CmdLine();
    cmdLineProgram.runProgram();
  }
}