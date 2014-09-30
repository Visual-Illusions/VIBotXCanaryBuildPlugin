package net.visualillusionsent.canarybuildplugin;

import net.visualillusionsent.canarybuildplugin.commands.BuildCommand;
import net.visualillusionsent.vibotx.VIBotX;
import net.visualillusionsent.vibotx.api.command.CommandCreationException;
import net.visualillusionsent.vibotx.api.plugin.JavaPlugin;

/**
 * Created by Aaron on 9/23/2014.
 */
public class CanaryBuildPlugin extends JavaPlugin {
    public static CanaryBuildPlugin instance;

    @Override
    public boolean enable() {
        instance = this;
        try {
            new BuildCommand(this);
        } catch (CommandCreationException e) {
            VIBotX.log.error("Error Registering Commands in Canary Build Plugin.", e);
        }
        return true;
    }

    @Override
    public void disable() {

    }

    public CanaryBuildPlugin instance() {
        return instance;
    }
}
