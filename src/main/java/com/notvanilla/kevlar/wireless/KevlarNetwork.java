package com.notvanilla.kevlar.wireless;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class KevlarNetwork<N extends KevlarNode> {

    private final int maxTransmissionDistance;
    // the keys are cell coordinates, not chunk positions
    private Map<ChunkPos, Cell<N>> cells = new HashMap<>();

    public KevlarNetwork(int maxTransmissionDistance) {
        this.maxTransmissionDistance = maxTransmissionDistance;
    }

    protected abstract N createNode();

    public N addNode(N node) {
        BlockPos pos = node.getPos();
        return getCell(toCellCoords(pos.getX()), toCellCoords(pos.getZ())).nodes.put(pos, node);
    }

    public N getNode(BlockPos pos) {
        Cell<N> cell = cells.get(new ChunkPos(toCellCoords(pos.getX()), toCellCoords(pos.getZ())));
        if (cell == null)
            return null;
        return cell.nodes.get(pos);
    }

    public N removeNode(BlockPos pos) {
        ChunkPos cellPos = new ChunkPos(toCellCoords(pos.getX()), toCellCoords(pos.getZ()));
        Cell<N> cell = cells.get(cellPos);
        if (cell == null)
            // nothing to remove
            return null;
        N removed = cell.nodes.remove(pos);
        if (cell.nodes.isEmpty())
            cells.remove(cellPos);
        return removed;
    }

    public boolean containsNode(BlockPos pos) {
        Cell<N> cell = cells.get(new ChunkPos(toCellCoords(pos.getX()), toCellCoords(pos.getZ())));
        return cell != null && cell.nodes.containsKey(pos);
    }

    public Stream<N> getNodesWithinDistance(BlockPos pos, double distance) {
        if (distance > maxTransmissionDistance)
            throw new IllegalArgumentException("Distance " + distance + " greater than max allowed " + maxTransmissionDistance);

        int cellX = toCellCoords(pos.getX());
        int cellZ = toCellCoords(pos.getZ());
        return IntStream.range(0, 9)
                .mapToObj(i -> getNodesWithinDistanceInCell(cellX + (i % 3) - 1, cellZ + (i / 3) - 1, pos, distance))
                .flatMap(Function.identity());
    }

    private Stream<N> getNodesWithinDistanceInCell(int cellX, int cellZ, BlockPos pos, double distance) {
        Cell<N> cell = cells.get(new ChunkPos(cellX, cellZ));
        if (cell == null)
            return Stream.empty();
        return cell.nodes.values().stream().filter(node -> node.getPos().isWithinDistance(pos, distance));
    }

    public List<N> shortestPath(N source, Predicate<N> destination, Predicate<N> nodePredicate) {
        return shortestPath(source, destination, nodePredicate, maxTransmissionDistance);
    }

    public List<N> shortestPath(N source, Predicate<N> destination, Predicate<N> nodePredicate, double maxDistance) {
        Object2DoubleMap<BlockPos> distances = new Object2DoubleOpenHashMap<>();
        distances.put(source.getPos(), 0);
        TreeSet<BlockPos> nodesToProcess = new TreeSet<>(
                Comparator.<BlockPos>comparingDouble(distances::getDouble)
                .thenComparing(Function.identity())
        );
        nodesToProcess.add(source.getPos());

        BlockPos destinationNode = null;

        while (!nodesToProcess.isEmpty()) {
            BlockPos node = nodesToProcess.pollFirst();
            assert node != null;

            if (destination.test(getNode(node))) {
                destinationNode = node;
                break;
            }

            double distance = distances.getDouble(node);
            getNodesWithinDistance(node, maxDistance)
                    .filter(nodePredicate)
                    .forEach(n -> {
                double newDistance = distance + Math.sqrt(n.getPos().getSquaredDistance(node.getX(), node.getY(), node.getZ(), false));
                double oldDistance = distances.getOrDefault(n.getPos(), Double.POSITIVE_INFINITY);
                if (newDistance < oldDistance) {
                    nodesToProcess.remove(n.getPos());
                    distances.put(n.getPos(), newDistance);
                    nodesToProcess.add(n.getPos());
                }
            });
        }

        if (destinationNode == null)
            return null;

        List<N> path = new ArrayList<>();
        path.add(getNode(destinationNode));

        BlockPos node = destinationNode;
        while (!node.equals(source.getPos())) {
            double distance = distances.getDouble(node);
            BlockPos node_f = node;
            node = getNodesWithinDistance(node, Math.min(maxDistance, distance + 0.001))
                    .filter(nodePredicate)
                    .filter(n -> !n.getPos().equals(node_f))
                    .filter(n -> MathHelper.approximatelyEquals(
                            distance - distances.getDouble(n),
                            Math.sqrt(n.getPos().getSquaredDistance(node_f.getX(), node_f.getY(), node_f.getZ(), false))
                    ))
                    .findAny()
                    .map(KevlarNode::getPos)
                    .orElse(null);
            if (node == null)
                return null;
            path.add(getNode(node));
        }

        Collections.reverse(path);

        return path;
    }

    private Cell<N> getCell(int x, int z) {
        return cells.computeIfAbsent(new ChunkPos(x, z), k -> new Cell<>());
    }

    private int toCellCoords(int blockCoord) {
        return Math.floorDiv(blockCoord, maxTransmissionDistance);
    }

    public void fromTag(CompoundTag tag) {
        ListTag nodes = tag.getList("Nodes", NbtType.COMPOUND);
        for (int i = 0; i < nodes.size(); i++) {
            CompoundTag nodeTag = nodes.getCompound(i);
            N node = createNode();
            node.fromTag(nodeTag);
            addNode(node);
        }
    }

    public CompoundTag toTag(CompoundTag tag) {
        ListTag nodes = new ListTag();
        for (Cell<N> cell : cells.values()) {
            for (N node : cell.nodes.values()) {
                nodes.add(node.toTag(new CompoundTag()));
            }
        }
        tag.put("Nodes", nodes);
        return tag;
    }

    private static class Cell<N extends KevlarNode> {
        private Map<BlockPos, N> nodes = new HashMap<>();
    }

}
