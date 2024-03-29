package me.darefox.videosharebot.match

infix fun UrlSegmentMatcher.or(matcher: UrlSegmentMatcher): UrlSegmentMatcher {
    return UrlSegmentMatcher {
        this.validate(it) || matcher.validate(it)
    }
}

infix fun UrlSegmentMatcher.and(matcher: UrlSegmentMatcher): UrlSegmentMatcher {
    return UrlSegmentMatcher {
        this.validate(it) && matcher.validate(it)
    }
} 