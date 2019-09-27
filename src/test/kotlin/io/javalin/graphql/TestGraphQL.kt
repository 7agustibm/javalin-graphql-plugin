package io.javalin.graphql

import io.javalin.Javalin
import io.javalin.graphql.example.ContextExample
import io.javalin.graphql.example.MutationExample
import io.javalin.graphql.example.QueryExample
import io.javalin.graphql.example.SubscriptionExample
import io.javalin.graphql.helper.HttpUtil
import kong.unirest.json.JSONObject
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestGraphQL {

    private val graphqlPath = "/graphql"
    private val message = "Hello World"
    private val newMessage = "hi"

    private fun shortTimeoutServer(test: (http: HttpUtil) -> Unit) {
        val server = Javalin.create()
            .after { ctx -> ctx.req.asyncContext.timeout = 10 }
        server.start(0)
        JavalinGraphql
            .addPackage("io.javalin.graphql")
            .register(QueryExample(io.javalin.graphql.example.message))
            .register(MutationExample(io.javalin.graphql.example.message))
            .register(SubscriptionExample())
            .addContext(ContextExample())
            .build(graphqlPath, server)
        test(HttpUtil(server))
        server.stop()
    }

    @Test
    fun query() {
        shortTimeoutServer { httpUtil ->
            val response = httpUtil.post(graphqlPath, "{\"query\": \"{ hello }\"}").body
            val json = JSONObject(response)
            assertEquals(json.getJSONObject("data").getString("hello"), message)
        }
    }

    @Test
    fun mutation() = shortTimeoutServer { httpUtil ->
        val mutation = "mutation { changeMessage(newMessage: \\\"$newMessage\\\") }"
        val response = httpUtil.post(graphqlPath, "{\"query\": \"$mutation\"}").body
        val json = JSONObject(response)
        assertEquals(json.getJSONObject("data").getString("changeMessage"), newMessage)
    }

    @Test
    fun mutation_with_variables() = shortTimeoutServer { httpUtil ->
        val mutation = "mutation changeMessage(\$message: String!){changeMessage(newMessage: \$message)}"
        val variables = "{\"message\": \"$newMessage\"}"
        val response = httpUtil.post(graphqlPath, "{\"variables\": $variables, \"query\": \"$mutation\" }").body
        val json = JSONObject(response)
        assertEquals(json.getJSONObject("data").getString("changeMessage"), newMessage)
    }

    @Test
    fun context() = shortTimeoutServer { httpUtil ->
        val response = httpUtil.post(graphqlPath, "{\"query\": \"{ context { hello, hi} }\"}").body
        val json = JSONObject(response)
        assertEquals(json.getJSONObject("data").getJSONObject("context").getString("hello"), ContextExample().hello)
        assertEquals(json.getJSONObject("data").getJSONObject("context").getString("hi"), ContextExample().hi)
    }

}
