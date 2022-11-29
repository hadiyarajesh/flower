/*
 *  Copyright (C) 2022 Rajesh Hadiya
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.hadiyarajesh.flower_core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

inline fun <DB : Any> dbResource(
    crossinline fetchFromLocal: suspend () -> Flow<DB>,
) = flow<Resource<DB>> {
    emit(Resource.loading(data = null))

    val localData = fetchFromLocal().first()
    emit(Resource.success(data = localData))
}
