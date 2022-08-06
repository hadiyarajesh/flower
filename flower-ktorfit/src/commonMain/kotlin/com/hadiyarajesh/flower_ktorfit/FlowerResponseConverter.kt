package com.hadiyarajesh.flower_ktorfit

import com.hadiyarajesh.flower_core.*
import com.hadiyarajesh.flower_ktorfit.common.toCommonResponse
import de.jensklingenberg.ktorfit.adapter.ResponseConverter
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KClass

class FlowerResponseConverter : ResponseConverter {
    override fun supportedType(returnTypeName: String): Boolean {
        return returnTypeName == "kotlinx.coroutines.flow.Flow"
    }

    override fun <PRequest : Any> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>
    ): Any {
        return flow<ApiResponse<Any>> {
            try {
                val (info, response) = requestFunction()

                val kotlinType = info.kotlinType ?: throw IllegalArgumentException("Type must match Flow<ApiResponse<YourModel>>")
                val modelKTypeProjection = if (kotlinType.arguments.size >= 0) kotlinType.arguments[0] else throw IllegalArgumentException("Type must match Flow<ApiResponse<YourModel>>")
                val modelKType = modelKTypeProjection.type ?: throw IllegalArgumentException("Could not get a KType of your model class or return type")
                val modelClass = (modelKType.classifier as? KClass<*>?) ?: throw IllegalArgumentException("Could not parse your model class or return type to a KClass")

                emit(ApiResponse.create(response.toCommonResponse(TypeInfo(modelClass, modelKType.platformType, modelKType))))
            } catch (e: Throwable) {
                emit(ApiResponse.Companion.create(e))
            }
        }
    }
}