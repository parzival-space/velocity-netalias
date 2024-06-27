# NetAlias
Velocity plugin for creating command aliases.

<a href="https://github.com/parzival-space/velocity-netalias/releases/latest">
  <code>[ Download Plugin ]</code>
</a>

## Commands
NetAlias has the following commands:

* ``/netalias [list, reload]``  
  Displays information about the plugin.
  * ``[list]`` Shows all currently registered aliases.
  * ``[reload]`` Reloads the plugins configuration.

## Configuration
You can find the config file here: ``plugins/netalias/config.toml``
```toml
aliases = [
    { name = "switchserver", args = "(\\S*)", command = "server", commandArgs = "$1" } 
]
```
This will create a command /switchserver. 
The command /server is going to be an alias with the arguments of the first catch (which means the first argument, 
the server name) of RegEx.