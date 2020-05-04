/**
 * Copyright (C) 2018 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package workshop.akbolatss.xchangesrates.base.resource

/**
 * Base Class for handling errors/failures/exceptions.
 * Every feature specific failure should extend [FeatureFailure] class.
 */
sealed class Failure {
    open var errorMessage: String? = null

    data class ServerError(override var errorMessage: String?) : Failure()
    data class UnknownException(override var errorMessage: String?) : Failure()
    data class DatabaseError(override var errorMessage: String?) : Failure()

    /** * Extend this class for feature specific failures.*/
    abstract class FeatureFailure(override var errorMessage: String? = null) : Failure()

    object UseCaseError : FeatureFailure()


    object SnapshotListIsEmpty : FeatureFailure()
    object SnapshotAlreadyExists : FeatureFailure()
    object SnapshotNotFound : FeatureFailure()
    object ChartNotLoaded : FeatureFailure()
}

