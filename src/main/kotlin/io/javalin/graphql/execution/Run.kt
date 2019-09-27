package io.javalin.graphql.execution

import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture

abstract class Run(private val graphql: GraphQL) {
    private var context: Any? = null
    private var query: String = ""
    private var variables: Map<String, Any> = emptyMap()

    fun withQuery(query: String): Run {
        this.query = query
        return this
    }

    fun withVariables(variables: Map<String, Any>): Run {
        this.variables = variables
        return this
    }

    fun withContext(context: Any?): Run {
        this.context = context
        return this
    }

    private fun generateAction(): ExecutionInput {
        var builder = ExecutionInput.newExecutionInput()
            .variables(variables)
            .query(query)
        builder = context.let { builder.context(context) }
        return builder
            .build()
    }

    protected fun executeAsync(): CompletableFuture<ExecutionResult> {
        val action = generateAction();
        return graphql.executeAsync(action)
    }

    protected fun executeSubscription(): Publisher<ExecutionResult> {
        val action = generateAction()
        return graphql
            .execute(action)
            .getData<Publisher<ExecutionResult>>()
    }

    abstract fun run(): Unit
}
