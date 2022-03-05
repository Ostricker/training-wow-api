package cz.ostricker.training.wowApi.cmdline.API;

public enum REST
{
  DELETE("DELETE"),
  GET("GET"),
  POST("POST"),
  PUT("PUT");

  public final String text;

  REST(String text)
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
