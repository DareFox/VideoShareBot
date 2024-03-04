package me.darefox.videosharebot.match

import me.darefox.videosharebot.extensions.tryParseURL
import java.net.URL

/**
 * Class for matching URLs based on specific patterns.
 */
abstract class UrlMatcher {
    private val supportedProtocols = listOf("https", "http")

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
            if (applyPattern(pattern, it)) {
                it.toString()
            } else {
                null
            }
        }.toList()
    }

    private fun applyPattern(pattern: UrlPattern, url: URL): Boolean {
        val domain = pattern.baseDomains.firstOrNull { baseDomain ->
            url.host.endsWith(baseDomain)
        } ?: return false

        val subdomain = url.host.removeSuffix(domain).removeSuffix(".")
        if (subdomain !in pattern.subdomains) return false

        return pattern.segmentPatterns.any {
            if (!isQueryMatch(it.queryMatcher, url)) return@any false
            isSegmentMatch(it.segmentMatchers, url)
        }
    }

    private fun isSegmentMatch(segmentMatchers: List<UrlSegmentMatcher>, url: URL): Boolean {
        val path = url.path.removeSuffix("/").removePrefix("/").split("/")
        if (segmentMatchers.size != path.size) return false


        var index = 0
        return segmentMatchers.all {
            it.validate(path[index]).also {
                index++
            }
        }
    }

    private fun isQueryMatch(queryMatcher: List<UrlQueryMatcher>, url: URL): Boolean {
        val querySplit = url.query?.split("=", "&") ?: return true
        require(querySplit.size % 2 == 0) {
            "(${url.query}) can't be split evenly ($querySplit)"
        }
        val map = mutableMapOf<String,String>()
        var key: String? = null
        for ((index, keyOrValue) in querySplit.withIndex()) {
            if (index % 2 == 0) { // key
                key = keyOrValue
            } else { // value
                requireNotNull(key)
                map[key] = keyOrValue
                key = null
            }
        }

        return queryMatcher.all {
            val value = map[it.key] ?: return@all false
            it.validator.validate(value)
        }
    }
}

