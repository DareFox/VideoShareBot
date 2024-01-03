package parser

import match.services.TikTokMatcher

class TikTokMatcherTest : MatcherTest(
    parser = TikTokMatcher,
    validUrls = listOf(
        "https://www.tiktok.com/@rooroo01636/video/7284682918864145696",
        "https://www.tiktok.com/@itsendosare/video/7313225401059314976?_t=8iKujNKw5Cw&_r=1",
        "https://vm.tiktok.com/ZM6hbrPoP/",
    ),
    invalidUrls = listOf(
        "tiktok.com/video/7269402457187405098",
        "tiktok.com/@luckysaul7/video/726940245718740509822222",
        "tiktok.com/@luckysaul7/video/726940",
        "tiktok.com/@luckysaul7/video/7269402457187405098/",
        "tiktok.com/ZMjeeUWfr/"
    )
)