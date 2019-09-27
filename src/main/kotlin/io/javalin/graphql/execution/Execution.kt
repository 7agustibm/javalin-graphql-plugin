package io.javalin.graphql.execution

import graphql.GraphQL
import io.javalin.http.Context
import io.javalin.plugin.json.JavalinJson
import java.util.concurrent.CompletableFuture

class Execution(private val context: Context, graphql: GraphQL) : Run(graphql) {

    override fun run() {
        context.contentType("application/json").result(runAsync())
    }

    private fun runAsync(): CompletableFuture<String> =
        executeAsync()
            .thenApplyAsync { it.toSpecification() }
            .thenApplyAsync { JavalinJson.toJson(it) }
}
