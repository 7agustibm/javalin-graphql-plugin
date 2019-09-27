package io.javalin.graphql.example

import io.javalin.graphql.Subscription
import reactor.core.publisher.Flux
import java.time.Duration
import kotlin.random.Random

class SubscriptionExample: Subscription {
    fun counter(): Flux<Int> = Flux.interval(Duration.ofSeconds(1)).map { Random.nextInt() }
}
