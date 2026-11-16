// Independent reimplementation, not Mojang code. See stratum-bukkit/NOTICE.md
package net.minecraft.util.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class ParallelMapTransform {
    public static <K, U, V> CompletableFuture<Map<K, V>> schedule(Map<K, U> input, BiFunction<K, U, V> operation, int maxTaskCount, Executor executor) {
        int size = input.size();
        if (size == 0) {
            return CompletableFuture.completedFuture(Map.of());
        }
        List<Map.Entry<K, U>> entries = new ArrayList<>(input.entrySet());
        Object[] results = new Object[size];
        int tasks = Math.max(1, Math.min(maxTaskCount, size));
        int batch = (size + tasks - 1) / tasks;
        List<CompletableFuture<?>> pending = new ArrayList<>();
        for (int start = 0; start < size; start += batch) {
            int from = start;
            int to = Math.min(size, start + batch);
            pending.add(CompletableFuture.runAsync(() -> {
                for (int i = from; i < to; i++) {
                    results[i] = operation.apply(entries.get(i).getKey(), entries.get(i).getValue());
                }
            }, executor));
        }
        return CompletableFuture.allOf(pending.toArray(CompletableFuture[]::new)).thenApplyAsync(ignored -> {
            Map<K, V> output = new HashMap<>(size * 2);
            for (int i = 0; i < size; i++) {
                @SuppressWarnings("unchecked")
                V value = (V) results[i];
                output.put(entries.get(i).getKey(), value);
            }
            return output;
        }, executor);
    }

    public static <K, U, V> CompletableFuture<Map<K, V>> schedule(Map<K, U> input, BiFunction<K, U, V> operation, Executor executor) {
        return schedule(input, operation, Runtime.getRuntime().availableProcessors(), executor);
    }
    static record Container<K, U, V>(BiFunction<K, U, V> operation, Object[] keys, Object[] values) {
        public Container(BiFunction<K, U, V> operation, int size) {
            this((BiFunction) null, (Object[]) null, (Object[]) null); throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.Container#<init>");
        }
        public void put(int index, K key, U input) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.Container#put");
        }
        public void applyOperation(int index) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.Container#applyOperation");
        }
        public void copyOut(int index, Map<K, V> output) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.Container#copyOut");
        }
        public int size() {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.Container#size");
        }
    }
    static abstract class SplitterBase<K, U, V> {
        private SplitterBase(BiFunction<K, U, V> operation, int size, int taskCount) {
            
        }
        public CompletableFuture<Map<K, V>> scheduleTasks(Map<K, U> input, Executor executor) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.SplitterBase#scheduleTasks");
        }
        protected abstract int batchSize(int index);
        protected abstract CompletableFuture<?> scheduleBatch(Container<K, U, V> container, int startIndex, int endIndex, Executor executor);
        protected abstract CompletableFuture<Map<K, V>> scheduleFinalOperation(CompletableFuture<?> allTasksDone, Container<K, U, V> container);
    }
    static class SingleTaskSplitter<K, U, V> extends SplitterBase<K, U, V> {
        private SingleTaskSplitter(BiFunction<K, U, V> operation, int size) {
            super((java.util.function.BiFunction) null, (int) 0, (int) 0);
        }
        protected int batchSize(int index) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.SingleTaskSplitter#batchSize");
        }
        protected CompletableFuture<?> scheduleBatch(Container<K, U, V> container, int startIndex, int endIndex, Executor executor) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.SingleTaskSplitter#scheduleBatch");
        }
        protected CompletableFuture<Map<K, V>> scheduleFinalOperation(CompletableFuture<?> allTasksDone, Container<K, U, V> container) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.SingleTaskSplitter#scheduleFinalOperation");
        }
    }
    static class BatchedTaskSplitter<K, U, V> extends SplitterBase<K, U, V> {
        private BatchedTaskSplitter(BiFunction<K, U, V> operation, int size, int maxTasks) {
            super((java.util.function.BiFunction) null, (int) 0, (int) 0);
        }
        protected CompletableFuture<?> scheduleBatch(Container<K, U, V> container, int startIndex, int endIndex, Executor executor) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.BatchedTaskSplitter#scheduleBatch");
        }
        protected int batchSize(int index) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.BatchedTaskSplitter#batchSize");
        }
        protected CompletableFuture<Map<K, V>> scheduleFinalOperation(CompletableFuture<?> allTasksDone, Container<K, U, V> container) {
            throw org.basaltmc.stratum.bukkit.facade.NmsStub.unsupported("net.minecraft.util.thread.ParallelMapTransform.BatchedTaskSplitter#scheduleFinalOperation");
        }
    }
}
