package org.codetome.zircon.internal.tileset.impl

import org.codetome.zircon.api.tileset.TileTextureMetadata
import org.codetome.zircon.api.tileset.Tileset
import org.codetome.zircon.api.tileset.TilesetLoader
import org.codetome.zircon.api.tileset.MetadataPickingStrategy
import org.codetome.zircon.internal.util.impl.NoOpCache
import org.codetome.zircon.internal.tileset.transformer.Java2DTileTextureCloner
import org.codetome.zircon.internal.tileset.transformer.Java2DTileTextureColorizer
import org.codetome.zircon.platform.factory.CacheFactory
import java.io.File
import javax.imageio.ImageIO

class Java2DTilesetLoader : TilesetLoader {

    override fun fetchTiledFont(width: Int,
                                height: Int,
                                path: String,
                                cacheFonts: Boolean,
                                metadataTile: Map<Char, List<TileTextureMetadata>>,
                                metadataPickingStrategy: MetadataPickingStrategy): Tileset {
        return Java2DTiledTileset(
                source = ImageIO.read(File(path)),
                metadataTile = metadataTile,
                width = width,
                height = height,
                cache = if (cacheFonts) {
                    CacheFactory.create()
                } else {
                    NoOpCache()
                },
                regionTransformers = TILE_TRANSFORMERS)
    }

    companion object {
        private val TILE_TRANSFORMERS = listOf(
                Java2DTileTextureCloner(),
                Java2DTileTextureColorizer())
    }
}
