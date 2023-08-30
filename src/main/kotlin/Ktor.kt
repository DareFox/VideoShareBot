import io.ktor.client.*
import io.ktor.client.engine.cio.*

val ktor = HttpClient(CIO)