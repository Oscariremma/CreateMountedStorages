package com.juh9870.moremountedstorages.integrations.create;

import com.juh9870.moremountedstorages.Config;
import com.juh9870.moremountedstorages.ContraptionItemStackHandler;
import com.juh9870.moremountedstorages.ContraptionStorageRegistry;
import com.juh9870.moremountedstorages.Utils;
import com.juh9870.moremountedstorages.helpers.InfiniteItemStackHandler;
import com.simibubi.create.AllBlockEntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

public class CreativeCrateRegistry extends ContraptionStorageRegistry {
	public static final String REGISTRY_NAME = Utils.constructId("create", "creative_crate");
	public static final Lazy<ContraptionStorageRegistry> INSTANCE = getInstance(REGISTRY_NAME);
	public static final Config.PriorityRegistryInfo CONFIG = new Config.PriorityRegistryInfo("creative_crate", "Create Creative Crate", 0);
	@Override
	public Priority getPriority() {
		return Priority.ADDON;
	}

	@Override
	public BlockEntityType<?>[] affectedStorages() {
		return new BlockEntityType[]{
				AllBlockEntityTypes.CREATIVE_CRATE.get()
		};
	}

	@Override
	public boolean canUseAsStorage(BlockEntity te) {
		return super.canUseAsStorage(te) && CONFIG.isEnabled();
	}

	@Override
	public ContraptionItemStackHandler deserializeHandler(CompoundTag nbt) {
		return deserializeHandler(new CreativeCrateItemHandler(() -> ItemStack.EMPTY), nbt);
	}


	public static class CreativeCrateItemHandler extends InfiniteItemStackHandler {
		public CreativeCrateItemHandler(Supplier<ItemStack> suppliedItemStack) {
			super(suppliedItemStack);
		}

		@Override
		protected ContraptionStorageRegistry registry() {
			return INSTANCE.get();
		}

		@Override
		protected String getRegistryName() {
			return REGISTRY_NAME;
		}

		@Override
		public int getPriority() {
			return super.getPriority();
		}
	}
}