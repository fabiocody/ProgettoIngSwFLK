![Sagrada Banner](https://ksr-ugc.imgix.net/assets/013/393/383/88f9cae91e41ef71ac2b06fb2fa564de_original.jpg?w=1024&h=576&fit=fill&bg=000000&v=1473272732&auto=format&q=92&s=ec3ac5ec050115cbbcc0666e2315ab7b)

# ProgettoIngSwFLK

[![Build Status](https://travis-ci.com/fabiocody/ProgettoIngSwFLK.svg?token=vreerFzSmcFLsbiVv8aF&branch=master)](https://travis-ci.com/fabiocody/ProgettoIngSwFLK)

As a skilled artisan, you will use tools-of-the-trade and careful planning to construct a stained glass window masterpiece in the Sagrada Familia. Players will take turns drafting glass pieces, represented by dice; carefully choosing where to place each one in their window. Gain prestige by adapting to the preferences of your fickle admirers, and of course, by adding your own artistic flair while completing your glass masterpiece in Sagrada. Further info can be found at this [link](https://www.kickstarter.com/projects/floodgategames/sagrada-a-game-of-dice-drafting-and-window-craftin).

## Team members

- _Fabio Codiglioni_ (10484720)
- _Luca dell'Oglio_ (10497928)
- _Kai de Gast_ (10523952)

## Table of contents

<!-- TOC depthFrom:2 depthTo:6 withLinks:1 updateOnSave:0 orderedList:0 -->

- [Before playing](#before-playing)
	- [Playing under Linux](#playing-under-linux)
	- [Playing under Windows](#playing-under-windows)
	- [Playing under macOS](#playing-under-macos)
- [Start playing](#start-playing)
	- [Start the server](#start-the-server)
	- [Start a client](#start-a-client)

<!-- /TOC -->

## Before playing

You can find the complete set of rules of the original board game [here](http://floodgategames.com/Sagrada/Sagrada-Rules-Floodgate-Games-SA01.pdf).

This game is written in Java, so you need to have Java Runtime Environment 8 or greater installed on your system in order to play. More information [here](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html).

### Playing under Linux

Before playing under Linux, we suggest to have a look at your `/etc/hosts` file. On certain distros (e.g. Debian, Ubuntu, ...), it can contain a line such as

```
127.0.1.1   <hostname>
```

which can cause RMI connection problems. To avoid messing with your network configuration, we recommend to comment out this line, so you can bring it back once you finish playing.

### Playing under Windows

If you intend to play on Windows using the CLI, you need to install the Linux Bash Shell, which is part of the Linux Subsystem available for Windows 10. Playing on the standard command line (i.e. `cmd.exe`) is not supported at the moment.

### Playing under macOS

Everything should just work (pure Apple style).

## Start playing

### Start the server

To start the server, simply locate its jar file and then type into a shell

```
$ java -jar path/to/server.jar <host>
```

where `host` must be the IP address of the machine running the server (needed to make RMI work flawlessly).

Additional parameters include:

- `--port PORT`: the port to be used for socket connectivity (default to 42000).
- `--wr-timeout`: the amount of seconds to wait before starting a new game (default to 30).
- `--game-timeout`: the amount of seconds to wait before suspending a player (default to 90).
- `--debug`: activate debug mode (to be used only if really needed).

### Start a client

To start a client, simply locate its jar file and then type into a shell

```
$ java -jar path/to/client.jar --host HOST
```

where `HOST` must be the IP address of the machine running the server.

Additional parameters include:

- `--port PORT`: the port to be used for socket connectivity (default to 42000).
- `--connection CONNECTION`: the type of connection, can only be `socket` or `rmi` (default to `socket`). Be aware that some network configurations (e.g. firewalls, NAT, ...) can cause connection problems, especially if you choose to use RMI.
- `--interface INTERFACE`: the type of interface, can only be `cli` or `gui` (default to `cli`).
- `--debug`: activate debug mode (should be avoided).
