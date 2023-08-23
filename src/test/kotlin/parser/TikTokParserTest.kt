package parser

class TikTokParserTest : ParserTest(
    parser = TikTokParser,
    validUrls = listOf(
        "tiktok.com/@luckysaul7/video/7269402457187405098",
        "vm.tiktok.com/ZMjeeUWfr"
    ),
    invalidUrls = listOf(
        "tiktok.com/video/7269402457187405098",
        "tiktok.com/@luckysaul7/video/726940245718740509822222",
        "tiktok.com/@luckysaul7/video/726940",
        "tiktok.com/@luckysaul7/video/7269402457187405098/",
        "tiktok.com/ZMjeeUWfr/"
    )
)