package org.dimdev.dimdoors.world.level;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class Counter implements ComponentV3 {
	private final ItemStack stack;
	private int counter;

	public Counter(ItemStack stack) {
		this.stack = stack;
	}

	public int increment() {
		return this.counter++;
	}

	public int count() {
		return this.counter;
	}

	public void reset() {
		this.counter = 0;
	}

	public static <T> Counter get(T provider) {
		return DimensionalDoorsComponents.COUNTER_COMPONENT_KEY.get(provider);
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		this.counter = tag.getInt("counter");
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putInt("counter", this.counter);
	}
}
