import kotlinx.coroutines.*

/**
 * Runs two suspending blocks concurrently in a race-like manner, canceling the slower one if the other finishes first.
 *
 * @param raceOne The first suspending block to execute.
 * @param raceTwo The second suspending block to execute.
 *
 * @throws CancellationException If the current coroutine is canceled while the function is executing.
 */
suspend fun raceWithCancellation(
    raceOne: suspend () -> Unit,
    raceTwo: suspend () -> Unit
) {
    coroutineScope {
        lateinit var raceOneJob: Job
        lateinit var raceTwoJob: Job

        raceOneJob = launch(start = CoroutineStart.LAZY) {
            raceOne()
            raceTwoJob.cancel("Another job finished faster")
        }
        raceTwoJob = launch(start = CoroutineStart.LAZY) {
            raceTwo()
            raceOneJob.cancel("Another job finished faster")
        }

        raceOneJob.start()
        raceTwoJob.start()
    }
}