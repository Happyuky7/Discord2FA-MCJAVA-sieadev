package dev.siea.spigot;

import dev.siea.common.CommonException;
import dev.siea.common.discord.DiscordUtils;
import dev.siea.common.messages.Messages;
import dev.siea.common.storage.CommonStorageManager;
import dev.siea.common.util.UpdateChecker;
import dev.siea.spigot.commands.Discord2FACommand;
import dev.siea.spigot.commands.LinkCommand;
import dev.siea.spigot.commands.UnlinkCommand;
import dev.siea.spigot.managers.LinkManager;
import dev.siea.common.Common;
import dev.siea.spigot.managers.VerifyManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class Discord2FA extends JavaPlugin {
    private static Plugin plugin;
    private static Common common;

    @Override
    public void onEnable() {
        enablePlugin();
    }

    private void enablePlugin() {
        plugin = this;

        LinkManager linkManager = new LinkManager();
        VerifyManager verifyManager = new VerifyManager();

        try {
            common = new Common(linkManager, verifyManager, getDataFolder().toPath());
        } catch (CommonException e) {
            disable(e.getMessage());
            return;
        }

        verifyManager.loadConfig();

        getServer().getPluginManager().registerEvents(verifyManager, this);
        Objects.requireNonNull(getCommand("link")).setExecutor(new LinkCommand());
        Objects.requireNonNull(getCommand("unlink")).setExecutor(new UnlinkCommand());
        Objects.requireNonNull(getCommand("discord2fa")).setExecutor(new Discord2FACommand(this));
        String versionMessage = UpdateChecker.generateUpdateMessage(getDescription().getVersion());
        if (versionMessage != null) {
            plugin.getLogger().severe(versionMessage);
        }
        try {
            enableBStats();
        } catch (Exception ignore){}
    }

    private void enableBStats(){
        int pluginID = 21448;
        new Metrics(this, pluginID);
    }

    public void reload(){
        plugin.reloadConfig();
        common.reload();
    }

    public static void disable(String reason){
        try {
            common.disable(reason);
        } catch (Exception ignore) {}
        plugin.getLogger().severe(reason);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    @Override
    public void onDisable() {
        try{
            common.shutdown();
        } catch (Exception ignore){

        }
    }
    
    public static Plugin getPlugin() {
        return plugin;
    }

    public static LinkManager getLinkManager() {
        return (LinkManager) common.getLinkManager();
    }

    public static VerifyManager getVerifyManager() {
        return (VerifyManager) common.getVerifyManager();
    }

    public static DiscordUtils getDiscordUtils() {
        return common.getDiscordUtils();
    }

    public static CommonStorageManager getStorageManager() {
        return common.getStorageManager();
    }

    public static Messages getMessages(){
        return common.getMessages();
    }

    public static Common getCommon() {
        return common;
    }
}
