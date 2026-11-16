// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.PathFinder;

public class WaterBoundPathNavigation extends PathNavigation {
    private boolean allowBreaching;

    public WaterBoundPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        allowBreaching = mob.getType() == net.minecraft.world.entity.EntityTypes.DOLPHIN;
        nodeEvaluator = new SwimNodeEvaluator(allowBreaching);
        return new PathFinder(nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected boolean canUpdatePath() {
        return allowBreaching || mob.isInLiquid();
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return level.getFluidState(pos).is(net.minecraft.tags.FluidTags.WATER) || !level.getBlockState(pos).isAir() && allowBreaching;
    }

    @Override
    public boolean canNavigateGround() {
        return false;
    }
    protected Vec3 getTempMobPos() {
        return super.getTempMobPos();
    }
    protected double getGroundY(Vec3 target) {
        return 0;
    }
    protected boolean canMoveDirectly(Vec3 startPos, Vec3 stopPos) {
        return false;
    }
    public void setCanFloat(boolean canFloat) {
        super.setCanFloat(canFloat);
    }
    public float getMaxVerticalDistanceToWaypoint() {
        return super.getMaxVerticalDistanceToWaypoint();
    }
}
