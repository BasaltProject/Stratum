#pragma once

#include <basalt/proxy/plugins/AdapterMapping.hpp>

#include <span>

namespace basalt::stratum::bungee {

/// BungeeCord API member to native service and Lua spelling; the adapter reads this, it adds no behavior.
[[nodiscard]] std::span<const proxy::plugins::AdapterMapping> mappings() noexcept;

} // namespace basalt::stratum::bungee
