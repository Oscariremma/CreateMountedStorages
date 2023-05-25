package com.juh9870.moremountedstorages;

import com.mojang.serialization.Codec;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.Contraption;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.*;
//import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.tags.ITagManager;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

public  class ContraptionStorageRegistry {
	public static final ItemStackHandler dummyHandler = new ItemStackHandler();

	public static final ResourceLocation ID = new ResourceLocation(MoreMountedStorages.ID, "contraptionstorage");

	public static final RegistryBuilder<ContraptionStorageRegistry> builder = new RegistryBuilder<ContraptionStorageRegistry>().setName(ResourceLocation.tryParse(MoreMountedStorages.ID));
	public static final DeferredRegister<ContraptionStorageRegistry> STORAGES = DeferredRegister.create(ID, MoreMountedStorages.ID);


	public static final Supplier<IForgeRegistry<ContraptionStorageRegistry>> REGISTRY = STORAGES.makeRegistry(RegistryBuilder::new);
	public static final String REGISTRY_NAME = "StorageRegistryId";
	private static Map<BlockEntityType<?>, ContraptionStorageRegistry> BlockEntityMappingsCache = null;

	public static void initCache() {
		if (BlockEntityMappingsCache != null) return;
		BlockEntityMappingsCache = new HashMap<>();
		ContraptionStorageRegistry other;
		for (ContraptionStorageRegistry registry : REGISTRY.get()) {
			for (BlockEntityType<?> BlockEntityType : registry.affectedStorages()) {
				if ((other = BlockEntityMappingsCache.get(BlockEntityType)) != null) {
					if (other.getPriority() == registry.getPriority() && other.getPriority() != Priority.DUMMY) {
						throw new RegistryConflictException(BlockEntityType, other.getClass(), registry.getClass());
					} else if (!registry.getPriority().isOverwrite(other.getPriority()))
						continue;
				}
				BlockEntityMappingsCache.put(BlockEntityType, registry);
			}
		}
	}

	/**
	 * Returns registry entry that handles provided entity type, or null if no matching entry found
	 *
	 * @param type Type of tile entity
	 * @return matching registry entry, or null if nothing is found
	 */
	@Nullable
	public static ContraptionStorageRegistry forBlockEntity(BlockEntityType<?> type) {
		return BlockEntityMappingsCache.get(type);
	}

	/**
	 * Helper method to conditionally register handlers. Registers value from {@code supplier} parameter if {@code condition} returns true, otherwise generates new {@link DummyHandler} and registers it
	 *
	 * @param registry     registry for entry registering
	 * @param condition    Loading condition supplier
	 * @param registryName Name to register the entry under
	 * @param supplier     Supplier to get the entry
	 */
	public static void registerConditionally(IForgeRegistry<ContraptionStorageRegistry> registry, Supplier<Boolean> condition, String registryName, Supplier<ContraptionStorageRegistry> supplier) {
		ContraptionStorageRegistry entry;
		if (condition.get()) {
			entry = supplier.get();
		} else {
			entry = new DummyHandler();
		}
		registry.register(registryName ,entry);
	}

	/**
	 * Helper method to conditionally register handlers based on if specified mod is loaded. Registers value from {@code supplier} parameter if specified mod is loaded, otherwise generates new {@link DummyHandler} and registers it
	 *
	 * @param registry     registry for entry registering
	 * @param modid        Required mod ID
	 * @param registryName Name to register the entry under
	 * @param supplier     Supplier to get the entry
	 */
	public static void registerIfModLoaded(IForgeRegistry<ContraptionStorageRegistry> registry, String modid, String registryName, Supplier<ContraptionStorageRegistry> supplier) {
		registerConditionally(registry, () -> ModList.get().isLoaded(modid), registryName, supplier);
	}

	/**
	 * Helper method to unconditionally register handlers
	 *
	 * @param registry     registry for entry registering
	 * @param registryName Name to register the entry under
	 * @param supplier     Supplier to get the entry
	 */
	public static void register(IForgeRegistry<ContraptionStorageRegistry> registry, String registryName, Supplier<ContraptionStorageRegistry> supplier) {
		registerConditionally(registry, () -> true, registryName, supplier);
	}

	/**
	 * Helper method to get default item handler capability from tile entity
	 *
	 * @param te BlockEntity to get handler from
	 * @return IItemHandler from {@link CapabilityItemHandler#ITEM_HANDLER_CAPABILITY} capability
	 */
	protected static IItemHandler getHandlerFromDefaultCapability(BlockEntity te) {
		return te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(dummyHandler);
	}

