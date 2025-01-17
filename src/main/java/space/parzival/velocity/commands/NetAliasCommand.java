package space.parzival.velocity.commands;

import com.google.common.base.Strings;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.RGBLike;
import org.jetbrains.annotations.NotNull;
import space.parzival.velocity.NetAliasPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.kyori.adventure.text.Component.text;

public class NetAliasCommand implements SimpleCommand {
    @Override
    public void execute(final Invocation invocation) {
        List<String> arguments = Arrays.stream(invocation.arguments()).toList();

        // display plugin info if no args are provided
        if (arguments.isEmpty()) {
            Plugin pluginInfo = NetAliasPlugin.getPluginInfo();

            // create info message
            Component infoMessage = text()
                    .content(pluginInfo.name()).appendSpace()
                    .append(text("v" + pluginInfo.version()))
                    .appendNewline()
                    .append(text(pluginInfo.description()))
                    .appendNewline()
                    .append(text("Source:")).appendSpace()
                    .append(text(pluginInfo.url()).clickEvent(ClickEvent.openUrl(pluginInfo.url())))
                    .appendNewline()
                    .append(text("Author:")).appendSpace()
                    .append(text(String.join(", ", pluginInfo.authors())))
                    .build();
            invocation.source().sendMessage(infoMessage);
            return;
        }

        // first argument is "reload"
        else if (arguments.getFirst().equalsIgnoreCase("list")) {
            HashMap<String, String> aliases = NetAliasPlugin.getInstance().getAliasCommandManager()
                    .getRegisteredAliases();

            TextComponent.Builder aliasMessage = text()
                    .content("Registered aliases:").color(NamedTextColor.GREEN)
                    .appendNewline();

            aliases.forEach((key, value) -> aliasMessage.append(text(Strings.padEnd("/" + key, 20, ' ')).color(NamedTextColor.WHITE))
                    .append(text("   ->   ").color(NamedTextColor.BLUE))
                    .append(text(Strings.padStart("/" + value, 20, ' ')).color(NamedTextColor.WHITE))
                    .appendNewline());

            invocation.source().sendMessage(aliasMessage.build());
            return;
        }

        // first argument is "reload"
        else if (arguments.getFirst().equalsIgnoreCase("reload")) {
            NetAliasPlugin.getInstance().Reload();

            invocation.source().sendMessage(
                    text("Reloaded configuration.").color(NamedTextColor.GREEN)
            );
            return;
        }

        invocation.source().sendMessage(
                text("Unknown command.").color(NamedTextColor.RED)
        );
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(final Invocation invocation) {
        return CompletableFuture.completedFuture(List.of("list", "reload"));
    }
}
