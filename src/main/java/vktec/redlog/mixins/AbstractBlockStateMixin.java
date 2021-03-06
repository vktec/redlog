package vktec.redlog.mixins;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vktec.redlog.RedEventLogger;
import vktec.redlog.events.RedBlockEvent;
import vktec.redlog.events.RedUpdateEvent;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
	@Shadow public abstract Block getBlock();

	@Inject(method = "onSyncedBlockEvent", at = @At("HEAD"))
	private void beginBlockEvent(World world, BlockPos pos, int type, int data, CallbackInfoReturnable ci) {
		if (world.isClient) return;
		RedEventLogger.begin(new RedBlockEvent(world.getTime(), (BlockState)(Object)this, pos, type, data));
	}

	@Inject(method = "onSyncedBlockEvent", at = @At("TAIL"))
	private void endBlockEvent(World world, BlockPos pos, int type, int data, CallbackInfoReturnable ci) {
		if (world.isClient) return;
		RedEventLogger.end();
	}

	@Inject(method = "neighborUpdate", at = @At("HEAD"))
	private void beginBlockUpdate(World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify, CallbackInfo ci) {
		if (world.isClient) return;
		RedEventLogger.begin(new RedUpdateEvent(RedUpdateEvent.UpdateType.BLOCK, world.getTime(), (BlockState)(Object)this, pos, block, fromPos));
	}

	@Inject(method = "neighborUpdate", at = @At("TAIL"))
	private void endBlockUpdate(World world, BlockPos pos, Block block, BlockPos posFrom, boolean notify, CallbackInfo ci) {
		if (world.isClient) return;
		RedEventLogger.end();
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
	private void beginStateUpdate(Direction direction, BlockState state, WorldAccess world, BlockPos pos, BlockPos fromPos, CallbackInfoReturnable ci) {
		if (!(world instanceof ServerWorld)) return;
		RedEventLogger.begin(new RedUpdateEvent(RedUpdateEvent.UpdateType.STATE, ((ServerWorld)world).getTime(), (BlockState)(Object)this, pos, state.getBlock(), fromPos));
	}

	@Inject(method = "getStateForNeighborUpdate", at = @At("HEAD"))
	private void endStateUpdate(Direction direction, BlockState state, WorldAccess world, BlockPos pos, BlockPos fromPos, CallbackInfoReturnable ci) {
		if (!(world instanceof ServerWorld)) return;
		RedEventLogger.end();
	}
}
