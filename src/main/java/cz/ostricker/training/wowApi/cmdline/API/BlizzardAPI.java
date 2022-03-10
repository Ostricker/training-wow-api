package cz.ostricker.training.wowApi.cmdline.API;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static cz.ostricker.training.wowApi.cmdline.API.REST.POST;

/**
 * @author Afterimage
 * / Earthfury
 * / Mal'ganis
 *
 * For this app to work, you need to download and include in your classpath
 * json.jar which can be downloaded from https://github.com/stleary/JSON-java
 * (direct link https://search.maven.org/remotecontent?filepath=org/json/json/20210307/json-20210307.jar)
 *
 * You will also have to enter your own CLIENT_ID & CLIENT_SECRET obtained through
 * blizzard OAuth service (https://develop.battle.net/documentation/guides/getting-started)
 *
 * WOW - https://develop.battle.net/documentation/world-of-warcraft/game-data-apis
 */
public class BlizzardAPI
{
  // Paměť kde se drží jediná isntance této třídy
  private static BlizzardAPI blizzardAPI;

  // Informace z BlizzardAPI
  private static final String CLIENT_ID = "e4ed468bb9a647099060708d87b0cc62";
  private static final String CLIENT_SECRET = "gTismLXiI6CI9PG3rnFjFXcu5GUwe3Fu";
  private static final String TOKEN_URL = "https://us.battle.net/oauth/token";

  // Base url pro BlizzardAPI
  public static final String BASE_URL = "https://eu.api.blizzard.com";
  public static final String en_GB = "en_GB";

  // Statická proměnná TOKENU -> pokud existuje, tak nemusím vytvářet další - informace k tokenu
  private static JSONObject TOKEN = null;
  private long TOKEN_EXPIRE_TIME = 0;

  /**
   * Privátní konstruktor -> nelze vytvořit novou instanci jinak, než z metody getInstance (Singleton Třída)
   */
  private BlizzardAPI()
  {}

  /**
   * Vrací jedinou instanci třídy XML_TPlanVecnaSkupina
   * @return
   */
  public static BlizzardAPI getInstance()
  {
    // Kontrola, jestli existuje instance v paměti
    if (blizzardAPI == null)
    {
      blizzardAPI = new BlizzardAPI();
    }

    return blizzardAPI;
  }

  /**
   * Vytvoření autentifikačního tokenu, nutné pro každý dotaz
   * @return
   * @throws IOException
   */
  private JSONObject createToken() throws IOException
  {
    System.out.println("BlizzardAPI.createToken: Vytváření autentifikačního tokenu");

    // Zpracování Credentials do Stringu Base64
    final String encodedCredentials = Base64.getEncoder().encodeToString(String.format("%s:%s", CLIENT_ID, CLIENT_SECRET).getBytes(StandardCharsets.UTF_8));

    // Vytvoření POST volání
    final URL url = new URL(TOKEN_URL);
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod(POST.toString());
    connection.setRequestProperty("Authorization", String.format("Basic %s", encodedCredentials));
    connection.setDoOutput(true);
    connection.getOutputStream().write("grant_type=client_credentials".getBytes(StandardCharsets.UTF_8));

    // Připojení a získání response code - Pokud kod není OK, tak zkusím načíst co mám
    int responseCode = connection.getResponseCode();
    if (responseCode != HttpStatusCode.OK.getValue())
    {
      final String message = connection.getInputStream() == null ? "" : IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
      System.out.println("CHYBA: Chyba při autentifikaci. ResponseCode: " + responseCode + ", Message=" + message);
      return null;
    }

    // Jinak načítám token
    final String str = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
    TOKEN             = new JSONObject(str);
    TOKEN_EXPIRE_TIME = System.currentTimeMillis() + Long.parseLong(TOKEN.get("expires_in").toString()) - 5000L;

    return TOKEN;
  }

  /**
   * Získání tokenu
   * @return
   * @throws IOException
   */
  private JSONObject getToken() throws IOException
  {
    // Pokud už expiroval nebo nemám, tak vytvářím nový
    if (TOKEN == null || TOKEN_EXPIRE_TIME > System.currentTimeMillis())
    {
      return createToken();
    }

    return TOKEN;
  }

  /**
   * Metoda GET, která nezpracovává locale -> zavolá se předaný string tak jak je
   * @param sURL
   * @param namespace
   * @return
   */
  public static String GET_RAW(String sURL, Namespace namespace)
  {
    try
    {
      return getInstance().requestGET(sURL, namespace);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

  public static String GET(String sURL, Namespace namespace)
  {
    return BlizzardAPI.GET(sURL, null, namespace);
  }

  /**
   * Vrací jedinou instanci třídy XML_TPlanVecnaSkupina
   * @return
   */
  public static String GET(String sURL, String suffix, Namespace namespace)
  {
    try
    {
      // Zpracování URL
      String kompletniURL = sURL + "?locale=" + en_GB + (suffix != null ? suffix : "");
      return getInstance().requestGET(kompletniURL, namespace);
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      return null;
    }
  }

  /**
   * Get požadavek
   * @param sURL
   * @param namespace
   * @return
   * @throws IOException
   */
  private String requestGET(String sURL, Namespace namespace) throws IOException
  {
    // Získání tokenu pro
    JSONObject token = getToken();
    if (token == null)
    {
      System.out.println("CHYBA: Chyba při získání validního autentifikačního tokenu - kontaktuj správce aplikace");
      return null;
    }

    // Vypsání sestaveného požadavku
    System.out.println("BlizzardAPI.requestGET URL: " + sURL);

    // Vytvoření připojení
    final URL url = new URL(sURL);
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Battlenet-Namespace", namespace.getText());
    connection.setRequestProperty("Authorization", "Bearer " + token.getString("access_token"));

    // Připojení a získání response code - Pokud kod není OK, tak zkusím načíst co mám
    int responseCode = connection.getResponseCode();
    if (responseCode != HttpStatusCode.OK.getValue())
    {
      System.out.println("CHYBA: Chyba při získání dat. ResponseCode: " + responseCode + ": " + HttpStatusCode.getByValue(responseCode).getDescription());
      return null;
    }
    
    // Vrácení odpovědi
    return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
  }

  /**
   * Test služby
   * @param args
   */
  public static void main(String[] args)
  {
    final String locale = "en_US";
    final String baseURL = "https://us.api.blizzard.com";
    final String[] testPaths = {"/data/wow/item-class/index", "/data/wow/media/item/19019", "/data/wow/creature/30"};

    try
    {
      for (final String path : testPaths)
      {
        final String uri = baseURL + path;
        final String result = BlizzardAPI.GET(uri, Namespace.STATIC_EU);
        final JSONObject object = new JSONObject(result);
        System.out.println(uri + " ===> " + object.toString(2));
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
