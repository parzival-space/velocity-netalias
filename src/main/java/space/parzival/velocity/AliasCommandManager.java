package space.parzival.velocity;

import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.SimpleCommand;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class AliasCommandManager {
    private final HashMap<String, Command> aliasCommands = new HashMap<>();

    public void registerAlias(String aliasName, String aliasArgs, String commandName, String commandArgs) {
        SimpleCommand virtualCommand = new SimpleCommand() {
            @Override
            public void execute(Invocation invocation) {
                String joinedArguments = String.join(" ", invocation.arguments());
                String newCommandArguments = aliasArgs.isEmpty() ? "" : (" " + joinedArguments.replaceAll(aliasArgs, commandArgs));

                CompletableFuture<Boolean> executionResult = NetAliasPlugin.getInstance().getCommandManager()
                        .executeImmediatelyAsync(invocation.source(), commandName + " " + newCommandArguments);
            }
        };

        NetAliasPlugin.getInstance().getCommandManager().register(aliasName, virtualCommand);
        this.aliasCommands.put(aliasName, virtualCommand);
        log.info("Registered alias command '{}'", aliasName);
    }

    public void unregisterAliases() {
        this.aliasCommands.forEach((key, value) -> {
            NetAliasPlugin.getInstance().getCommandManager().unregister(key);
            log.info("Unregistered alias command '{}'", key);
        });
        this.aliasCommands.clear();
    }

}
