package me.darefox.videosharebot

import java.net.URL

/**
 * The `me.darefox.videosharebot.EnvironmentConfig` object is responsible for accessing environment variables used within the application.
 */
object EnvironmentConfig {
    /**
     * The base URL for the cobalt.Cobalt API. This URL is used for making API requests to cobalt.Cobalt services.
     *
     * Environment variable name: `COBALT_API_URL`
     *
     * Default: `"https://co.wuk.sh/"`
     */
    val cobaltApiUrl: URL = URL(stringEnv("COBALT_API_URL", "https://co.wuk.sh/"))

    /**
     * A boolean flag indicating whether the application is in debug mode.
     *
     * Environment variable name: `ENABLE_DEBUG`.
     *
     * Default value: `false`
     */
    val debugMode: Boolean = booleanEnv("ENABLE_DEBUG", false)

    /**
     * The Discord bot token used for authentication and communication with the Discord API.
     *
     * Environment variable name: `DISCORD_BOT_TOKEN`.
     */
    val discordToken: String = stringEnv("DISCORD_BOT_TOKEN")

    /**
     * The ID of the Discord test server, if applicable.
     *
     * Environment variable name: `DISCORD_TEST_SERVER`.
     */
    val discordTestServer: String? = nullableStringEnv("DISCORD_TEST_SERVER")

    /**
     * Retrieves a nullable string value from the environment variables.
     *
     * @param envName The name of the environment variable.
     * @return The value of the environment variable, or `null` if not set.
     */
    private fun nullableStringEnv(envName: String): String? {
        return System.getenv(envName)
    }

    /**
     * Retrieves a non-null string value from the environment variables.
     *
     * @param envName The name of the environment variable.
     * @return The value of the environment variable.
     * @throws IllegalArgumentException if the environment variable is not set.
     */
    private fun stringEnv(envName: String): String {
        val value = requireNotNull(System.getenv(envName)) {
            "Environment variable $envName doesn't exists"
        }

        return value
    }

    /**
     * Retrieves a string value from the environment variables with a default fallback value.
     *
     * @param envName The name of the environment variable.
     * @param default The default value to use if the environment variable is not set.
     * @return The value of the environment variable, or the default value if not set.
     */
    private fun stringEnv(envName: String, default: String): String {
        return System.getenv(envName) ?: default
    }

    /**
     * Retrieves a boolean value from the environment variables with a default fallback value.
     * If the environment variable value is not a valid boolean, the default value is used.
     *
     * @param string The name of the environment variable.
     * @param default The default value to use if the environment variable is not set or is invalid.
     * @return The boolean value of the environment variable, or the default value if not set or invalid.
     */
    private fun booleanEnv(string: String, default: Boolean): Boolean {
        return string
            .trim()
            .lowercase()
            .toBooleanStrictOrNull() ?: default
    }
}

