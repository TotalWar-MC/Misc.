package com.steffbeard.totalwar.core.utils.structure;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.Builder.Default;
import moe.kira.structure.misc.IntReference;
import moe.kira.structure.misc.VolatileBooleanReference;
import moe.kira.structure.plugin.StructurePlugin;
import moe.kira.type.Type;

public class StructureAPI {
    public final static Type ANY_BLOCK = null;
    public final static Structure EMPTY_STRUCTURE = new StructureDummy();
    private final static Class<? extends BlockEvent> INTERNAL_LISTENER = BlockEventDummy.class;
    
    private static class StructureDummy implements Structure {
        @Override
        public void onCreate(BlockPlaceEvent event, MatchResult tree) {}
    }
    
    private static class BlockEventDummy extends BlockEvent {
        private BlockEventDummy(Block theBlock) {
            super(theBlock);
        }
        
        @Override
        public HandlerList getHandlers() {
            return null;
        }
    }
    
    private final static Map<Class<? extends BlockEvent>, List<StructureTree>> trees = Maps.newHashMap();
    private final static Map<Class<? extends BlockEvent>, List<StructureTree>> asyncTrees = Maps.newHashMap();
    
    protected static void registerTree(StructureTree tree) {
        if (tree instanceof AsyncSearch) {
            asyncTrees.get(INTERNAL_LISTENER).add(tree);
        } else {
            trees.get(INTERNAL_LISTENER).add(tree);
        }
    }
    
    protected static void unregisterTree(StructureTree tree) {
        if (tree instanceof AsyncSearch) {
            asyncTrees.get(INTERNAL_LISTENER).remove(tree);
        } else {
            trees.get(INTERNAL_LISTENER).remove(tree);
        }
    }
    
    public static StructureTree[] register(StructureTree... trees) {
        for (StructureTree each : trees)
            register(each);
        return trees;
    }
    
    public static Iterable<StructureTree> register(Iterable<StructureTree> trees) {
        for (StructureTree each : trees) 
            register(each);
        return trees;
    }
    
    @SneakyThrows
    public static StructureTree register(Class<? extends Structure> structureClass, Coord... coords) {
        return register(structureClass.newInstance(), coords);
    }
    
    public static StructureTree register(Structure structure, Coord... coords) {
        return register(StructureTree.wrap(structure, coords));
    }
    
    public static StructureTree register(StructureTree tree) {
        registerTree(tree);
        if (tree.structure instanceof Saveable)
            ((Saveable) tree.structure).onLoad(StructurePlugin.presistentStructuresFolder());
        return tree;
    }
    
    public static Iterable<StructureTree> unregister(Iterable<StructureTree> trees) {
        for (StructureTree each : trees) 
            unregister(each);
        return trees;
    }
    
    public static StructureTree[] unregister(StructureTree... trees) {
        for (StructureTree each : trees) 
            unregister(each);
        return trees;
    }
    
    public static StructureTree unregister(StructureTree tree) {
        unregisterTree(tree);
        if (tree.structure instanceof Saveable)
            ((Saveable) tree.structure).onSave(StructurePlugin.presistentStructuresFolder());
        return tree;
    }
    
    public static class Unsafe {
        public static void unregisterAll() {
            for (List<StructureTree> treeList : trees.values())
                for (StructureTree tree : treeList) {
                    if (tree.structure instanceof Saveable)
                        ((Saveable) tree.structure).onSave(StructurePlugin.presistentStructuresFolder());
                }
            trees.clear();
            // Async
            for (List<StructureTree> treeList : asyncTrees.values())
                for (StructureTree tree : treeList) {
                    if (tree.structure instanceof Saveable)
                        ((Saveable) tree.structure).onSave(StructurePlugin.presistentStructuresFolder());
                }
            trees.clear();
        }
    }
    
    @Data
    @Getter
    @RequiredArgsConstructor
    public static class MatchResult implements moe.kira.structure.misc.Present {
        public static final MatchResult EMPTY_RESULT = new MatchResult(null, null, false);
        
        @Override
        public boolean isPresent() {
            return this != EMPTY_RESULT;
        }
        
