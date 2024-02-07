package me.darefox.videosharebot.match

/**
 * A functional interface defining the behavior of validating path element within a URL.
 *
 * ## What is path element?
 *
 * Example link: `https://google.com/skyfall/when/you/crumble`
 *
 * Path element in link is `skyfall` ; `when` ; `you` ; `crumble`
 */
fun interface UrlSegmentMatcher {

    /**
     * Examines the provided text and determines whether it aligns with the specified validation criteria.
     *
     * @param text The text of the URL segment to be validated.
     * @return `true` if the text matches the criteria, `false` otherwise.
     */
    fun validate(text: String): Boolean
}

