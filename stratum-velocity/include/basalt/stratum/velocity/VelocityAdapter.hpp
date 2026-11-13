#pragma once

#include <basalt/proxy/plugins/JavaAdapter.hpp>

namespace basalt::stratum::velocity {

[[nodiscard]] inline std::unique_ptr<proxy::plugins::ForeignAdapter>
createVelocityAdapter(const proxy::plugins::JavaRuntimeOptions& options, const proxy::plugins::PluginHooks& hooks) {
    return proxy::plugins::createJavaAdapter(options, hooks,
                                             {.label = "Velocity",
                                              .logName = "velocity",
                                              .facade = "org/basaltmc/stratum/velocity/BasaltVelocity",
                                              .natives = "org/basaltmc/stratum/velocity/Natives",
                                              .kind = proxy::plugins::ForeignKind::kVelocity,
                                              .slot = 1});
}

} // namespace basalt::stratum::velocity
