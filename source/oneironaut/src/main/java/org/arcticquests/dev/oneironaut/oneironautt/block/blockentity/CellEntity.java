package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.MiscAPIKt;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellEntity extends BlockEntity {

    /*
     * the plan is to eventually make it act as a 3D cellular automaton, with rule B6/S567 as mentioned here https://conwaylife.com/wiki/Three-dimensional_cellular_automaton
     * will produce plasmodial psyche, and something else that I haven't decided
     */
    public final Map<BlockPos, BlockState> neighborMap;
    public BlockPos initialPos;
    private Boolean verified;

    public CellEntity(BlockPos pos, BlockState state){
        super(OneironautBlockRegistry.CELL_ENTITY.get(), pos, state);
        this.initialPos = this.worldPosition;
        Level world = this.level;
        this.neighborMap = new HashMap<>();
        if (world != null){
            for(BlockPos neighborPos : getNeighbors(this.worldPosition)){
                neighborMap.put(neighborPos, world.getBlockState(neighborPos));
            }
        }
        this.verified = false;
    }

    public void tick(Level world, BlockPos pos, BlockState state){
        if (this.initialPos == null){
            this.initialPos = pos;
        }
        removeIfMoved();
    }

    public boolean updateNeighborMap(){
        if (removeIfMoved() || this.level == null){
            return false;
        }
        for (BlockPos neighborPos : this.neighborMap.keySet()){
            neighborMap.put(neighborPos, this.level.getBlockState(neighborPos));
        }
        return true;
    }

    private boolean removeIfMoved(){
        if (!this.initialPos.equals(this.worldPosition) && level != null){
            Oneironaut.LOGGER.info("position mismatch: initial pos " + this.initialPos.toShortString() + " is not " + this.worldPosition.toShortString());
            level.removeBlockEntity(this.worldPosition);
            level.removeBlock(this.worldPosition, false);
            return true;
        }
        return false;
    }

    private static List<BlockPos> getNeighbors(BlockPos pos){
        return MiscAPIKt.getPositionsInCuboid(pos.offset(-1,-1,-1), pos.offset(1, 1, 1), pos);
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (this.initialPos == null){
            this.initialPos = this.worldPosition;
        }
        if (this.verified == null){
            this.verified = false;
        }
        nbt.putIntArray("initialPos", new int[]{this.initialPos.getX(), this.initialPos.getY(), this.initialPos.getZ()});
        nbt.putBoolean("verified", this.verified);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        int[] posArray = nbt.getIntArray("initialPos");
        if (posArray.length == 3) {
            this.initialPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
        } else {
            this.initialPos = this.worldPosition;
        }
        this.verified = nbt.getBoolean("verified");
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public boolean getVerified(){
        return this.verified != null && this.verified;
    }

    public void setVerified(boolean verification){
        this.verified = verification;
    }
}