	/**
	 * Helper method for getting registry instance
	 *
	 * @param id registry name
	 * @return Lazy with given name
	 */
	public static Lazy<ContraptionStorageRegistry> getInstance(String id) {
		return Lazy.of(() -> REGISTRY.get().getValue(new ResourceLocation(id)));
	}

	/**
	 * Method for getting registry priority. For additional info see {@link Priority}
	 *
	 * @return registry priority
	 */
	public Priority getPriority() {
		throw new NotImplementedException("getPriority() is not implemented for " + getClass().getName());
	};

	/**
	 * @return array of Tile Entity types handled by this registry
	 */
	public BlockEntityType<?>[] affectedStorages(){
		throw new NotImplementedException("affectedStorages() is not implemented for " + getClass().getName());
	};

	/**
	 * @param te Tile Entity
	 * @return true if given tile entity can be used as mounted storage
	 */
	public boolean canUseAsStorage(BlockEntity te) {
		return true;
	}


	/**
	 * called when the specific block at pos is about to be moved
	 * @param world           the level where the block is
	 * @param forcedDirection the direction that the block is about to move to
	 * @param frontier        queue containing the blocks to be moved
	 *                        push new blocks here if they should be moved along with this one
	 * @param visited         blocks visited, including this one
	 * @param pos             position of the block
	 * @param state           block state of the block
	 * @return indicates whether this block can be moved
	 * 		   true: move as usual
	 * 		   false: reject to move, this will fail the contraption assemble process
	 * @see Contraption#moveBlock(Level, Direction, Queue, Set)
	 */
	public boolean moveBlock(Level world, Direction forcedDirection, Queue<BlockPos> frontier, Set<BlockPos> visited,
							 BlockPos pos, BlockState state)
	{
		return true;
	}

	/**
	 * @param te original Tile Entity
	 * @return Item handler to be used in contraption or null if default logic should be used
	 */
	public ContraptionItemStackHandler createHandler(BlockEntity te) {
		return null;
	}

	/**
	 * Returns {@link  ContraptionItemStackHandler} deserialized from NBT
	 *
	 * @param nbt serialized NBT
	 * @return deserialized handler
	 */
	public ContraptionItemStackHandler deserializeHandler(CompoundTag nbt) {
		throw new NotImplementedException();
	}

	/**
	 * Helper method for deserializing handler from NBT
	 *
	 * @param handler handler to deserialize
	 * @param nbt     serialized NBT
	 * @return Deserialized handler
	 */
	protected final <T extends ContraptionItemStackHandler> T deserializeHandler(T handler, CompoundTag nbt) {
		handler.deserializeNBT(nbt);
		return handler;
	}

	/**
	 * Registry priority enum
	 */
	public enum Priority {
		/**
		 * Dummy priority, use this in case if your registry is a dummy and should be overwritten by any better option
		 */
		DUMMY {
			@Override
			public boolean isOverwrite(Priority other) {
				return false;
			}
		},
		/**
		 * Add-on priority, use this if your registry is coming from an add-on to the external mod and should be overwritten if official support is added
		 */
		ADDON {
			@Override
			public boolean isOverwrite(Priority other) {
				return other == DUMMY;
			}
		},
		/**
		 * Native mod priority, use this if your registry is a part of the mod it's adding support to
		 */
		NATIVE {
			@Override
			public boolean isOverwrite(Priority other) {
				return other != NATIVE;
			}
		};

		public abstract boolean isOverwrite(Priority other);
	}

	public static class DummyHandler extends ContraptionStorageRegistry {
		@Override
		public boolean canUseAsStorage(BlockEntity te) {
			return false;
		}

		@Override
		public Priority getPriority() {
			return Priority.DUMMY;
		}

		@Override
		public BlockEntityType<?>[] affectedStorages() {
			return new BlockEntityType[0];
		}
	}

	public static class RegistryConflictException extends RuntimeException {
		public RegistryConflictException(BlockEntityType<?> teType, Class<? extends ContraptionStorageRegistry> a, Class<? extends ContraptionStorageRegistry> b) {
			super("Registry conflict: registries " + a.getName() + " and " + b.getName() + " tried to register the same tile entity");
		}
	}
}