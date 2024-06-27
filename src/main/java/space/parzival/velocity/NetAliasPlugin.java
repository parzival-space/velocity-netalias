package space.parzival.velocity;

import com.google.inject.Inject;
import com.moandjiezana.toml.Toml;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import space.parzival.velocity.commands.NetAliasCommand;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Plugin(
        id = "netalias",
        name = "NetAlias",
        version = "1.0.0-SNAPSHOT",
        url = "https://github.com/parzival-space/velocity-netalias",
        description = "A Velocity plugin for creating network-wide command aliases.",
        authors = { "Parzival" }
)
public class NetAliasPlugin {
    private final AliasCommandManager aliasCommandManager;
    private final Path dataDirectory;

    @Getter
    private final CommandManager commandManager;

    @Getter
    private final ProxyServer proxyServer;

    @Getter
    private static final Plugin pluginInfo = NetAliasPlugin.class.getAnnotation(Plugin.class);

    @Getter
    private static NetAliasPlugin instance;

    @Inject
    public NetAliasPlugin(CommandManager commandManager, ProxyServer proxyServer, @DataDirectory Path dataDirectory) {
        instance = this;
        this.commandManager = commandManager;
        this.dataDirectory = dataDirectory;
        this.proxyServer = proxyServer;
        this.aliasCommandManager = new AliasCommandManager();

        // fetch own plugin metadata
        log.info("{} v{} loaded. Hello there!", getPluginInfo().name(), getPluginInfo().version());
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // register internal commands
        this.commandManager.register(getPluginInfo().id(), new NetAliasCommand());

        // register alias commands
        Toml config = loadConfig("config.toml");
        List<HashMap<String, String>> aliases = config.getList("aliases");
        aliases.forEach(alias -> this.aliasCommandManager
                .registerAlias(alias.get("name"), alias.get("args"), alias.get("command"), alias.get("commandArgs"))
        );
    }

    public void Reload() {
        // unregister internal commands
        this.commandManager.unregister(getPluginInfo().id());
        this.commandManager.register(getPluginInfo().id(), new NetAliasCommand());

        // unregister aliases
        this.aliasCommandManager.unregisterAliases();

        // register alias commands
        Toml config = loadConfig("config.toml");
        List<HashMap<String, String>> aliases = config.getList("aliases");
        aliases.forEach(alias -> this.aliasCommandManager
                .registerAlias(alias.get("name"), alias.get("args"), alias.get("command"), alias.get("commandArgs"))
        );
    }

    public Toml loadConfig(String configFileName) {
        File configFile = new File(this.dataDirectory.toFile(), configFileName);
        if (!configFile.getParentFile().exists())
            configFile.getParentFile().mkdirs();

        if (!configFile.exists()) {
            try (InputStream configFileStream = getClass().getResourceAsStream("/" + configFileName)) {
                if (configFileStream != null)
                    Files.copy(configFileStream, configFile.toPath());
                else
                    configFile.createNewFile();
            } catch (IOException e) {
                log.error("Failed to create configuration file.", e);
            }
        }
        return new Toml().read(configFile);
    }
}
