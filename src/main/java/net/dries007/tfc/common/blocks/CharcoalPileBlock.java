package net.dries007.tfc.common.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import net.dries007.tfc.client.TFCSoundTypes;

public class CharcoalPileBlock extends SnowBlock
{
    public CharcoalPileBlock(Properties properties)
    {
        super(properties);
    }

    @Override
    public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos)
    {
        BlockState blockstate = worldIn.getBlockState(pos.below());
        return Block.isFaceFull(blockstate.getCollisionShape(worldIn, pos.below()), Direction.UP) || (blockstate.getBlock() == this && blockstate.getValue(LAYERS) == 8);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid)
    {
        playerWillDestroy(world, pos, state, player);
        int prevLayers = state.getValue(LAYERS);
        if (prevLayers == 1)
        {
            return true;
        }
        return world.setBlock(pos, state.setValue(LAYERS, prevLayers - 1), world.isClientSide ? 11 : 3);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos)
    {
        if (!worldIn.isClientSide() && facing == Direction.DOWN)
        {
            if (facingState.is(TFCBlocks.CHARCOAL_PILE.get()))
            {
                int layersAt = stateIn.getValue(LAYERS);
                int layersUnder = facingState.getValue(LAYERS);
                if (layersUnder < 8)
                {
                    if (layersUnder + layersAt <= 8)
                    {
                        worldIn.setBlock(facingPos, facingState.setValue(LAYERS, layersAt + layersUnder), 3);
                        return Blocks.AIR.defaultBlockState();
                    }
                    else
                    {
                        worldIn.setBlock(facingPos, facingState.setValue(LAYERS, 8), 3);
                        return stateIn.setValue(LAYERS, layersAt + layersUnder - 8);
                    }
                }
            }
            if (!canSurvive(stateIn, worldIn, currentPos))
            {
                worldIn.destroyBlock(currentPos, true);
                return stateIn.setValue(LAYERS, stateIn.getValue(LAYERS) - 1);
            }
        }
        return stateIn;
    }

    @Override
    @SuppressWarnings("deprecation")
    public SoundType getSoundType(BlockState state)
    {
        return TFCSoundTypes.CHARCOAL;
    }
}