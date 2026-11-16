// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.gametest.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.basaltmc.stratum.bukkit.WorldNatives;
import java.util.function.Consumer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;

public class GameTestHelper extends GameTestHelperAssertions {
    public GameTestHelper(GameTestInfo testInfo) {
        super(testInfo);
    }


    public void killAllEntities() {
        for (Entity entity : getLevel().getEntitiesOfClass(Entity.class, getBounds().inflate(1.0), e -> !(e instanceof Player))) {
            entity.discard();
        }
    }

    public void killAllEntitiesOfClass(Class<? extends Entity> baseClass) {
        for (Entity entity : getLevel().getEntitiesOfClass(baseClass, getBounds().inflate(1.0), e -> !(e instanceof Player))) {
            entity.discard();
        }
    }

    public ItemEntity spawnItem(Item item, Vec3 pos) {
        Vec3 abs = absoluteVec(pos);
        ItemEntity entity = new ItemEntity(getLevel(), abs.x, abs.y, abs.z, new ItemStack(item, 1));
        entity.setDeltaMovement(0.0, 0.0, 0.0);
        getLevel().addFreshEntity(entity);
        return entity;
    }

    public ItemEntity spawnItem(Item item, float x, float y, float z) {
        return spawnItem(item, new Vec3(x, y, z));
    }

    public ItemEntity spawnItem(Item item, BlockPos pos) {
        return spawnItem(item, center(pos));
    }

    public void despawnItem(BlockPos pos, double distance) {
        for (ItemEntity entity : getLevel().getEntitiesOfClass(ItemEntity.class, new AABB(absolutePos(pos)).inflate(distance))) {
            entity.discard();
        }
    }

    public <E extends Entity> E spawn(EntityType<E> entityType, BlockPos pos) {
        return spawn(entityType, center(pos));
    }

    public <E extends Entity> List<E> spawn(EntityType<E> entityType, BlockPos pos, int amount) {
        return spawn(entityType, center(pos), amount);
    }

    public <E extends Entity> List<E> spawn(EntityType<E> entityType, Vec3 pos, int amount) {
        return spawnEntity(entityType, pos).spawn(amount);
    }

    public <E extends Entity> E spawn(EntityType<E> entityType, Vec3 pos) {
        return spawnEntity(entityType, pos).spawn();
    }

    public <E extends Mob> E spawn(EntityType<E> entityType, double x, double y, double z, EntitySpawnReason entitySpawnReason) {
        return spawnEntity(entityType, new Vec3(x, y, z)).spawnReason(entitySpawnReason).spawn();
    }

    public <E extends Entity> E spawn(EntityType<E> entityType, BlockPos pos, EntitySpawnReason spawnReason) {
        return spawn(entityType, center(pos), spawnReason);
    }

    public <E extends Entity> E spawn(EntityType<E> entityType, Vec3 pos, EntitySpawnReason spawnReason) {
        return spawnEntity(entityType, pos).spawnReason(spawnReason).spawn();
    }

    public <E extends Entity> GameTestEntityBuilder<E> spawnEntity(EntityType<E> entityType, Vec3 position) {
        return new GameTestEntityBuilder<>(this, entityType, position);
    }

    public <E extends Entity> GameTestEntityBuilder<E> spawnEntity(EntityType<E> entityType, BlockPos position) {
        return spawnEntity(entityType, center(position));
    }

    public <E extends Entity> GameTestEntityBuilder<E> spawnEntity(EntityType<E> entityType, int x, int y, int z) {
        return spawnEntity(entityType, new BlockPos(x, y, z));
    }

    public <E extends Entity> GameTestEntityBuilder<E> spawnEntity(EntityType<E> entityType, float x, float y, float z) {
        return spawnEntity(entityType, new Vec3(x, y, z));
    }

    public <E extends Mob> GameTestMobBuilder<E> spawnMob(EntityType<E> entityType, Vec3 position) {
        return new GameTestMobBuilder<>(this, entityType, position);
    }

    public <E extends Mob> GameTestMobBuilder<E> spawnMob(EntityType<E> entityType, BlockPos position) {
        return spawnMob(entityType, center(position));
    }

    public <E extends Mob> GameTestMobBuilder<E> spawnMob(EntityType<E> entityType, int x, int y, int z) {
        return spawnMob(entityType, new BlockPos(x, y, z));
    }

