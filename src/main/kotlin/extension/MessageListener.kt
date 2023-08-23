package extension

import com.kotlindiscord.kord.extensions.events.EventContext
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.core.event.message.MessageCreateEvent

class MessageListener : LoggerExtension("MessageListener") {
    override suspend fun setup() {
        log.info { "setup" }
        event<MessageCreateEvent> {
            action {
                log.info { "test" }
                actionImpl()
            }
        }
    }

    private suspend fun EventContext<MessageCreateEvent>.actionImpl() {
        log.info { event.message.content }
    }
}