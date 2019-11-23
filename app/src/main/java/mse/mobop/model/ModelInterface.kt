package mse.mobop.model

import androidx.room.Ignore

abstract class ModelInterface(@Ignore var modelId: Any?) {
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}