    public <E extends Mob> GameTestMobBuilder<E> spawnMob(EntityType<E> entityType, float x, float y, float z) {
        return spawnMob(entityType, new Vec3(x, y, z));
    }

    public <E extends Entity> E spawn(EntityType<E> entityType, int x, int y, int z) {
        return spawn(entityType, new BlockPos(x, y, z));
    }

    public <E extends Entity> E spawn(EntityType<E> entityType, float x, float y, float z) {
        return spawn(entityType, new Vec3(x, y, z));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> entityType, BlockPos pos) {
        return spawnMob(entityType, pos).withNoFreeWill().spawn();
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> entityType, int x, int y, int z) {
        return spawnWithNoFreeWill(entityType, new BlockPos(x, y, z));
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> entityType, Vec3 pos) {
        return spawnMob(entityType, pos).withNoFreeWill().spawn();
    }

    public <E extends Mob> E spawnWithNoFreeWill(EntityType<E> entityType, float x, float y, float z) {
        return spawnWithNoFreeWill(entityType, new Vec3(x, y, z));
    }

    public void hurt(Entity entity, DamageSource source, float damage) {
        entity.hurtServer(getLevel(), source, damage);
    }

    public void kill(Entity entity) {
        entity.kill(getLevel());
    }

    public void discard(Entity entity) {
        entity.discard();
    }

    public <E extends Entity> E findOneEntity(EntityType<E> entityType) {
        List<E> found = entitiesIn(entityType, getBounds());
        if (found.size() != 1) {
            throw assertionException("test.error.expected_entity_count", 1, entityType.getDescription(), found.size());
        }
        return found.getFirst();
    }

    public <E extends Entity> E findClosestEntity(EntityType<E> entityType, int x, int y, int z, double distance) {
        Vec3 origin = absoluteVec(new Vec3(x + 0.5, y, z + 0.5));
        return getEntities(entityType, new BlockPos(x, y, z), distance).stream()
            .min(java.util.Comparator.comparingDouble(e -> e.distanceToSqr(origin))).orElse(null);
    }

    public <E extends Entity> List<E> findEntities(EntityType<E> entityType, int x, int y, int z, double distance) {
        return getEntities(entityType, new BlockPos(x, y, z), distance);
    }

    public <E extends Entity> List<E> findEntities(EntityType<E> entityType, Vec3 pos, double distance) {
        return entitiesIn(entityType, new AABB(absoluteVec(pos), absoluteVec(pos)).inflate(distance));
    }

    public void moveTo(Mob mob, float x, float y, float z) {
        moveTo(mob, new Vec3(x, y, z));
    }

    public void moveTo(Mob mob, BlockPos pos) {
        moveTo(mob, center(pos));
    }

    public void moveTo(Mob mob, Vec3 pos) {
        Vec3 abs = absoluteVec(pos);
        mob.snapTo(abs.x, abs.y, abs.z);
    }

    public GameTestSequence walkTo(Mob mob, BlockPos targetPos, float speedModifier) {
        BlockPos abs = absolutePos(targetPos);
        mob.getNavigation().moveTo(abs.getX() + 0.5, abs.getY(), abs.getZ() + 0.5, speedModifier);
        return startSequence();
    }

    public void pressButton(int x, int y, int z) {
        pressButton(new BlockPos(x, y, z));
    }

    public void pressButton(BlockPos buttonPos) {
        assertBlockTag(net.minecraft.tags.BlockTags.BUTTONS, buttonPos);
        useBlock(buttonPos);
    }

    public void useBlock(BlockPos relativePos) {
        useBlock(relativePos, makeMockPlayer(GameType.CREATIVE));
    }

    public void useBlock(BlockPos relativePos, Player player) {
        BlockPos abs = absolutePos(relativePos);
        useBlock(relativePos, player, new BlockHitResult(Vec3.atCenterOf(abs), Direction.NORTH, abs, true));
    }

    public void useBlock(BlockPos relativePos, Player player, BlockHitResult hitResult) {
        BlockPos abs = absolutePos(relativePos);
        WorldNatives.useItemOn(getLevel().getWorld().getName(), abs.getX(), abs.getY(), abs.getZ(), hitResult.getDirection().get3DDataValue(), "minecraft:air", 0.0F, 0.0F);
    }

