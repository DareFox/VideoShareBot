package match

import enhancements.tryParseURL
import java.net.URL

abstract class UrlMatcher {
    private val supportedProtocols = listOf("https", "http")

    /**
     * Regular expression used to match any URLs
     */
    private val anyUrlRegex =
        // i hate regex
        "[-a-zA-Z0-9@:%_\\+.~#?&//=]{2,256}\\.[a-z]{2,4}\\b(\\/[-a-zA-Z0-9@:%_\\+.~#?&//=]*)?".toRegex()

    protected abstract val pattern: UrlPattern

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
        val path = url.path.removeSurrounding("/").split("/")
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