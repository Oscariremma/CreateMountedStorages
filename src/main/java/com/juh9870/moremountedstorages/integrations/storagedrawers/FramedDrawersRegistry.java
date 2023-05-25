package com.juh9870.moremountedstorages.integrations.storagedrawers;

import com.juh9870.moremountedstorages.ContraptionStorageRegistry;
import com.juh9870.moremountedstorages.Utils;
import eutros.framedcompactdrawers.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.Lazy;

public class FramedDrawersRegistry extends StorageDrawersRegistry {

	public static final String REGISTRY_NAME = Utils.constructId("framedcompactdrawers", "drawer");

	public static final Lazy<ContraptionStorageRegistry> INSTANCE = getInstance(REGISTRY_NAME);

	@Override
	public BlockEntityType<?>[] affectedStorages() {
		return new BlockEntityType[]{
				ModBlocks.Tile.standardDrawers1,
				ModBlocks.Tile.standardDrawers2,
				ModBlocks.Tile.standardDrawers4,
		};
	}
}
