package me.darefox.videosharebot.parser

import me.darefox.videosharebot.match.services.YoutubeShortsMatcher

class YoutubeShortsMatcherTest: MatcherTest(
    parser = YoutubeShortsMatcher,
    validUrls = listOf(
        "https://www.youtube.com/shorts/abcdef12345",
        "https://youtube.com/shorts/abcdef12345"
    ),
    invalidUrls = listOf(
        "youtube.com/shorts/abc",
        "youtube.com",
        "youtube.com/shorts/"
    )
)