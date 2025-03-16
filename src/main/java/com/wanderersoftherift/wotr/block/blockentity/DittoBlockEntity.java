package com.dimensiondelvers.dimensiondelvers.block.entity;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.init.ModBlockEntityTypes;
import com.dimensiondelvers.dimensiondelvers.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;

import javax.annotation.Nullable;
import java.util.List;


public class DittoBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {
	private ItemStack item;

	@Nullable
	protected ResourceKey<LootTable> lootTable;
	protected long lootTableSeed;

	public DittoBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.DITTO_BLOCK_ENTITY.get(), pos, state);
		this.item = ModItems.DITTO_BLOCK_ITEM.toStack();
	}

	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
		super.saveAdditional(tag, provider);
		if (!this.trySaveLootTable(tag) && !this.item.isEmpty()) {
			tag.put("item", this.item.save(provider));
		}

	}

	public boolean canTakeItem(Container target, int slot, ItemStack stack) {
		return false;
	}

	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
		super.loadAdditional(tag, provider);
		if (!this.tryLoadLootTable(tag)) {
			if (tag.contains("item", 10)) {
				this.item = (ItemStack)ItemStack.parse(provider, tag.getCompound("item")).orElse(ItemStack.EMPTY);
			} else {
				this.item = ItemStack.EMPTY;
			}
		}
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	public CompoundTag getUpdateTag(HolderLookup.Provider provider) {

		DimensionDelvers.LOGGER.debug(this.item.getItemName().getString());
		return this.saveCustomOnly(provider);
	}

	@Nullable
	public ResourceKey<LootTable> getLootTable() {
		return this.lootTable;
	}

	public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
		this.lootTable = lootTable;
	}

	public long getLootTableSeed() {
		return this.lootTableSeed;
	}

	public void setLootTableSeed(long p_309580_) {
		this.lootTableSeed = p_309580_;
	}

	protected void collectImplicitComponents(DataComponentMap.Builder builder) {
		super.collectImplicitComponents(builder);
		builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
	}

	protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
		super.applyImplicitComponents(input);
		this.item = ((ItemContainerContents)input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY)).copyOne();
	}

	public void removeComponentsFromTag(CompoundTag tag) {
		super.removeComponentsFromTag(tag);
		tag.remove("item");
	}

	public ItemStack getTheItem() {
		this.unpackLootTable((Player)null);
		return this.item;
	}

	public void setTheItem(ItemStack item) {
		this.unpackLootTable((Player)null);
		this.item = item;
	}

	public BlockEntity getContainerBlockEntity() {
		return this;
	}
}