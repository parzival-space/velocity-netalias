package space.parzival.velocity;

import com.velocitypowered.api.command.SimpleCommand;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AliasCommandManager {
    @Getter
    private final HashMap<String, String> registeredAliases = new HashMap<>();

    public void registerAlias(String aliasName, String aliasArgs, String commandName, String commandArgs) {
        SimpleCommand virtualCommand = new SimpleCommand() {
            @Override
            public void execute(Invocation invocation) {
                String joinedArguments = String.join(" ", invocation.arguments());
                String newCommandArguments = "";

                if (!aliasArgs.isEmpty() && !joinedArguments.isEmpty()) {
                    Pattern pattern = Pattern.compile(aliasArgs);
                    Matcher matcher = pattern.matcher(joinedArguments);

                    StringBuilder result = new StringBuilder();
                    while (matcher.find()) {
                        String replacement = commandArgs;
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            replacement = replacement.replace("$" + i, matcher.group(i));
                        }
                        matcher.appendReplacement(result, replacement);
                    }
                    matcher.appendTail(result);

                    newCommandArguments = result.toString();
                }

                CompletableFuture<Boolean> executionResult = NetAliasPlugin.getInstance().getCommandManager()
                        .executeImmediatelyAsync(invocation.source(), commandName + (newCommandArguments.isEmpty() ? "" : " " + newCommandArguments));
            }
        };

        NetAliasPlugin.getInstance().getCommandManager().register(aliasName, virtualCommand);
        this.registeredAliases.put(aliasName, commandName + (commandArgs.isEmpty() ? "" : " " + commandArgs));
        log.info("Registered alias command '{}'", aliasName);
    }

    public void unregisterAliases() {
        this.registeredAliases.forEach((key, value) -> {
            NetAliasPlugin.getInstance().getCommandManager().unregister(key);
            log.info("Unregistered alias command '{}'", key);
        });
        this.registeredAliases.clear();
    }

}