        @Override
        public boolean isEmpty() {
            return this == EMPTY_RESULT;
        }
        
        private final StructureTree structureTree;
        private final CoordData fromCoords;
        private final boolean byLink;
    }
    
    public static void match(Block origin, LinkStage stage, Consumer<MatchResult> callback) {
        match(INTERNAL_LISTENER, origin, stage, callback, null, null);
    }
    
    public static void match(Class<? extends BlockEvent> assignedEvent, Block origin, LinkStage stage, Consumer<MatchResult> callback) {
        match(assignedEvent, origin, stage, callback, null, null);
    }
    
    public static void match(Block origin, LinkStage stage, Consumer<MatchResult> callback, @Nullable Predicate<StructureTree> structurePredicate) {
        match(INTERNAL_LISTENER, origin, stage, callback, structurePredicate, null);
    }
    
    public static void match(Class<? extends BlockEvent> assignedEvent, Block origin, LinkStage stage, Consumer<MatchResult> callback, @Nullable Predicate<StructureTree> structurePredicate) {
        match(assignedEvent, origin, stage, callback, structurePredicate, null);
    }
    
    public static void match(Block origin, LinkStage stage, Consumer<MatchResult> callback, @Nullable Predicate<StructureTree> structurePredicate, @Nullable Predicate<Coord> coordPredicate) {
        match(INTERNAL_LISTENER, origin, stage, callback, structurePredicate, coordPredicate);
    }
    
    public static void match(Class<? extends BlockEvent> assignedEvent, Block origin, LinkStage stage, Consumer<MatchResult> callback, @Nullable Predicate<StructureTree> structurePredicate, @Nullable Predicate<Coord> coordPredicate) {
        VolatileBooleanReference sharedSignal = VolatileBooleanReference.empty();
        Bukkit.getScheduler().runTaskAsynchronously(StructurePlugin.getInstance(), () -> {
            match0(asyncTrees.get(assignedEvent), origin, stage, callback, sharedSignal, structurePredicate, coordPredicate);
        });
        match0(trees.get(assignedEvent), origin, stage, callback, sharedSignal, structurePredicate, coordPredicate);
    }
    
    protected static void match0(Iterable<StructureTree> trees, Block origin, LinkStage stage, Consumer<MatchResult> callback, VolatileBooleanReference foundAsynchronously, @Nullable Predicate<StructureTree> structurePredicate, @Nullable Predicate<Coord> coordPredicate) {
        Tree_Loop: for (StructureTree tree : trees) {
            if (foundAsynchronously.has()) return;
            if (structurePredicate != null && !structurePredicate.test(tree)) continue;
            
            switch (matchCoords(tree.coordData, origin, coordPredicate)) {
                case SUCCESS:
                    foundAsynchronously.set(true);
                    callback.accept(new MatchResult(tree, tree.coordData, false));
                    return;
                case FAILED:
                    if (tree.linkedCoords == null) continue Tree_Loop;
                    for (LinkStage each : tree.stages) {
                        if (each != stage && each != LinkStage.ANY && stage != LinkStage.ANY) continue;
                        Coord_Loop: for (CoordData linked : tree.linkedCoords) {
                            if (foundAsynchronously.has()) return;
                            switch (matchCoords(linked, origin, coordPredicate)) {
                                case SUCCESS:
                                    foundAsynchronously.set(true);
                                    callback.accept(new MatchResult(tree, linked, true));
                                    return;
                                case FAILED:
                                    continue Coord_Loop; // mirror coords of next coord
                            }
                        }
                        continue Tree_Loop; // none link matches, next tree!
                    }
            }
        }
        
        if (trees == StructureAPI.trees)
            callback.accept(MatchResult.EMPTY_RESULT);
    }
    
    public static enum LinkStage {
        ANY,
        PARK, // manual usage
        CREATE,
        DESTROY,
        INTERACT;
    }
    
    static enum MatchResult0 {
        SUCCESS,
        FAILED;
    }
    
