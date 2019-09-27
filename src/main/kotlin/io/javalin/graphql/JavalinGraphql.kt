package io.javalin.graphql

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.toSchema
import graphql.GraphQL
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.*
import io.javalin.graphql.execution.Execution
import io.javalin.graphql.execution.ExecutionSubscribe
import io.javalin.graphql.execution.Run

object JavalinGraphql {
    private var queries: List<TopLevelObject> = emptyList()
    private var mutations: List<TopLevelObject> = emptyList()
    private var subscriptions: List<TopLevelObject> = emptyList()
    private var context: Any? = null
    private var packages: List<String> = emptyList()
    lateinit var build: GraphQL


    @JvmStatic
    fun register(vararg queries: Query): JavalinGraphql {
        JavalinGraphql.queries = queries.asList().map { TopLevelObject(it) }
        return JavalinGraphql
    }

    @JvmStatic
    fun register(vararg mutations: Mutation): JavalinGraphql {
        JavalinGraphql.mutations = mutations.asList().map { TopLevelObject(it) }
        return JavalinGraphql
    }

    @JvmStatic
    fun register(vararg subscriptions: Subscription): JavalinGraphql {
        JavalinGraphql.subscriptions = subscriptions.asList().map { TopLevelObject(it) }
        return JavalinGraphql
    }

    @JvmStatic
    fun addPackage(`package`: String): JavalinGraphql {
        packages = listOf(*packages.toTypedArray(), `package`)
        return JavalinGraphql
    }

    @JvmStatic
    fun addContext(context: Any): JavalinGraphql {
        JavalinGraphql.context = context
        return JavalinGraphql
    }

    @JvmStatic
    fun build(pathService: String, app: Javalin) {
        val config = SchemaGeneratorConfig(supportedPackages = packages)
        val schema = toSchema(
            config = config,
            queries = queries,
            mutations = mutations,
            subscriptions = subscriptions
        )
        build = GraphQL.newGraphQL(schema).build()
        app
            .routes {
                path(pathService) {
                    get { it.contentType("text/html; charset=UTF-8").result(this.getGraphQLi())}
                    post { ctx ->
                        val body = ctx.bodyAsClass(Map::class.java)
                        runGraphQL(body, Execution(ctx, build))
                    }
                    ws(pathService) { ws ->
                        ws.onMessage { ctx ->
                            val body = ctx.message(Map::class.java)
                            runGraphQL(body, ExecutionSubscribe(ctx, build))
                        }
                    }
                }
            }
    }

    private fun runGraphQL(body: Map<*, *>, execution: Run) {
        val query = body.get("query").toString()
        val variables: Map<String, Any> =
            if (body["variables"] == null) emptyMap() else body["variables"] as Map<String, Any>
        execution
            .withContext(context)
            .withQuery(query)
            .withVariables(variables)
            .run()
    }

    private fun getGraphQLi() =JavalinGraphql.javaClass.classLoader.getResourceAsStream("graphqli/index.html")
}
