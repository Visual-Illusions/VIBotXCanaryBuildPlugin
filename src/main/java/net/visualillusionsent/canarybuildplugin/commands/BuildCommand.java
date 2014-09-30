package net.visualillusionsent.canarybuildplugin.commands;

/**
 * Created by Aaron on 9/24/2014.
 */

import net.visualillusionsent.canarybuildplugin.HttpHelper;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.BaseCommand;
import net.visualillusionsent.vibotx.api.command.BotCommand;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.command.CommandEvent;
import net.visualillusionsent.vibotx.api.plugin.Plugin;
import org.pircbotx.Colors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Shut The Fuck Up Command<br>
 * Quiets the {@link net.visualillusionsent.vibotx.VIBotX} in a {@link org.pircbotx.Channel}<br>
 * <b>Usage:</b>!tell <username> <message><br>
 * <b>Minimum Params:</b> 0<br>
 * <b>Maximum Params:</b> 2<br>
 * <b>Requires:</b> <br>
 *
 * @author Aaron (somners)
 */
@BotCommand(
        main = "build",
        prefix = '!',
        usage = "!build <version number|'release'|'dev'>",
        desc = "Returns a download link to a release, dev, or specified version of Canary.",
        minParam = 0,
        maxParam = 1,
        op = false,
        privateAllowed = false
)
public final class BuildCommand extends BaseCommand {

    /**
     * Constructs a new {@code ShutTheFuckUpCommand}
     */
    public BuildCommand(Plugin plugin) throws CommandCreationException {
        super(plugin);
    }

    @Override
    public final synchronized boolean execute(CommandEvent event) {
        String arg = event.getArgument(0);
        if (arg == null || arg.trim().isEmpty()) {
            /* get a list of versions and message the player */
            List<String> list = null;
            try {
                list = HttpHelper.getFileNames();
            } catch (IOException e) {
                VIBotX.log.error("Error getting file list from server.", e);
            }
            event.getChannel().send().message(event.getUser(), "List of valid versions: " + listToString(list));
        }
        else if (arg.equalsIgnoreCase("dev")) {
            /* get the latest 'dev' version and message the player */
            String version = null;
            String url = null;
            try {
                version = HttpHelper.getLatestSnapshotVersion();
                url = HttpHelper.getSnapshotURL(version);
            } catch (IOException e) {
                VIBotX.log.error("Error getting latest url.", e);
            }

            String shortUrl = HttpHelper.getShortUrl(url);
            String[] versions = version.split("-");
            event.getChannel().send().message(event.getUser(), Colors.NORMAL + "Latest CanaryMod SNAPSHOT Version " + Colors.DARK_GREEN
                    + versions[1] + Colors.NORMAL + " Minecraft Version " + Colors.DARK_GREEN
                    + versions[0] + Colors.NORMAL  + ": " + shortUrl);
        }
        else if (arg.equalsIgnoreCase("release") || arg.equalsIgnoreCase("latest") || arg.equalsIgnoreCase("last")) {
            String version = null;
            String url = null;
            /* get the latest release url */
            try {
                version = HttpHelper.getLatestReleaseVersion();
                url = HttpHelper.getLatestReleaseURL(version);
            } catch (IOException e) {
                VIBotX.log.error("Error getting latest url.", e);
            }
            /* get the short url */
            String shortUrl = HttpHelper.getShortUrl(url);
            /* message the player */
            String[] versions = version.split("-");
            event.getChannel().send().message(event.getUser(), Colors.NORMAL + "Latest CanaryMod Release Version " + Colors.DARK_GREEN
                    + versions[1] + Colors.NORMAL + " Minecraft Version " + Colors.DARK_GREEN
                    + versions[0] + Colors.NORMAL  + ": " + shortUrl);

        } else {
            /* get all versions matching the 'arg' */
            List<String> matching = this.getMatchingVersions(arg);
            /* If nothing returns, tell them */
            if (matching.size() == 0) {
                event.getChannel().send().message(event.getUser(), Colors.NORMAL + "No CanaryMod builds matching " + Colors.DARK_GREEN
                        + arg + Colors.NORMAL + ". Try using '!build' to view available Canary Builds.");
                return true;
            }
            /* check if any of these versions is an exact match */
            String matched = null;
            for (String version : matching) {
                if (version.equals(arg)) {
                    matched = version;
                    break;
                }
            }
            /* we had an exact match, return the build link */
            if (matched != null) {
                String longUrl = HttpHelper.getLatestReleaseURL(matched);
                String shortUrl = HttpHelper.getShortUrl(longUrl);
                String[] versions = matched.split("-");
                event.getChannel().send().message(event.getUser(), Colors.NORMAL + "CanaryMod Release Version " + Colors.DARK_GREEN
                        + versions[1] + Colors.NORMAL + " Minecraft Version " + Colors.DARK_GREEN
                        + versions[0] + Colors.NORMAL  + ": " + shortUrl);
                return true;
            }
            /* no exact build, give them a list of similar versions */
            event.getChannel().send().message(event.getUser(), "List of versions matching " +
                    Colors.DARK_GREEN + arg + Colors.NORMAL + ": " + listToString(matching));

        }


        return true;
    }

    /**
     * Converts a list to a string that can be sent as an irc message.
     * 
     * @param list list to convert
     * @return formatted string
     */
    public String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(", ").append(s);
        }
        if (sb.length() > 2 ) {
            sb.replace(0, 1, "");
        }
        return sb.toString();
    }

    /**
     * Get all versions matching substring.
     * 
     * @param substring version to match.
     * @return list of matchinv versions.
     */
    public List<String> getMatchingVersions(String substring) {
        List<String> matching = new ArrayList<String>();
        List<String> versions = null;
        try {
            versions = HttpHelper.getFileNames();
        } catch (IOException e) {
            VIBotX.log.error("Error getting version list while matching versions.", e);
        }
        for (String version : versions) {
            if (version.contains(substring)) {
                matching.add(version);
            }
        }
        return matching;
    }
}
