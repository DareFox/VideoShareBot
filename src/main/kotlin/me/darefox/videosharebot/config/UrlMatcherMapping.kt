package me.darefox.videosharebot.config

import me.darefox.videosharebot.match.UrlMatcher
import me.darefox.videosharebot.match.services.*
import me.darefox.videosharebot.tools.EnumMapping

enum class UrlMatcherMapping(override val mappedValue: UrlMatcher): EnumMapping<UrlMatcher> {
    Instagram(InstagramMatcher),
    Reddit(RedditMatcher),
    TikTok(TikTokMatcher),
    Twitter(TwitterMatcher),
    Vk(VkMatcher),
    YouTube(YoutubeMatcher),
    YouTubeShorts(YoutubeShortsMatcher),
}