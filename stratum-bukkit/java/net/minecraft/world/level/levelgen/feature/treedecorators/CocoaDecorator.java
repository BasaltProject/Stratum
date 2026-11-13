// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.world.level.levelgen.feature.treedecorators;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.util.RandomSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import java.util.List;
import com.mojang.serialization.MapCodec;
public class CocoaDecorator extends TreeDecorator {
    private final float probability;
    public static final MapCodec<CocoaDecorator> CODEC = org.basaltmc.stratum.bukkit.facade.LazyCodecs.mapCodec(net.minecraft.world.level.levelgen.feature.treedecorators.CocoaDecorator.class, "CODEC");
    public CocoaDecorator(float probability) {
        this.probability = probability;
    }
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.COCOA;
    }
    public void place(TreeDecorator.Context context) {
        RandomSource random = context.random();
        if (random.nextFloat() >= probability) {
            return;
        }
        List<BlockPos> logs = context.logs();
        if (logs.isEmpty()) {
            return;
        }
        int baseY = logs.get(0).getY();
        for (BlockPos log : logs) {
            if (log.getY() - baseY > 2) {
                continue;
            }
            for (Direction side : Direction.Plane.HORIZONTAL) {
                if (random.nextFloat() <= 0.25F) {
                    Direction away = side.getOpposite();
                    BlockPos pod = log.offset(away.getStepX(), 0, away.getStepZ());
                    if (context.isAir(pod)) {
                        context.setBlock(pod, org.basaltmc.stratum.bukkit.facade.StateProps.with(org.basaltmc.stratum.bukkit.facade.StateProps.with(Blocks.COCOA.defaultBlockState(), "age", random.nextInt(3)), "facing", side.getSerializedName()));
                    }
                }
            }
        }
    }
}
