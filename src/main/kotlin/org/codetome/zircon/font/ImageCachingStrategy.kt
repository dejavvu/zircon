package org.codetome.zircon.font

import org.codetome.zircon.TextCharacter

/**
 * An [ImageCachingStrategy] is responsible for generating cache keys for [TextCharacter]s.
 * For a [Font] which is a graphical tileset for example and uses tags to differentiate between
 * textures a caching strategy will generate hashes based on tags, while for a cp437 [Font]
 * only the character and the colors might be used.
 */
interface ImageCachingStrategy {

    /**
     * Generates a cache key for a given [TextCharacter].
     */
    fun generateCacheKeyFor(textCharacter: TextCharacter): Int
}