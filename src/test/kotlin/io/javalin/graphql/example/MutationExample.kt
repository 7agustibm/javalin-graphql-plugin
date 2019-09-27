package io.javalin.graphql.example

import io.javalin.graphql.Mutation

class MutationExample(message: String) : Mutation {
    fun changeMessage(newMessage: String): String {
        message = newMessage
        return message
    }
}
