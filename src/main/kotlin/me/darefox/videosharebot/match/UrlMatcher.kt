package me.darefox.videosharebot.match

import me.darefox.videosharebot.extensions.tryParseURL
import java.net.URL

/**
 * Class for matching URLs based on specific patterns.
 */
abstract class UrlMatcher {
    private val supportedProtocols = listOf("https", "me/darefox/videosharebot/http")

    /**
     * Regular expression used to match any URLs
     */
    private val anyUrlRegex =
        // i hate regex
        "[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?".toRegex()

    /**
     * The URL pattern used for matching.
     */
    protected abstract val pattern: UrlPattern

    /**
     * Extracts URLs from text that match a specific pattern.
     *
     * @param text The text to search for URLs.
     * @return A list of matching URLs.
     */
    fun parse(text: String): List<String> {
        return anyUrlRegex.findAll(text).mapNotNull {
            it.value.tryParseURL()
        }.filter {
            it.protocol in supportedProtocols
        }.mapNotNull {
            applyPattern(pattern, it)
        }.toList()
    }

    private fun applyPattern(pattern: UrlPattern, url: URL): String? {
        val domain = pattern.baseDomains.firstOrNull { baseDomain ->
            url.host.endsWith(baseDomain)
        } ?: return null

        val subdomain = url.host.removeSuffix(domain).removeSuffix(".")
        if (subdomain !in pattern.subdomains) return null

        return applySegmentMatchers(pattern.segmentMatchers, url)?.toString()
    }

    private fun applySegmentMatchers(matchers: List<List<UrlSegmentMatcher>>, url: URL): URL? {
        val path = url.path.removeSuffix("/").removePrefix("/").split("/")
        val usefulMatchers = matchers.filter {
            it.size == path.size
        }

        for (pattern in usefulMatchers) {
            var valid = true
            for ((index, segmentMatcher) in pattern.withIndex()) {
                val text = path[index]
                valid = segmentMatcher.validate(text)
                if (!valid) break
            }
            if (valid) return url
        }

        return null
    }

}