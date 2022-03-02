package cz.ostricker.training.wowApi.cmdline;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Logger;

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
  private static final String CLIENT_ID = "e4ed468bb9a647099060708d87b0cc62";
  private static final String CLIENT_SECRET = "gTismLXiI6CI9PG3rnFjFXcu5GUwe3Fu";
  private static final String TOKEN_URL = "https://us.battle.net/oauth/token";

  private static final Logger logger = Logger.getLogger(BlizzardAPI.class.getSimpleName());
  private static long TokenExpireTime = 0;
  private static JSONObject Token = null;

  /**
   * Vytvoření autentifikačního tokenu, nutné pro každý dotaz
   * @return
   * @throws IOException
   */
  private static JSONObject createToken() throws IOException
  {
    logger.warning("Creating new authentication token");

    final String encoding = "utf-8";

    final String encodedCredentials = Base64.getEncoder().encodeToString(String.format("%s:%s", CLIENT_ID, CLIENT_SECRET).getBytes(encoding));

    final URL url = new URL(TOKEN_URL);
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Authorization", String.format("Basic %s", encodedCredentials));
    connection.setDoOutput(true);
    connection.getOutputStream().write("grant_type=client_credentials".getBytes(encoding));

    int responseCode = connection.getResponseCode();

    final InputStream in = connection.getInputStream();

    if (responseCode != 200)
    {
      final String message = in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
      logger.severe("Failed to authenticate.  responseCode: " + responseCode + ", message=" + message);
      return null;
    }

    final String str = new String(in.readAllBytes(), StandardCharsets.UTF_8);
    Token           = new JSONObject(str);
    TokenExpireTime = System.currentTimeMillis() + Long.parseLong(Token.get("expires_in").toString()) - 5000L;

    return Token;
  }

  /**
   * Získání tokenu
   * @return
   * @throws IOException
   */
  public static JSONObject getToken() throws IOException
  {

    logger.fine("get");

    if (TokenExpireTime > System.currentTimeMillis())
    {
      return createToken();
    }

    if (Token == null)
    {
      return createToken();
    }
    else
    {
      return Token;
    }
  }

  /**
   * Get požadavek
   * @param uri
   * @param namespace
   * @param locale
   * @return
   * @throws IOException
   */
  public static String get(final String uri, final String namespace, final String locale) throws IOException
  {
    JSONObject token = getToken();

    if (token == null)
    {
      return null;
    }

    final StringBuilder builder = new StringBuilder();
    builder.append(uri);
    if (uri.contains("?"))
    {
      builder.append("&");
    }
    else
    {
      builder.append("?");
    }
    builder.append("namespace=").append(namespace);
    builder.append("&locale=").append(locale);
    builder.append("&access_token=").append(token.getString("access_token"));

    logger.info(builder.toString());
    final URL url = new URL(builder.toString());
    final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    int responseCode = connection.getResponseCode();

    if (responseCode != 200)
    {
      logger.severe("Failed to request data." + ", uri=" + uri + ", responseCode=" + responseCode);
      return null;
    }

    final InputStream in = connection.getInputStream();
    return new String(in.readAllBytes(), StandardCharsets.UTF_8);
  }

  /**
   * Test služby
   * @param args
   */
  public static void main(String[] args)
  {
    final String namespace = "static-classic-us";
    final String locale = "en_US";
    final String baseURL = "https://us.api.blizzard.com";
    final String[] testPaths = {"/data/wow/item-class/index", "/data/wow/media/item/19019", "/data/wow/creature/30"};

    try
    {
      for (final String path : testPaths)
      {
        final String uri = baseURL + path;
        final String result = get(uri, namespace, locale);
        final JSONObject object = new JSONObject(result);
        logger.info(uri + " ===> " + object.toString(2));
      }

    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
