<p align="center">
  <a href="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-white.svg#gh-dark-mode-only">
    <img src="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-white.svg" alt="Basalt" width="360">
  </a>
  <a href="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-black.svg#gh-light-mode-only">
    <img src="https://raw.githubusercontent.com/BasaltProject/.github/main/assets/basalt-logo-black.svg" alt="Basalt" width="360">
  </a>
</p>

<h2 align="center">Basalt Stratum</h2>

<p align="center">
  <strong>The Java plugin API compatibility layer for Basalt: Bukkit, BungeeCord and Velocity, on top of Shim.</strong>
</p>

<p align="center">
  <img alt="Minecraft" src="https://img.shields.io/badge/Minecraft-26.3-62B47A?logo=minecraft&logoColor=white">
  <img alt="Protocol" src="https://img.shields.io/badge/protocol-777-62B47A">
  <img alt="C++" src="https://img.shields.io/badge/C%2B%2B-23-00599C?logo=cplusplus&logoColor=white">
  <img alt="Java" src="https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white">
  <img alt="Platforms" src="https://img.shields.io/badge/platforms-macOS_%7C_Linux-00599C">
  <img alt="Build" src="https://img.shields.io/badge/build-CMake-064F8C?logo=cmake&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/license-GPL--3.0-green">
</p>

> **Pre-1.0.** Stratum is consumed by Basalt Server and Basalt Proxy, which move together with it,
> so its APIs change without notice. The Bukkit layer is the most complete; the BungeeCord and
> Velocity layers are still in development.

## Overview

Basalt runs plugins written for other Minecraft platforms without giving up its native core.
Stratum is the part that implements those platforms' APIs and translates them into Basalt's own
native services, the same ones the Lua plugin API uses.

It is not a standalone program. It is built into Basalt Server and Basalt Proxy as a git submodule:

```
Basalt Server       Basalt Proxy
      \                 /
       v               v
   Shim   JVM that runs the plugins' Java bytecode
       |
       v
   Stratum   Bukkit / Velocity / BungeeCord APIs
       |
       v
   Basalt's native engine
```

The diagram shows how plugins execute, not network traffic. [Shim](https://github.com/BasaltProject/Shim)
only executes bytecode, and Stratum provides the classes that bytecode calls.

| Product | What it builds from Stratum |
|---|---|
| [Server](https://github.com/BasaltProject/Server) | Bukkit, Spigot and Paper plugins |
| [Proxy](https://github.com/BasaltProject/Proxy) | BungeeCord and Velocity plugins |

Each product compiles only its own platforms. Without a matching jar in `plugins/`, no Java
machine starts, and no Java installation is needed on the host: Shim is embedded.

Why a separate repository: the Java side of these APIs is far larger than the native code that
adapts it. Keeping it here leaves Server and Proxy pure C++.

| | |
|---|---|
| Language | Native bridge in C++23, API facade in Java |
| Target version | Minecraft 26.3 (protocol 777) |
| Consumed as | A git submodule, added to the consumer's CMake build |
| Logic | Lives in Basalt's native services; the Java side only adapts the shape |
| License | GPL-3.0 |

## Modules

| Module | Platform | Product |
|---|---|---|
| `stratum-bukkit` | Bukkit, Spigot, Paper | Server |
| `stratum-bungee` | BungeeCord | Proxy |
| `stratum-velocity` | Velocity | Proxy |

Java packages live under `org.basaltmc.stratum.*` and the native code under `basalt::stratum::*`.
The `net.minecraft` and `com.mojang` classes that Bukkit plugins expect are an independent
reimplementation, not Mojang code; see [NOTICE.md](stratum-bukkit/NOTICE.md).

## Building

Stratum is not built on its own. Server and Proxy include it as a submodule, so clone either of
them with its submodules:

```bash
git clone --recurse-submodules https://github.com/BasaltProject/Server.git
```

In an existing clone, `git submodule update --init` does the same. The Bukkit layer needs a JDK
(`javac`, `jar` and `jni.h`) at build time only. `STRATUM_BUKKIT` is `AUTO` by default, which turns it
on when a JDK is found; `ON` requires it and `OFF` disables it.

## Contributing and community

Please read the [Code of Conduct](.github/CODE_OF_CONDUCT.md) before participating.
Report suspected vulnerabilities privately using the [security policy](SECURITY.md).

## License

Basalt Stratum is distributed under the [GNU General Public License v3.0](LICENSE).

Basalt is not affiliated with Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Studios.
