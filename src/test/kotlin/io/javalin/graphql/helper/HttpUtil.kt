package io.javalin.graphql.helper

import io.javalin.Javalin
import kong.unirest.JsonNode
import kong.unirest.Unirest

class HttpUtil(javalin: Javalin) {

    @JvmField
    val origin: String = "http://localhost:" + javalin.port()

    fun post(path: String, body: String) = Unirest.post(origin + path).body(JsonNode(body)).asString()
}
