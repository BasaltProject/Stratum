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
  <strong>Java plugin compatibility for Basalt: Bukkit, BungeeCord and Velocity, on top of Shim.</strong>
</p>

<p align="center">
  <img alt="Java Edition" src="https://img.shields.io/badge/Java_Edition-26.3-62B47A?logo=minecraft&logoColor=white">
  <img alt="C++" src="https://img.shields.io/badge/language-C%2B%2B-00599C?logo=cplusplus&logoColor=white">
  <img alt="Java" src="https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white">
  <img alt="License" src="https://img.shields.io/badge/license-GPL--3.0-green">
</p>

> **Pre-1.0.** Stratum moves together with Server and Proxy, so its APIs change without notice. The Bukkit layer is the
> most complete; BungeeCord and Velocity are still in development.

## About

Stratum implements the APIs of other Minecraft platforms and translates them into Basalt's own native services, the
same ones the Lua plugin API uses. It is not a standalone program: it is built into Server and Proxy as a git
submodule.

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

The diagram shows how plugins execute, not network traffic. [Shim](https://github.com/BasaltProject/Shim) only
executes bytecode, and Stratum provides the classes that bytecode calls. Without a matching jar in `plugins/`, no Java
machine starts.

| Module | Platform | Product |
|---|---|---|
| `stratum-bukkit` | Bukkit, Spigot, Paper | [Server](https://github.com/BasaltProject/Server) |
| `stratum-bungee` | BungeeCord | [Proxy](https://github.com/BasaltProject/Proxy) |
| `stratum-velocity` | Velocity | [Proxy](https://github.com/BasaltProject/Proxy) |

It lives in its own repository because the Java side of these APIs is far larger than the native code that adapts it,
and keeping it here leaves Server and Proxy pure C++. The logic stays native; the Java side only adapts the shape.
The `net.minecraft` and `com.mojang` classes that Bukkit plugins expect are an independent reimplementation, not
Mojang code; see [NOTICE.md](stratum-bukkit/NOTICE.md).

## Building

Stratum is not built on its own: clone Server or Proxy with `--recurse-submodules`. The Bukkit layer needs a JDK at
build time only (`STRATUM_BUKKIT` is `AUTO`, `ON` or `OFF`).

## Contributing

Read the [Code of Conduct](.github/CODE_OF_CONDUCT.md). Report vulnerabilities privately through the
[security policy](SECURITY.md).

## License

[GPL-3.0](LICENSE).

Basalt is not affiliated with Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Studios.
