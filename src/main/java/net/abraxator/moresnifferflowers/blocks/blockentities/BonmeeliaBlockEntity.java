package net.abraxator.moresnifferflowers.blocks.blockentities;

import net.abraxator.moresnifferflowers.blocks.BonmeeliaBlock;
import net.abraxator.moresnifferflowers.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BonmeeliaBlockEntity extends GrowingCropBlockEntity {
    public boolean hasBottle;
    public boolean hasHint;
    private double hintDuration;
    
    public BonmeeliaBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.BONMEELIA.get(), pPos, pBlockState, 0.01F);
    }

    @Override
    public void tick() {
        hintLogic();
    }
    
    private void hintLogic() {
        if(hintDuration > 0) {
            hintDuration -= 0.01;
        }
        if(hintDuration == 0) {
            hasHint = false;
            BonmeeliaBlock.displayHint(level, worldPosition, getBlockState(), false);
        }
    }
    
    public void displayHint() {
        this.hasHint = true;
        this.hintDuration = 1F;
        BonmeeliaBlock.displayHint(level, worldPosition, getBlockState(), true);
    }
    
    public void giveBottle(ItemStack itemStack) {
        this.hasBottle = true;
        itemStack.shrink(1);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        hasHint = pTag.getBoolean("hint");
        hasBottle = pTag.getBoolean("bottle");
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag); 
        pTag.putBoolean("hint", hasHint);
        pTag.putBoolean("bottle", hasBottle);
    }
}
