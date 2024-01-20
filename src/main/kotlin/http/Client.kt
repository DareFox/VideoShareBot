package http

import org.http4k.client.OkHttp
import org.http4k.core.BodyMode

val HttpStreamingClient = OkHttp(bodyMode = BodyMode.Stream)