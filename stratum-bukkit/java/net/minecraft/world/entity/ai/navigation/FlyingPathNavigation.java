// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;

public class FlyingPathNavigation extends PathNavigation {
    public FlyingPathNavigation(Mob mob, Level level) {
        super(mob, level);
    }

    @Override
    protected PathFinder createPathFinder(int maxVisitedNodes) {
        nodeEvaluator = new FlyNodeEvaluator();
        nodeEvaluator.setCanPassDoors(true);
        return new PathFinder(nodeEvaluator, maxVisitedNodes);
    }

    @Override
    protected boolean canUpdatePath() {
        return canFloat() && mob.isInLiquid() || !mob.isPassenger();
    }

    @Override
    public boolean isStableDestination(BlockPos pos) {
        return level.getBlockState(pos).entityCanStandOn(level, pos, mob);
    }

    @Override
    public boolean canNavigateGround() {
        return false;
    }
    protected boolean canMoveDirectly(Vec3 startPos, Vec3 stopPos) {
        return false;
    }
    protected Vec3 getTempMobPos() {
        return super.getTempMobPos();
    }
    public Path createPath(Entity target, int reachRange) {
        return super.createPath(target, reachRange);
    }
    public void tick() {
    }
}
