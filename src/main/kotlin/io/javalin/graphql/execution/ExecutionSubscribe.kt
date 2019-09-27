package io.javalin.graphql.execution

import graphql.GraphQL
import io.javalin.graphql.subscription.Subscriber
import io.javalin.websocket.WsMessageContext

class ExecutionSubscribe(private val ctx: WsMessageContext, graphql: GraphQL): Run(graphql) {

    override fun run() {
        executeSubscription()
            .subscribe(Subscriber(ctx))
    }
}
