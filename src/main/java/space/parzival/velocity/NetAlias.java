package space.parzival.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(
        id = "netalias",
        name = "NetAlias",
        version = "1.0.0-SNAPSHOT",
        url = "https://github.com/parzival-space/velocity-netalias",
        description = "A Velocity plugin for creating network-wide command aliases.",
        authors = { "Parzival" }
)
public class NetAlias {
    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public NetAlias(ProxyServer server, Logger logger) {
        this.server = server;
        this.logger = logger;

        logger.info("NetAlias loaded. Hello there!");
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        
    }
}
