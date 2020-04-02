package com.notvanilla.kevlar.block.entity;

import com.notvanilla.kevlar.block.BlockBreakerBlock;
import com.notvanilla.kevlar.container.Generic1x1Container;
import com.notvanilla.kevlar.mixin.BlockSoundGroupAccessor;
import com.notvanilla.kevlar.mixin.EntityAccessor;
import com.notvanilla.kevlar.mixin.TurtleEggBlockAccessor;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.block.*;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.container.Container;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShearsItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockBreakerBlockEntity extends LootableContainerBlockEntity implements Tickable {

    private DefaultedList<ItemStack> items = DefaultedList.ofSize(1, ItemStack.EMPTY);

    private int fakeEntityId;
    private int miningSlot = -1;
    private ItemStack miningStack = ItemStack.EMPTY;
    private long startMiningTime;
    private int lastBreakAnimationProgress = -1;

    public BlockBreakerBlockEntity() {
        super(KevlarBlockEntities.BLOCK_BREAKER);
    }

    @Override
    public void setLocation(World world, BlockPos pos) {
        super.setLocation(world, pos);
        if (!world.isClient)
            fakeEntityId = EntityAccessor.getMaxEntityId().incrementAndGet();
    }

    @Override
    protected DefaultedList<ItemStack> getInvStackList() {
        return items;
    }

    @Override
    protected void setInvStackList(DefaultedList<ItemStack> list) {
        this.items = list;
    }

    @Override
    protected Text getContainerName() {
        return new TranslatableText("container.kevlar.block_breaker");
    }

    @Override
    protected Container createContainer(int syncId, PlayerInventory playerInventory) {
        return new Generic1x1Container(syncId, playerInventory, this);
    }

    @Override
    public int getInvSize() {
        return 9;
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        items = DefaultedList.ofSize(getInvSize(), ItemStack.EMPTY);
        if (!deserializeLootTable(tag)) {
            Inventories.fromTag(tag, items);
        }
        if (tag.contains("MiningSlot", NbtType.NUMBER)) {
            miningSlot = tag.getInt("MiningSlot");
            miningStack = ItemStack.fromTag(tag.getCompound("MiningStack"));
            startMiningTime = tag.getLong("StartMiningTime");
        } else {
            miningSlot = -1;
            miningStack = ItemStack.EMPTY;
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (!serializeLootTable(tag)) {
            Inventories.toTag(tag, items);
        }
        if (miningSlot != -1) {
            tag.putByte("MiningSlot", (byte) miningSlot);
            tag.put("MiningStack", miningStack.toTag(new CompoundTag()));
            tag.putLong("StartMiningTime", startMiningTime);
        }
        return tag;
    }

    public boolean isMining() {
        return miningSlot >= 0 && miningSlot < getInvSize() && !miningStack.isEmpty();
    }

    public void startMining(int slot, float initialProgress) {
        assert world != null;

        miningSlot = slot;
        if (slot < 0 || slot >= getInvSize())
            miningStack = ItemStack.EMPTY;
        else
            miningStack = getInvStack(slot);

        if (isMining()) {
            startMiningTime = world.getTime();
            int animationProgress = (int) (initialProgress * 10);
            world.setBlockBreakingInfo(fakeEntityId, getPosInFront(), animationProgress);
            lastBreakAnimationProgress = animationProgress;
        }
    }

    public void finishMining() {
        assert world != null;

        BlockPos posInFront = getPosInFront();
        world.setBlockBreakingInfo(fakeEntityId, posInFront, -1);
        BlockState stateInFront = world.getBlockState(posInFront);
        Block blockInFront = stateInFront.getBlock();

        if (!(blockInFront instanceof CommandBlock) && !(blockInFront instanceof StructureBlock) && !(blockInFront instanceof JigsawBlock)) {
            world.playLevelEvent(null, 2001, posInFront, Block.getRawIdFromState(stateInFront)); // break
            boolean removed = world.removeBlock(posInFront, false);
            if (removed) {
                blockInFront.onBroken(world, posInFront, stateInFront);
            }
            boolean effectiveTool = stateInFront.getMaterial().canBreakByHand() || miningStack.isEffectiveOn(stateInFront);

            boolean silkTouch = EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, miningStack) != 0;

            boolean shouldCallAfterBreak = true;
            if (miningStack.getItem() instanceof ShearsItem) {
                if (stateInFront.matches(BlockTags.LEAVES)
                    || blockInFront == Blocks.COBWEB
                    || blockInFront == Blocks.GRASS
                    || blockInFront == Blocks.FERN
                    || blockInFront == Blocks.DEAD_BUSH
                    || blockInFront == Blocks.VINE
                    || blockInFront == Blocks.TRIPWIRE
                    || stateInFront.matches(BlockTags.WOOL)
                ) {
                    shouldCallAfterBreak = false;
                }
            }

            if (effectiveTool && shouldCallAfterBreak) {
                Block.dropStacks(stateInFront, world, posInFront, world.getBlockEntity(posInFront), null, miningStack);

                if (blockInFront instanceof BeehiveBlock) {
                    if (!silkTouch) { // vanilla bug?
                        world.updateHorizontalAdjacent(posInFront, blockInFront);
                    }
                } else if (blockInFront instanceof IceBlock) {
                    if (!silkTouch) {
                        if (world.dimension.doesWaterVaporize()) {
                            world.removeBlock(posInFront, false);
                        } else {
                            Material materialBelow = world.getBlockState(posInFront.down()).getMaterial();
                            if (materialBelow.blocksMovement() || materialBelow.isLiquid()) {
                                world.setBlockState(posInFront, Blocks.WATER.getDefaultState());
                            }
                        }
                    }
                } else if (blockInFront instanceof TurtleEggBlock) {
                    ((TurtleEggBlockAccessor) blockInFront).callBreakEgg(world, posInFront, stateInFront);
                }
            }

            // damage the item
            if (miningStack.damage(1, world.random, null))
                setInvStack(miningSlot, ItemStack.EMPTY);
        }

        miningSlot = -1;
        miningStack = ItemStack.EMPTY;
    }

    private BlockPos getPosInFront() {
        return pos.offset(getCachedState().get(BlockBreakerBlock.FACING));
    }

    @Override
    public void tick() {
        if (world == null)
            return;

        if (!isMining())
            return;

        BlockPos posInFront = getPosInFront();
        BlockState stateInFront = world.getBlockState(posInFront);
        ItemStack stack = getInvStack(miningSlot);
        if (!ItemStack.areItemsEqual(stack, miningStack)
                || !ItemStack.areTagsEqual(stack, miningStack)
                || stateInFront.isAir()
                || stateInFront.getHardness(world, posInFront) == -1) {
            abortMining();
            return;
        }

        long ticksMined = world.getTime() - startMiningTime;

        if (ticksMined % 4 == 0) {
            world.playSound(
                    null,
                    posInFront,
                    ((BlockSoundGroupAccessor) stateInFront.getSoundGroup()).getHitSound_common(),
                    SoundCategory.BLOCKS,
                    stateInFront.getSoundGroup().volume,
                    stateInFront.getSoundGroup().pitch
            );
        }

        float progress = BlockBreakerBlock.calcBlockBreakingDelta(world, posInFront, miningStack, stateInFront) * (ticksMined + 1);

        if (progress >= 1) {
            finishMining();
        } else {
            int animationProgress = (int) (progress * 10);
            if (animationProgress != lastBreakAnimationProgress) {
                world.setBlockBreakingInfo(fakeEntityId, posInFront, animationProgress);
                lastBreakAnimationProgress = animationProgress;
            }
        }
    }

    public void abortMining() {
        assert world != null;
        miningSlot = -1;
        miningStack = ItemStack.EMPTY;
        world.setBlockBreakingInfo(fakeEntityId, getPosInFront(), -1);
    }
}
