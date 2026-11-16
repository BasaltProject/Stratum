// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package com.mojang.authlib.services;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public interface MinecraftServicesDiscoveryService {
    ServicesKeySet keySet();

    static URL constantURL(String url) {
        try {
            return URI.create(url).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Couldn't create constant for " + url, e);
        }
    }

    static URL concatenateURL(URL url, String query) {
        try {
            String base = url.toString();
            return URI.create(base + (url.getQuery() == null || url.getQuery().isEmpty() ? "?" : "&") + query).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Couldn't concatenate " + url + " and " + query, e);
        }
    }
}
