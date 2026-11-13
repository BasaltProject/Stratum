#pragma once

#include <basalt/proxy/plugins/JavaAdapter.hpp>

namespace basalt::stratum::bungee {

[[nodiscard]] inline std::unique_ptr<proxy::plugins::ForeignAdapter>
createBungeeAdapter(const proxy::plugins::JavaRuntimeOptions& options, const proxy::plugins::PluginHooks& hooks) {
    return proxy::plugins::createJavaAdapter(options, hooks,
                                             {.label = "Bungee",
                                              .logName = "bungee",
                                              .facade = "org/basaltmc/stratum/bungee/BasaltBungee",
                                              .natives = "org/basaltmc/stratum/bungee/Natives",
                                              .kind = proxy::plugins::ForeignKind::kBungee,
                                              .slot = 0});
}

} // namespace basalt::stratum::bungee