    protected static MatchResult0 matchCoords(CoordData coordData, Block origin, @Nullable Predicate<Coord> coordPredicate) {
        boolean abort = coordData.forEachIfAbort(coord -> {
            if (coord.material == null) {
                if (Type.AIR.is(Coord.offset(origin, coord).getBlockData()))
                    return true;
            } else {
                if (!coord.material.is(Coord.offset(origin, coord).getBlockData()))
                    return true;
            }
            
            if (coordPredicate != null && !coordPredicate.test(coord)) {
                return true;
            }
            
            return false;
        });
        
        return abort ? MatchResult0.FAILED : MatchResult0.SUCCESS;
    }
    
    @Data
    @Getter
    @Builder
    @Immutable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Trigger implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @Default boolean leftClickable = false;
        @Default boolean rightClickable = false;
        @Default boolean offHandClickable = false;
    }
    
    @Getter
    @Builder
    @EqualsAndHashCode
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class Coord implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public static final String DEFAULT_SIGNATURE = "DEFAULT";
        public static final Trigger DEFAULT_TRIGGER = new Trigger();
        
        @Nullable final Type material; // null = any block except air
        final int x;
        final int y;
        final int z;
        final String signature;
        @Nullable Trigger trigger; // null may from mistakes
        
        public static Coord wrap(Type type, int blockX, int blockY, int blockZ) {
            return new Coord(type, blockX, blockY, blockZ, DEFAULT_SIGNATURE, DEFAULT_TRIGGER);
        }
        
        public static Coord wrap(Type type, int blockX, int blockY, int blockZ, @Nullable Trigger trigger) {
            return new Coord(type, blockX, blockY, blockZ, DEFAULT_SIGNATURE, trigger);
        }
        
        public static Coord wrap(Type type, int blockX, int blockY, int blockZ, String signature) {
            return new Coord(type, blockX, blockY, blockZ, signature, DEFAULT_TRIGGER);
        }
        
        public static Coord wrap(Type type, int blockX, int blockY, int blockZ, String signature, @Nullable Trigger trigger) {
            return new Coord(type, blockX, blockY, blockZ, signature, trigger);
        }
        
        public static Coord[] contact(Coord... coords) {
            return concat(coords);
        }
        public static Coord[] concat(Coord... coords) {
            return coords;
        }
        
        public Coord setTrigger(@Nullable Trigger trigger) {
            this.trigger = trigger;
            return this;
        }
        
        public static Coord[] reserveLink(StructureTree tree, Coord... coords) {
            return reserveLink(tree.coordData.origin, coords);
        }
        public static Coord[] reserveLink(CoordData data, Coord... coords) {
            return reserveLink(data.origin, coords);
        }
        public static Coord[] reserveLink(Coord origin, Coord... coords) {
            int arrayIndex = 0;
            Coord[] reserved = new Coord[coords.length];
            for (Coord each : coords)
                reserved[arrayIndex++] = each.offset(origin);
            return reserved;
        }
        public Coord reserveLink(StructureTree tree) {
            return reserveLink(tree.getCoordData());
        }
        public Coord reserveLink(CoordData data) {
            return offset(data.getOrigin());
        }
        public Coord reserveLink(Coord origin) {
            return offset(origin); 
        }
        public Coord offset(Coord other) {
            return offset(other.getX(), other.getY(), other.getZ());
        }
        public Coord offset(int offsetX, int offsetY, int offsetZ) {
            return wrap(material, x + offsetX, y + offsetY, z + offsetZ, signature, trigger);
        }
        
        public static Block reserveLink(Block source, Coord origin) {
            return offset(source, origin);
        }
        /**
         * Offsets the block with `x`, `y` and `z` of the given coord, similar with `getRelative`.
         * @param source
         * @param relative
         * @return block offseted from source by coord
         */
        public static Block offset(Block source, Coord relative) {
            return source.getRelative(relative.getX(), relative.getY(), relative.getZ());
        }
        
        public static Block reserveToOrigin(Block source, Coord origin) {
            return source.getRelative(-origin.getX(), -origin.getY(), -origin.getZ());
        }
        
        /**
         * Whether this is a origin coord (0, 0, 0)
         * @return
         */
        public boolean isOrigin() {
            return x == 0 && y == 0 && z == 0;
        }
    }
    
    @Data
    @RequiredArgsConstructor
    public static class CoordData implements Serializable {
        private static final long serialVersionUID = 1L;
        
        @Getter final Coord origin;
        @Getter final Map<String, Set<Coord>> coordMap;
        final int size;
        
        public Coord origin() {
            return origin;
        }
        
        public int size() {
            return size;
        }
        
        public void forEach(Consumer<Coord> consumer) {
            for (Set<Coord> mappedCoords : coordMap.values())
                for (Coord coord : mappedCoords)
                    consumer.accept(coord);
        }
        
        public void forEachIf(Predicate<Coord> predicate, Consumer<Coord> consumer) {
            for (Set<Coord> mappedCoords : coordMap.values())
                for (Coord coord : mappedCoords)
                    if (predicate.test(coord))
                        consumer.accept(coord);
        }
        
        public boolean forEachIfAbort(Predicate<Coord> predicate, Consumer<Coord> consumer) {
            for (Set<Coord> mappedCoords : coordMap.values())
                for (Coord coord : mappedCoords) {
                    if (predicate.test(coord))
                        return true;
                    consumer.accept(coord);
                }
            return false;
        }
        
        public boolean forEachIfAbort(Predicate<Coord> predicate) {
            for (Set<Coord> mappedCoords : coordMap.values())
                for (Coord coord : mappedCoords) {
                    if (predicate.test(coord))
                        return true;
                }
            return false;
        }
        
        public boolean forEachIfOnce(Predicate<Coord> predicate, Consumer<Coord> consumer) {
            for (Set<Coord> mappedCoords : coordMap.values())
                for (Coord coord : mappedCoords)
                    if (predicate.test(coord)) {
                        consumer.accept(coord);
                        return true;
                    }
            return false;
        }
    }
    
    @Getter
    @EqualsAndHashCode
    @RequiredArgsConstructor
    public static class StructureTree implements Serializable {
        private static final long serialVersionUID = 1L;
        
        final Structure structure;
        final CoordData coordData;
        
        private LinkStage[] stages;
        @Nullable private CoordData[] linkedCoords;
        
        /**
         * Register as a listener to the Bukkit
         * @param plugin
         * @return itself
         */
        public StructureTree registerAsListener(Plugin plugin) {
            if (structure instanceof Listener) Bukkit.getPluginManager().registerEvents((Listener) structure, plugin);
            return this;
        }
        
        /**
         * Unregister as a listener from the Bukkit
         * @return itself
         */
        public StructureTree unregisterAsListener() {
            if (structure instanceof Listener) HandlerList.unregisterAll((Listener) this);
            return this;
        }
        
        /**
         * Register as a structure to the API
         * @return itself
         */
        public StructureTree registerAsStructure() {
            return StructureAPI.register(this);
        }
        
        /**
         * Unregister as a structure from the API
         * @return itself
         */
        public StructureTree unregisterAsStructure() {
            return StructureAPI.unregister(this);
        }
        
        private static Map<String, Set<Coord>> mapSignatures(Coord[] coords, IntReference sizeRef) {
            Map<String, Set<Coord>> signMap = Maps.newHashMap();
            for (Coord each : coords) {
                Set<Coord> coordSet = signMap.get(each.signature);
                
                if (coordSet == null)
                    signMap.put(each.signature, (coordSet = Sets.newHashSet(each)));
                
                coordSet.add(each);
                sizeRef.add(); // count total size meantime
            }
            return signMap;
        }
        
        public static StructureTree wrap(Coord... coords) {
            return wrap(EMPTY_STRUCTURE, coords);
        }
        
        public static StructureTree wrap(Structure structure, Coord... coords) {
            // search for origin
            Coord origin = coords[0];
            if (!coords[0].isOrigin()) {
                for (Coord each : coords)
                if (each.isOrigin()) {
                    origin = each;
                    break;
                }
            }
            
            IntReference sizeRef = IntReference.empty();
            return new StructureTree(structure, new CoordData(origin, mapSignatures(coords, sizeRef), sizeRef.get()));
        }
        
        public StructureTree link() {
            return link(LinkStage.ANY);
        }
        
        public StructureTree link(LinkStage... stages) {
            this.stages = stages;
            
            if (coordData.size() == 1)
                return this; // only one coord, no need to linking
            
            linkedCoords = new CoordData[coordData.size() - 1]; // size = [mirrorOrigin]
            
            IntReference primaryIndex = IntReference.empty();
            coordData.forEachIf(newOrigin -> !newOrigin.isOrigin(), newOrigin -> { // every coord (select new origin)
                Coord[] mirrorCoords = new Coord[coordData.size()];
                
                // Offset calc example:
                //  3 -> 0 : offsetY = -3 (-origin)
                // -3 -> 0 : offsetY = +3 (-origin)
                int offsetX = -newOrigin.getX();
                int offsetY = -newOrigin.getY();
                int offsetZ = -newOrigin.getZ();
                
                IntReference arrayIndex = IntReference.empty();
                coordData.forEach(coord -> { // every coord (mirror to new origin)
                    mirrorCoords[arrayIndex.addThen()] = coord.offset(offsetX, offsetY, offsetZ); // (include itself)
                });
                
                IntReference sizeRef = IntReference.empty();
                linkedCoords[primaryIndex.addThen()] = new CoordData(newOrigin, mapSignatures(mirrorCoords, sizeRef), sizeRef.get());
            });
            
            return this;
        }
    }
    
    /**
     * A base structure, and the base interface for all structure interfaces,
     * you will manually implement this for your structure if it's not function.
     */
    public interface Structure {
        /**
         * This will be called when a structure be created.
         * @param event The source event cause the creation.
         * @param tree The wrapped structure with additional data.
         */
        void onCreate(BlockPlaceEvent event, MatchResult tree);
    }
    
    public interface ReactAtBlock {
        void onBlockEvent(BlockEvent event, MatchResult tree);
    }
    
    /**
     * A clickable structure, you will may implement this for your structure
     * that have any block that will perform something when interact with it.
     */
    public interface Interactable extends Structure { // TODO unused
        /**
         * This will be called when a structure be interacted.
         * @param event The source event cause the interact.
         * @param structure The wrapped structure with additional data.
         * @param coord The interacted coord in the structure.
         */
        void onInteract(PlayerInteractEvent event, MatchResult structure, Coord coord);
    }
    
    /**
     * A clickable structure, you will definitely implement this for your structure
     * that have any block that will perform something when clicked.
     */
    public interface Clickable extends Interactable {
        /**
         * This will be called when a structure be clicked.
         * @param event The source event cause the click.
         * @param structure The wrapped structure with additional data.
         * @param coord The clicked coord in the structure.
         */
        @Override // TODO do not override
        void onInteract(PlayerInteractEvent event, MatchResult structure, Coord coord);
    }
    
    public interface AsyncSearch extends Clickable {
        //@Async
        @Override
        void onInteract(PlayerInteractEvent event, MatchResult structure, Coord coord);
    }
    
    /**
     * A left-only clickable structure, you will may implement this for your
     * structure that only have clickable and left-only clickable coords.
     */
    public interface LeftOnly extends Clickable {}
    
    /**
     * A right-only clickable structure, you will may implement this for your
     * structure that only have clickable and right-only clickable coords.
     */
    public interface RightOnly extends Clickable {}
    
    /**
     * A mainhand-only clickable structure, you will may implement this for your
     * structure that only have clickable and mainhand-only clickable coords.
     */
    public interface MainHandOnly extends Clickable {}
    
    /**
     * An offhand-only clickable structure, you will may implement this for your
     * structure that only have clickable and offhand-only clickable coords.
     */
    public interface OffHandOnly extends Clickable {} // TODO unused
    
    /**
     * A structure that will save data when disabled and load data when enabled.
     */
    public interface Saveable extends Structure {
        /**
         * This will be called when plugin disabled.
         * @param folder The common data folder
         */
        void onSave(File folder);
        
        /**
         * This will be called when plugin enabled.
         * @param folder The common data folder
         */
        void onLoad(File folder);
    }
}
