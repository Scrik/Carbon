package com.lastabyss.carbon.generator.monument;

import java.util.Random;

import com.lastabyss.carbon.utils.nmsclasses.BlockFace;

class WorldGenMonumentXYDoubleRoomFitHelper implements WorldGenMonumentRoomFitHelper {

	public boolean a(WorldGenMonumentRoomDefinition definition) {
		if (definition.c[BlockFace.EAST.getId()] && !definition.b[BlockFace.EAST.getId()].d && definition.c[BlockFace.UP.getId()] && !definition.b[BlockFace.UP.getId()].d) {
			WorldGenMonumentRoomDefinition var2 = definition.b[BlockFace.EAST.getId()];
			return var2.c[BlockFace.UP.getId()] && !var2.b[BlockFace.UP.getId()].d;
		} else {
			return false;
		}
	}

	public WorldGenMonumentPiece getPiece(BlockFace face, WorldGenMonumentRoomDefinition definition, Random var3) {
		definition.d = true;
		definition.b[BlockFace.EAST.getId()].d = true;
		definition.b[BlockFace.UP.getId()].d = true;
		definition.b[BlockFace.EAST.getId()].b[BlockFace.UP.getId()].d = true;
		return new WorldGenMonumentDoubleXYRoom(face, definition, var3);
	}

}
