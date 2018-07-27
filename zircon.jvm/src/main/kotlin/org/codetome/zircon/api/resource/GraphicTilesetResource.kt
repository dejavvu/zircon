package org.codetome.zircon.api.resource

import org.codetome.zircon.api.tileset.Tileset
import org.codetome.zircon.internal.tileset.impl.DefaultTileTextureMetadata
import org.codetome.zircon.internal.tileset.impl.TilesetLoaderRegistry
import org.codetome.zircon.api.tileset.MetadataPickingStrategy
import org.codetome.zircon.internal.util.rex.unZipIt
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.File


/**
 * This enum encapsulates the means of loading graphic tilesets.
 * You can either use a built-in tileset or you can load your own using [GraphicTilesetResource.loadGraphicTileset]
 */
enum class GraphicTilesetResource(private val tilesetName: String,
                                  val size: Int,
                                  val path: String = "zircon.jvm/src/main/resources/graphic_tilesets/${tilesetName}_${size}x$size.zip") {

    NETHACK_16X16("nethack", 16);

    /**
     * Loads this built-in tileset as a tiled [Tileset].
     */
    @JvmOverloads
    fun toFont(metadataPickingStrategy: MetadataPickingStrategy,
               cacheFonts: Boolean = true) =
            loadGraphicTileset(
                    path = path,
                    metadataPickingStrategy = metadataPickingStrategy,
                    cacheFonts = cacheFonts)


    companion object {

        /**
         * Loads a tileset from the given `sourceZip` as a tiled [Tileset].
         * *Note that* it is your responsibility to supply the proper parameters for
         * this method!
         */
        @JvmStatic
        @JvmOverloads
        fun loadGraphicTileset(path: String,
                               metadataPickingStrategy: MetadataPickingStrategy,
                               cacheFonts: Boolean = true): Tileset {
            val files = unZipIt(File(path).inputStream(), createTempDir())
            val tileInfoSource = files.first { it.name == "tileinfo.yml" }.bufferedReader().use {
                it.readText()
            }
            val yaml = Yaml(Constructor(TileInfo::class.java))
            val tileInfo: TileInfo = yaml.load(tileInfoSource) as TileInfo
            // TODO: multi-file support
            val file = tileInfo.files.first()
            val metadata = file.tiles.mapIndexed { i, tileData: TileData ->
                val (name, _, char) = tileData
                val cleanName = name.toLowerCase().trim()
                tileData.tags = tileData.tags
                        .plus(cleanName.split(" ").map { it }.toSet())
                if (char == ' ') {
                    tileData.char = cleanName.first()
                }
                tileData.x = i.rem(file.tilesPerRow)
                tileData.y = i.div(file.tilesPerRow)
                DefaultTileTextureMetadata(
                        char = tileData.char,
                        tags = tileData.tags,
                        x = tileData.x,
                        y = tileData.y)
            }.groupBy { it.char }

            // TODO: figure out something for multi-file tilesets

            return TilesetLoaderRegistry.getCurrentFontLoader().fetchTiledFont(
                    width = tileInfo.size,
                    height = tileInfo.size,
                    path = files.first { it.name == file.name }.absolutePath,
                    cacheFonts = cacheFonts,
                    metadataTile = metadata,
                    metadataPickingStrategy = metadataPickingStrategy)
        }
    }

    data class TileInfo(var name: String,
                        var size: Int,
                        var files: List<TileFile>) {
        constructor() : this(name = "",
                size = 0,
                files = listOf())
    }

    data class TileFile(var name: String,
                        var tilesPerRow: Int,
                        var tiles: List<TileData>) {
        constructor() : this(name = "",
                tilesPerRow = 0,
                tiles = listOf())
    }

    data class TileData(var name: String,
                        var tags: Set<String> = setOf(),
                        var char: Char = ' ',
                        var description: String = "",
                        var x: Int = -1,
                        var y: Int = -1) {
        constructor() : this(name = "")

    }
}
