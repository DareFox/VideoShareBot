import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * A builder class for creating [RaceWithCancellation] instances.
 *
 * @constructor Creates a new RaceWithCancellationBuilder.
 */
class RaceWithCancellationBuilder {
    private val racers: MutableList<Pair<CoroutineContext, suspend CoroutineScope.() -> Unit>> = mutableListOf()
    /**
     * Adds a racer to the race.
     *
     * @param context The coroutine context to use for the racer.
     * @param block The suspendable block of code to run as the racer.
     */
    fun addRacer(context: CoroutineContext = EmptyCoroutineContext, block: suspend CoroutineScope.() -> Unit) {
        racers += context to block
    }
    /**
     * Builds a [RaceWithCancellation] instance from the added racers.
     *
     * @return The constructed RaceWithCancellation instance.
     */
    fun build(): RaceWithCancellation = RaceWithCancellation(racers)
}

/**
 * A class that manages a race between multiple coroutines, when one of the coroutines completes it will cancel all remaining coroutines.
 *
 * @constructor Creates a new RaceWithCancellation instance.
 * @param racers The list of racers to run in the race.
 */
class RaceWithCancellation(val racers: List<Pair<CoroutineContext, suspend CoroutineScope.() -> Unit>>) {
    private val mutex = Mutex()
    /**
     * Starts the race and cancels all remaining coroutines when one of them completes.
     *
     * @param context The coroutine context to use for the race.
     */
    suspend fun startRace(context: CoroutineContext = EmptyCoroutineContext) = withContext(context +  CoroutineName("raceWithCancellation Racer")) {
        val jobs = mutableListOf<Job>()
        for (racer in racers) {
            jobs += launch(racer.first, start = CoroutineStart.LAZY) {
                launch(CoroutineName("raceWithCancellation Block Invocation") + racer.first) { racer.second.invoke(this) }.join()
                mutex.withLock {
                    jobs.cancelAll()
                }
            }
        }

        for (job in jobs) {
            job.start()
        }
    }

    private fun List<Job>.cancelAll() {
        for (job in this) {
            job.cancel()
        }
    }
}

/**
 * Runs a race between multiple coroutines, when one of them completes it will cancel all remaining coroutines.
 *
 * This function returns as soon as all racing coroutines are cancelled/completed.
 *
 * @param context The coroutine context to use for the race.
 * @param builder A builder function for adding racers to the race.
 */

suspend fun raceWithCancellation(
    context: CoroutineContext = EmptyCoroutineContext,
    builder: RaceWithCancellationBuilder.() -> Unit
) {
    val raceBuild = RaceWithCancellationBuilder()
    raceBuild.builder()
    raceBuild.build().startRace(context)
}