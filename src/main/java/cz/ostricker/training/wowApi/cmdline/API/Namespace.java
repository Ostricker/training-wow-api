package cz.ostricker.training.wowApi.cmdline.API;

public enum Namespace
{
  STATIC_EU("static-eu"),
  DYNAMIC_EU("dynamic-eu"),
  PROFILE_EU("profile-eu");

  public final String text;

  Namespace(String text)
  {
    this.text = text;
  }

  public String getText()
  {
    return text;
  }

  @Override
  public String toString()
  {
    return getText();
  }
}
