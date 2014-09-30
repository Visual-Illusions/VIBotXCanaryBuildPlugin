package net.visualillusionsent.canarybuildplugin;

import net.visualillusionsent.vibotx.VIBotX;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aaron on 9/28/2014.
 */
public class HttpHelper {

    // "http://repo.visualillusionsent.net/repository/public/net/canarymod/CanaryMod/"
    private static SAXBuilder fileBuilder = new SAXBuilder();

    public static String getShortUrl(String httplink) {
        String shortUrl = null;
        try {
            URL url = new URL("https://www.googleapis.com/urlshortener/v1/url/");
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type", "application/json");
            uc.setDoOutput(true);
            DataOutputStream dos = new DataOutputStream(uc.getOutputStream());
            JSONObject jsonO = new JSONObject();
            jsonO.put("longUrl", httplink);
            System.out.println(jsonO.toJSONString());
            dos.writeBytes(jsonO.toString());
            dos.flush();
            dos.close();
            // Parse it
            JSONParser parser = new JSONParser();
            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            Object obj = parser.parse(reader);
            shortUrl = (String) ((JSONObject) obj).get("id");
        }
        catch (Exception ex) {
            System.out.println("Failed to translate long URL into short URL");
            ex.printStackTrace();
        }
        return shortUrl;
    }

    public static List<String> getFileNames() throws IOException {
        List<String> filenames = new ArrayList<String>();
        /* Get the Files from the list */
        URL url = new URL("http://repo.visualillusionsent.net/repository/public/net/canarymod/CanaryMod/maven-metadata.xml");

        //BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        Document document = null;
        try {
            document = fileBuilder.build(url.openStream());
        } catch (JDOMException e) {
            VIBotX.log.error("Error opening 'maven-metadata.xml' from VI repository.");
        }
        for (Element element : document.getRootElement().getChild("versioning").getChild("versions").getChildren()) {
            String version = element.getValue();
            if (version.matches("\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}-\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}")) {
                filenames.add(version);
            }
        }

        return filenames;
    }

    /**
     * Get the latest snapshot version from the repo.
     * @return
     * @throws IOException
     */
    public static String getLatestSnapshotVersion() throws IOException {
        /* Get the Files from the list */
        URL url = new URL("http://repo.visualillusionsent.net/repository/public/net/canarymod/CanaryMod/maven-metadata.xml");

        Document document = null;
        try {
            document = fileBuilder.build(url.openStream());
        } catch (JDOMException e) {
            VIBotX.log.error("Error opening 'maven-metadata.xml' from VI repository.");
        }
        Element element = document.getRootElement().getChild("versioning").getChild("latest");

        return element.getValue();
    }

    /**
     * Get the url for the latest snapshot.
     * @return download url
     * @throws IOException
     */
    public static String getLatestSnapshotURL() throws IOException {
        String version = null;
        try {
            version = getLatestSnapshotVersion();
        } catch (IOException e) {
            VIBotX.log.error("Error getting latest Dev Version.", e);
        }
        return getSnapshotURL(version);
    }

    /**
     * Get the url for the given snap shot
     * @param version version to get the snapshot for
     * @return snapshot download url
     * @throws IOException
     */
    public static String getSnapshotURL(String version) throws IOException {
        URL url = new URL(String.format("http://repo.visualillusionsent.net/repository/public/net/canarymod/CanaryMod/%s/maven-metadata.xml", version));
        Document document = null;
        try {
            document = fileBuilder.build(url.openStream());
        } catch (JDOMException e) {
            VIBotX.log.error("Error opening 'maven-metadata.xml' from VI repository.");
        }
        Element element = document.getRootElement().getChild("versioning").getChild("snapshot");
        String modVersion = version.replace("-SNAPSHOT", "");
        String timestamp =  element.getChild("timestamp").getValue();
        String buildNumber =  element.getChild("buildNumber").getValue();
        return String.format("http://repo.visualillusionsent.net/repository/public/net/canarymod/CanaryMod/%s/CanaryMod-%s-%s-%s-shaded.jar", version, modVersion, timestamp, buildNumber);
    }

    /**
     * Get the version for the latest release
     * @return the version
     * @throws IOException
     */
    public static String getLatestReleaseVersion() throws IOException {
        /* Get the Files from the list */
        URL url = new URL("http://repo.visualillusionsent.net/repository/public/net/canarymod/CanaryMod/maven-metadata.xml");

        Document document = null;
        try {
            document = fileBuilder.build(url.openStream());
        } catch (JDOMException e) {
            VIBotX.log.error("Error opening 'maven-metadata.xml' from VI repository.");
        }
        Element element = document.getRootElement().getChild("versioning").getChild("release");

        return element.getValue();
    }

    /**
     * get the download url for the latest release build.
     * @return the download url
     * @throws IOException
     */
    public static String getLatestReleaseURL() throws IOException {
        String version = null;
        try {
            version = getLatestReleaseVersion();
        } catch (IOException e) {
            VIBotX.log.error("Error getting latest version for URL from VI repo");
        }
        return getLatestReleaseURL(version);
    }

    /**
     * get the download url for the given release build.
     * @param version the version to get the url for.
     * @return the download url
     * @throws IOException
     */
    public static String getLatestReleaseURL(String version) {
        return String.format("http://repo.visualillusionsent.net/repository/public/net/canarymod/CanaryMod/%s/CanaryMod-%s-shaded.jar", version, version);
    }
}
