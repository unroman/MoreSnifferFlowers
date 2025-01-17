package net.abraxator.moresnifferflowers.blockentities;

import net.abraxator.moresnifferflowers.entities.CorruptedProjectile;
import net.abraxator.moresnifferflowers.init.ModBlockEntities;
import net.abraxator.moresnifferflowers.init.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;

public class CorruptedSludgeBlockEntity extends ModBlockEntity implements GameEventListener.Provider<CorruptedSludgeBlockEntity.CorruptedSludgeListener> {
    public CorruptedSludgeListener corruptedSludgeListener;
    
    public CorruptedSludgeBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CORRUPTED_SLUDGE.get(), pPos, pBlockState);
        this.corruptedSludgeListener = new CorruptedSludgeListener(new BlockPositionSource(pPos));
    }

    @Override
    public CorruptedSludgeListener getListener() {
        return corruptedSludgeListener;
    }

    public static class CorruptedSludgeListener implements GameEventListener {
        private PositionSource positionSource;

        public CorruptedSludgeListener(PositionSource positionSource) {
            this.positionSource = positionSource;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.positionSource;
        }

        @Override
        public int getListenerRadius() {
            return GameEvent.BLOCK_DESTROY.value().notificationRadius();
        }

        @Override
        public boolean handleGameEvent(ServerLevel pLevel, Holder<GameEvent> pGameEvent, GameEvent.Context pContext, Vec3 pPos) {
            if(pGameEvent.is(GameEvent.BLOCK_DESTROY) && pContext.affectedState().is(ModTags.ModBlockTags.CORRUPTED_BLOCKS) && !pPos.equals(this.positionSource.getPosition(pLevel).get()) && pContext.sourceEntity() instanceof Player player) {
                Vec3 center = this.getListenerSource().getPosition(pLevel).get();
                var radius = 2.5;
                var projectileNumber = pLevel.random.nextInt(10) + 2;
                int attempts = 0;
                Set<Vec3> placed = new HashSet<>();
                
                for(int i = 0; i < projectileNumber; i++) {
                    generatePoint(placed, center, radius, pLevel);
                }
            }

            return false;
        }

        private void generatePoint(Set<Vec3> placed, Vec3 center, double radius, ServerLevel pLevel) {
            var random = pLevel.random;

            double theta = 2 * Mth.PI * random.nextDouble();
            double phi = Math.acos(2 * random.nextDouble() - 1);

            double xg = center.x + radius * Mth.sin((float) phi) * Mth.cos((float) theta);
            double yg = center.y + radius * Mth.sin((float) phi) * Mth.sin((float) theta);
            double zg = center.z + radius * Mth.cos((float) phi);
            var vec3 = new Vec3(xg, yg, zg);
            
            if (placed.stream().noneMatch(vec31 -> AABB.ofSize(vec3, 1, 1, 1).contains(vec31)) && pLevel.getBlockState(BlockPos.containing(vec3)).canBeReplaced()) {
                var pos = this.positionSource.getPosition(pLevel).get();
                var x = random.nextDouble() * 0.5;
                var y = random.nextDouble() * 0.5;
                var z = random.nextDouble() * 0.5;
                CorruptedProjectile projectile = new CorruptedProjectile(pLevel);
                projectile.setPos(vec3);
                Vec3 dir = new Vec3(projectile.getX() - pos.x, projectile.getY() - pos.y, projectile.getZ() - pos.z).normalize().multiply(x, y, z);
                projectile.setDeltaMovement(dir);
                pLevel.addFreshEntity(projectile);

                //level.sendParticles(ModParticles.CARROT.get(), vec3.x, vec3.y, vec3.z, 1, 0D, 0D, 0D, 0D);
                placed.add(vec3);
            }
        }
    }
}
