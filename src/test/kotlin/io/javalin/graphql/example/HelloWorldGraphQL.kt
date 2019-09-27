package io.javalin.graphql.example

import io.javalin.Javalin
import io.javalin.graphql.JavalinGraphql


var message: String = "Hello World"

fun main(args: Array<String>) {
    val app = Javalin.create().start(7070)
    JavalinGraphql
        .addPackage("io.javalin.graphql")
        .register(QueryExample(message))
        .register(MutationExample(message))
        .register(SubscriptionExample())
        .build("/graphql", app)
}

