package io.javalin.graphql.example

import com.expedia.graphql.annotations.GraphQLContext
import io.javalin.graphql.Query


class QueryExample(message: String) : Query {
    fun hello(): String = message

    fun context(@GraphQLContext context: ContextExample): ContextExample = context
}
