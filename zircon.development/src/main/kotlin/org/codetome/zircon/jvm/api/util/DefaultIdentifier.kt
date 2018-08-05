package org.codetome.zircon.jvm.api.util

import org.codetome.zircon.api.util.Identifier
import java.util.*

class DefaultIdentifier(val backend: UUID = UUID.randomUUID()) : Identifier {

    override fun compareTo(other: Identifier): Int {
        return backend.compareTo(fetchBackend(other))
    }

    override fun toString() = backend.toString()

    override fun equals(other: Any?) = backend == fetchBackend(other)

    override fun hashCode() = backend.hashCode()

    private fun fetchBackend(id: Any?): UUID = (id as? DefaultIdentifier?)?.backend ?: throw IllegalArgumentException(
            "Can't compare an UUIDIdentifier with an Identifier of a different class.")
}