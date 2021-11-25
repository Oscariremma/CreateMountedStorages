package com.juh9870.moremountedstorages.helpers;

import com.simibubi.create.api.contraption.ContraptionItemStackHandler;
import com.simibubi.create.api.contraption.ContraptionStorageRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public abstract class WrapperStackHandler extends ContraptionItemStackHandler {
	private final ItemStackHandler handler;

	public WrapperStackHandler(ItemStackHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setSize(int size) {
		handler.setSize(size);
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		handler.setStackInSlot(slot, stack);
	}

	@Override
	public int getSlots() {
		return handler.getSlots();
	}

	@Nonnull
	@Override
	public ItemStack getStackInSlot(int slot) {
		return handler.getStackInSlot(slot);
	}

	@Nonnull
	@Override
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		return handler.insertItem(slot, stack, simulate);
	}

	@Nonnull
	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		return handler.extractItem(slot, amount, simulate);
	}

	@Override
	public int getSlotLimit(int slot) {
		return handler.getSlotLimit(slot);
	}

	@Override
	public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
		return handler.isItemValid(slot, stack);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		handler.deserializeNBT(nbt);
	}
}