    public LivingEntity makeAboutToDrown(LivingEntity entity) {
        entity.setAirSupply(0);
        entity.setHealth(entity.getMaxHealth());
        return entity;
    }

    public LivingEntity withLowHealth(LivingEntity entity) {
        entity.setHealth(0.25F);
        return entity;
    }

    public Player makeMockPlayer(GameType gameType) {
        return makeMockServerPlayer(gameType);
    }

    public Player makeMockServerPlayer(GameType gameType) {
        ServerPlayer player = new ServerPlayer(getLevel().getServer(), getLevel(), new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "test-mock-player"), net.minecraft.server.level.ClientInformation.createDefault());
        player.setGameMode(gameType);
        return player;
    }

    public void rotateEntityWithTest(Entity entity) {
        entity.setYRot(entity.getYRot() + getTestRotation().ordinal() * 90.0F);
    }

    public ServerPlayer makeMockServerPlayerInLevel() {
        return (ServerPlayer) makeMockServerPlayer(GameType.CREATIVE);
    }

    public void pullLever(int x, int y, int z) {
        pullLever(new BlockPos(x, y, z));
    }

    public void pullLever(BlockPos leverPos) {
        assertBlockPresent(net.minecraft.world.level.block.Blocks.LEVER, leverPos);
        useBlock(leverPos);
    }

    public void placeBlock(BlockPos relativePos, Block block, Direction relativePlaceDirection) {
        setBlock(relativePos, block, relativePlaceDirection);
    }

    public void placeBlock(int x, int y, int z, Block block, Direction placeDirection) {
        placeBlock(new BlockPos(x, y, z), block, placeDirection);
    }

    public void pulseRedstone(BlockPos pos, long duration) {
        setBlock(pos, net.minecraft.world.level.block.Blocks.REDSTONE_BLOCK);
        runAfterDelay(duration, () -> setBlock(pos, net.minecraft.world.level.block.Blocks.AIR));
    }

    public void destroyBlock(BlockPos pos) {
        getLevel().destroyBlock(absolutePos(pos), false, null);
    }

    public void setBlock(int x, int y, int z, Block block) {
        setBlock(new BlockPos(x, y, z), block.defaultBlockState());
    }

    public void setBlock(int x, int y, int z, BlockState state) {
        setBlock(new BlockPos(x, y, z), state);
    }

    public void setBlock(BlockPos blockPos, Block block) {
        setBlock(blockPos, block.defaultBlockState());
    }

    public void setBlock(BlockPos blockPos, BlockState state) {
        getLevel().setBlock(absolutePos(blockPos), state, 3);
    }

    public void setBlock(BlockPos blockPos, Block block, Direction direction) {
        setBlock(blockPos, block.defaultBlockState(), direction);
    }

    public void setBlock(BlockPos blockPos, BlockState blockState, Direction direction) {
        Direction abs = getAbsoluteDirection(direction);
        BlockState oriented = blockState;
        for (Property<?> property : blockState.getProperties()) {
            if (property.getValueClass() == Direction.class) {
                oriented = withDirection(oriented, property, abs);
            }
        }
        setBlock(blockPos, oriented);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> BlockState withDirection(BlockState state, Property<T> property, Direction direction) {
        return property.getPossibleValues().contains((T) direction) ? state.setValue(property, (T) direction) : state;
    }

    public void placeAt(Player player, ItemStack blockStack, BlockPos pos, Direction face) {
        BlockPos abs = absolutePos(pos.relative(face.getOpposite()));
        WorldNatives.useItemOn(getLevel().getWorld().getName(), abs.getX(), abs.getY(), abs.getZ(), getAbsoluteDirection(face).get3DDataValue(),
            blockStack.getItem().builtInRegistryHolder().key().identifier().toString(), player.getYRot(), player.getXRot());
    }

    public void useItemOnBlock(Player player, Item item, BlockPos pos) {
        BlockPos abs = absolutePos(pos);
        WorldNatives.useItemOn(getLevel().getWorld().getName(), abs.getX(), abs.getY(), abs.getZ(), Direction.UP.get3DDataValue(),
            item.builtInRegistryHolder().key().identifier().toString(), player.getYRot(), player.getXRot());
    }

    public void setTime(long ticks) {
        WorldNatives.setTime(getLevel().getWorld().getName(), ticks);
    }

    public void randomTick(BlockPos pos) {
        BlockPos abs = absolutePos(pos);
        BlockState state = getLevel().getBlockState(abs);
        state.randomTick(getLevel(), abs, getLevel().getRandom());
    }

    public void tickBlock(BlockPos pos) {
        BlockPos abs = absolutePos(pos);
        BlockState state = getLevel().getBlockState(abs);
        state.tick(getLevel(), abs, getLevel().getRandom());
    }

    public void tickPrecipitation(BlockPos pos) {
        getLevel().tickPrecipitation(absolutePos(pos));
    }

    public void tickPrecipitation() {
        AABB bounds = getRelativeBounds();
        for (int x = (int) bounds.minX; x <= (int) bounds.maxX; x++) {
            for (int z = (int) bounds.minZ; z <= (int) bounds.maxZ; z++) {
                tickPrecipitation(new BlockPos(x, 0, z));
            }
        }
    }

    public int getHeight(Heightmap.Types heightmap, int x, int z) {
        BlockPos abs = absolutePos(new BlockPos(x, 0, z));
        return relativePos(new BlockPos(abs.getX(), getLevel().getHeight(heightmap, abs.getX(), abs.getZ()), abs.getZ())).getY();
    }

    public void fail(Component message, BlockPos pos) {
        throw assertionException(pos, message);
    }

    public void fail(Component message, Entity entity) {
        throw assertionException(relativePos(entity.blockPosition()), message);
    }

    public void fail(Component message) {
        throw assertionException(message);
    }

    public void fail(String message) {
        throw assertionException(Component.literal(message));
    }

    public void failIf(Runnable asserter) {
        testInfo.createSequence().thenWaitUntil(asserter).thenFail(() -> assertionException("test.error.fail"));
    }

    public void failIfEver(Runnable asserter) {
        testInfo.createSequence().thenExecuteAfter(1, () -> { }).thenWaitUntil(0L, asserter).thenFail(() -> assertionException("test.error.fail"));
    }

    public void assertTrue(boolean condition, Component errorMessage) {
        if (!condition) {
            throw assertionException(errorMessage);
        }
    }

    public void assertTrue(boolean condition, String errorMessage) {
        assertTrue(condition, Component.literal(errorMessage));
    }

    public <N> void assertValueEqual(N value, N expected, String valueName) {
        assertValueEqual(value, expected, Component.literal(valueName));
    }

    public <N> void assertValueEqual(N value, N expected, Component valueName) {
        if (!Objects.equals(value, expected)) {
            throw assertionException("test.error.value_not_equal", valueName, expected, value);
        }
    }

    public <N extends Comparable<N>> void assertValueInBetween(N lowerBound, N value, N upperBound, String valueName) {
        assertValueInBetween(lowerBound, value, upperBound, Component.literal(valueName));
    }

    public <N extends Comparable<N>> void assertValueInBetween(N lowerBound, N value, N upperBound, Component valueName) {
        if (value.compareTo(lowerBound) < 0 || value.compareTo(upperBound) > 0) {
            throw assertionException("test.error.value_not_in_range", valueName, lowerBound, upperBound, value);
        }
    }

    public void assertFalse(boolean condition, Component errorMessage) {
        assertTrue(!condition, errorMessage);
    }

    public void assertFalse(boolean condition, String errorMessage) {
        assertTrue(!condition, errorMessage);
    }

    public void onEachTick(Runnable action) {
        for (long tick = 0; tick < testInfo.getTimeoutTicks(); tick++) {
            testInfo.setRunAtTickTime(tick, action);
        }
    }

    public void setBiome(ResourceKey<Biome> biome) {
        AABB bounds = getBounds();
        String name = biome.identifier().toString();
        for (int x = (int) Math.floor(bounds.minX); x < bounds.maxX; x += 4) {
            for (int y = (int) Math.floor(bounds.minY); y < bounds.maxY; y += 4) {
                for (int z = (int) Math.floor(bounds.minZ); z < bounds.maxZ; z += 4) {
                    WorldNatives.setBiome(getLevel().getWorld().getName(), x, y, z, name);
                }
            }
        }
    }
    public GameTestAssertException assertionException(Component description) {
        return super.assertionException(description);
    }
    public GameTestAssertException assertionException(String descriptionId, Object... arguments) {
        return super.assertionException(descriptionId, arguments);
    }
    public GameTestAssertPosException assertionException(BlockPos pos, Component description) {
        return super.assertionException(pos, description);
    }
    public GameTestAssertPosException assertionException(BlockPos pos, String descriptionId, Object... arguments) {
        return super.assertionException(pos, descriptionId, arguments);
    }
    public ServerLevel getLevel() {
        return super.getLevel();
    }
    public BlockState getBlockState(BlockPos pos) {
        return super.getBlockState(pos);
    }
    public <T extends BlockEntity> T getBlockEntity(BlockPos pos, Class<T> type) {
        return super.getBlockEntity(pos, type);
    }
    public void assertBlockPresent(Block blockType, int x, int y, int z) {
        super.assertBlockPresent(blockType, x, y, z);
    }
    public void assertBlockPresent(Block blockType, BlockPos pos) {
        super.assertBlockPresent(blockType, pos);
    }
    public void assertBlockPresent(Block blockType) {
        super.assertBlockPresent(blockType);
    }
    public void assertBlockNotPresent(Block blockType, int x, int y, int z) {
        super.assertBlockNotPresent(blockType, x, y, z);
    }
    public void assertBlockNotPresent(Block blockType, BlockPos pos) {
        super.assertBlockNotPresent(blockType, pos);
    }
    public void assertBlockTag(TagKey<Block> tag, BlockPos pos) {
        super.assertBlockTag(tag, pos);
    }
    public void succeedWhenBlockPresent(Block block, int x, int y, int z) {
        super.succeedWhenBlockPresent(block, x, y, z);
    }
    public void succeedWhenBlockPresent(Block block, BlockPos pos) {
        super.succeedWhenBlockPresent(block, pos);
    }
    public void assertBlock(BlockPos pos, Predicate<Block> predicate, Function<Block, Component> errorMessage) {
        super.assertBlock(pos, predicate, errorMessage);
    }
    public <T extends Comparable<T>> void assertBlockProperty(BlockPos pos, Property<T> property, T value) {
        super.assertBlockProperty(pos, property, value);
    }
    public <T extends Comparable<T>> void assertBlockProperty(BlockPos pos, Property<T> property, Predicate<T> predicate, Component errorMessage) {
        super.assertBlockProperty(pos, property, predicate, errorMessage);
    }
    public void assertBlockState(BlockPos pos, BlockState expected) {
        super.assertBlockState(pos, expected);
    }
    public void assertBlockState(BlockPos pos, Predicate<BlockState> predicate, Function<BlockState, Component> errorMessage) {
        super.assertBlockState(pos, predicate, errorMessage);
    }
    public <T extends BlockEntity> void assertBlockEntityData(BlockPos pos, Class<T> type, Predicate<T> predicate, Supplier<Component> errorMessage) {
        super.assertBlockEntityData(pos, type, predicate, errorMessage);
    }
    public void assertRedstoneSignal(BlockPos pos, Direction direction, IntPredicate levelPredicate, Supplier<Component> errorMessage) {
        super.assertRedstoneSignal(pos, direction, levelPredicate, errorMessage);
    }
    public void assertEntityPresent(EntityType<?> entityType) {
        super.assertEntityPresent(entityType);
    }
    public void assertEntityPresent(EntityType<?> entityType, int x, int y, int z) {
        super.assertEntityPresent(entityType, x, y, z);
    }
    public void assertEntityPresent(EntityType<?> entityType, BlockPos pos) {
        super.assertEntityPresent(entityType, pos);
    }
    public void assertEntityPresent(EntityType<?> entityType, AABB relativeAABB) {
        super.assertEntityPresent(entityType, relativeAABB);
    }
    public void assertEntityPresent(EntityType<?> entityType, AABB relativeAABB, Component message) {
        super.assertEntityPresent(entityType, relativeAABB, message);
    }
    public void assertEntitiesPresent(EntityType<?> entityType, int expectedEntities) {
        super.assertEntitiesPresent(entityType, expectedEntities);
    }
    public void assertEntitiesPresent(EntityType<?> entityType, BlockPos pos, int numOfExpectedEntities, double distance) {
        super.assertEntitiesPresent(entityType, pos, numOfExpectedEntities, distance);
    }
    public void assertEntityPresent(EntityType<?> entityType, BlockPos pos, double distance) {
        super.assertEntityPresent(entityType, pos, distance);
    }
    public <T extends Entity> List<T> getEntities(EntityType<T> entityType, BlockPos pos, double distance) {
        return super.getEntities(entityType, pos, distance);
    }
    public <T extends Entity> List<T> getEntities(EntityType<T> entityType) {
        return super.getEntities(entityType);
    }
    public void assertEntityInstancePresent(Entity entity, int x, int y, int z) {
        super.assertEntityInstancePresent(entity, x, y, z);
    }
    public void assertEntityInstancePresent(Entity entity, BlockPos pos) {
        super.assertEntityInstancePresent(entity, pos);
    }
    public void assertEntityInstancePresent(Entity entity, BlockPos pos, double inflate) {
        super.assertEntityInstancePresent(entity, pos, inflate);
    }
    public void assertItemEntityCountIs(Item itemType, BlockPos pos, double distance, int count) {
        super.assertItemEntityCountIs(itemType, pos, distance, count);
    }
    public void assertItemEntityPresent(Item itemType, BlockPos pos, double distance) {
        super.assertItemEntityPresent(itemType, pos, distance);
    }
    public void assertItemEntityNotPresent(Item itemType, BlockPos pos, double distance) {
        super.assertItemEntityNotPresent(itemType, pos, distance);
    }
    public void assertItemEntityPresent(Item itemType) {
        super.assertItemEntityPresent(itemType);
    }
    public void assertItemEntityNotPresent(Item itemType) {
        super.assertItemEntityNotPresent(itemType);
    }
    public void assertEntityNotPresent(EntityType<?> entityType) {
        super.assertEntityNotPresent(entityType);
    }
    public void assertEntityNotPresent(EntityType<?> entityType, int x, int y, int z) {
        super.assertEntityNotPresent(entityType, x, y, z);
    }
    public void assertEntityNotPresent(EntityType<?> entityType, BlockPos pos) {
        super.assertEntityNotPresent(entityType, pos);
    }
    public void assertEntityNotPresent(EntityType<?> entityType, AABB relativeAABB) {
        super.assertEntityNotPresent(entityType, relativeAABB);
    }
    public void assertEntityTouching(EntityType<?> entityType, double x, double y, double z) {
        super.assertEntityTouching(entityType, x, y, z);
    }
    public void assertEntityNotTouching(EntityType<?> entityType, double x, double y, double z) {
        super.assertEntityNotTouching(entityType, x, y, z);
    }
    public <E extends Entity, T> void assertEntityData(BlockPos pos, EntityType<E> entityType, Predicate<E> test) {
        super.assertEntityData(pos, entityType, test);
    }
    public <E extends Entity, T> void assertEntityData(BlockPos pos, EntityType<E> entityType, Function<? super E, T> dataAccessor, T data) {
        super.assertEntityData(pos, entityType, dataAccessor, data);
    }
    public <E extends Entity, T> void assertEntityData(AABB box, EntityType<E> entityType, Function<? super E, T> dataAccessor, T data) {
        super.assertEntityData(box, entityType, dataAccessor, data);
    }
    public <E extends LivingEntity> void assertEntityIsHolding(BlockPos pos, EntityType<E> entityType, Item item) {
        super.assertEntityIsHolding(pos, entityType, item);
    }
    public <E extends Entity & InventoryCarrier> void assertEntityInventoryContains(BlockPos pos, EntityType<E> entityType, Item item) {
        super.assertEntityInventoryContains(pos, entityType, item);
    }
    public void assertContainerEmpty(BlockPos pos) {
        super.assertContainerEmpty(pos);
    }
    public void assertContainerContainsSingle(BlockPos pos, Item item) {
        super.assertContainerContainsSingle(pos, item);
    }
    public void assertContainerContains(BlockPos pos, Item item) {
        super.assertContainerContains(pos, item);
    }
    public void assertSameBlockStates(BoundingBox sourceBoundingBox, BlockPos targetBoundingBoxCorner) {
        super.assertSameBlockStates(sourceBoundingBox, targetBoundingBoxCorner);
    }
    public void assertSameBlockState(BlockPos sourcePos, BlockPos targetPos) {
        super.assertSameBlockState(sourcePos, targetPos);
    }
    public void assertAtTickTimeContainerContains(long time, BlockPos pos, Item item) {
        super.assertAtTickTimeContainerContains(time, pos, item);
    }
    public void assertAtTickTimeContainerEmpty(long time, BlockPos pos) {
        super.assertAtTickTimeContainerEmpty(time, pos);
    }
    public <E extends Entity, T> void succeedWhenEntityData(BlockPos pos, EntityType<E> entityType, Function<E, T> dataAccessor, T data) {
        super.succeedWhenEntityData(pos, entityType, dataAccessor, data);
    }
    public <E extends Entity> void assertEntityProperty(E entity, Predicate<E> test, Component description) {
        super.assertEntityProperty(entity, test, description);
    }
    public <E extends Entity, T> void assertEntityProperty(E entity, Function<E, T> test, T expected, Component description) {
        super.assertEntityProperty(entity, test, expected, description);
    }
    public void assertLivingEntityHasMobEffect(LivingEntity entity, Holder<MobEffect> mobEffect, int amplifier) {
        super.assertLivingEntityHasMobEffect(entity, mobEffect, amplifier);
    }
    public void succeedWhenEntityPresent(EntityType<?> entityType, int x, int y, int z) {
        super.succeedWhenEntityPresent(entityType, x, y, z);
    }
    public void succeedWhenEntityPresent(EntityType<?> entityType, BlockPos pos) {
        super.succeedWhenEntityPresent(entityType, pos);
    }
    public void succeedWhenEntityNotPresent(EntityType<?> entityType, int x, int y, int z) {
        super.succeedWhenEntityNotPresent(entityType, x, y, z);
    }
    public void succeedWhenEntityNotPresent(EntityType<?> entityType, BlockPos pos) {
        super.succeedWhenEntityNotPresent(entityType, pos);
    }
    public void succeed() {
        super.succeed();
    }
    public void succeedIf(Runnable asserter) {
        super.succeedIf(asserter);
    }
    public void succeedWhen(Runnable asserter) {
        super.succeedWhen(asserter);
    }
    public void succeedOnTickWhen(int tick, Runnable asserter) {
        super.succeedOnTickWhen(tick, asserter);
    }
    public void runAtTickTime(long time, Runnable asserter) {
        super.runAtTickTime(time, asserter);
    }
    public void runBeforeTestEnd(Runnable asserter) {
        super.runBeforeTestEnd(asserter);
    }
    public void runAfterDelay(long ticksToDelay, Runnable whatToRun) {
        super.runAfterDelay(ticksToDelay, whatToRun);
    }
    public GameTestSequence startSequence() {
        return super.startSequence();
    }
    public BlockPos absolutePos(BlockPos relativePos) {
        return super.absolutePos(relativePos);
    }
    public BlockPos relativePos(BlockPos absolutePos) {
        return super.relativePos(absolutePos);
    }
    public AABB absoluteAABB(AABB relativeAABB) {
        return super.absoluteAABB(relativeAABB);
    }
    public AABB relativeAABB(AABB absoluteAABB) {
        return super.relativeAABB(absoluteAABB);
    }
    public Vec3 absoluteVec(Vec3 relativeVec) {
        return super.absoluteVec(relativeVec);
    }
    public Vec3 relativeVec(Vec3 absoluteVec) {
        return super.relativeVec(absoluteVec);
    }
    public Rotation getTestRotation() {
        return super.getTestRotation();
    }
    public Direction getTestDirection() {
        return super.getTestDirection();
    }
    public Direction getAbsoluteDirection(Direction direction) {
        return super.getAbsoluteDirection(direction);
    }
    public long getTick() {
        return super.getTick();
    }
    public AABB getBounds() {
        return super.getBounds();
    }
    public AABB getBoundsWithPadding() {
        return super.getBoundsWithPadding();
    }
    public AABB getRelativeBounds() {
        return super.getRelativeBounds();
    }
    public void forEveryBlockInStructure(Consumer<BlockPos> forBlock) {
        super.forEveryBlockInStructure(forBlock);
    }
    class TestBlockPlaceContext extends BlockPlaceContext {
        public TestBlockPlaceContext(Level level, InteractionHand hand, ItemStack itemStackInHand, BlockHitResult hitResult, Direction placeDirection) {
            super((net.minecraft.world.item.context.UseOnContext) null);
        }
        public Direction getNearestLookingDirection() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.gametest.framework.GameTestHelper.TestBlockPlaceContext#getNearestLookingDirection");
        }
        public Direction[] getNearestLookingDirections() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.gametest.framework.GameTestHelper.TestBlockPlaceContext#getNearestLookingDirections");
        }
    }
}
