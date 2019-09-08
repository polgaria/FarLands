package site.geni.farlands.mixins.client.block.entity;

import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import site.geni.farlands.FarLands;

@SuppressWarnings("unused")
@Mixin(EnchantingTableBlockEntity.class)
abstract class EnchantingTableBlockEntityMixin {
	private static PlayerEntity playerEntity;

	/**
	 * Sets {@code playerEntity} to the {@link PlayerEntity} close to the {@link EnchantingTableBlockEntity} <br>
	 * Finds the closest player using {@link Double} for positions instead of {@link Float} in order to have it detect the player more precisely
	 *
	 * @param orig Original {@link PlayerEntity}
	 * @return Depending on the mod's configuration, either {@code orig} or a more precisely located player
	 * @author geni
	 * @see EnchantingTableBlockEntity#tick
	 */
	@ModifyVariable(
		at = @At(
			value = "JUMP",
			opcode = Opcodes.IFNULL,
			ordinal = 0
		),
		method = "tick"
	)
	private PlayerEntity setPlayerEntity(PlayerEntity orig) {
		if (FarLands.getConfig().fixParticlesEntities.getValue()) {
			final BlockPos pos = ((BlockEntityMixin) this).getPos();

			orig = (((BlockEntityMixin)this).getWorld().getClosestPlayer(pos.getX(), pos.getY(), pos.getZ()));
			playerEntity = orig;
		}

		return orig;
	}

	/**
	 * Sets the X position of the book (relative to the enchanting table?) using {@link Double} instead of {@link Float} (casted to {@link Double}) in order to have precise render positions
	 *
	 * @param orig Original X position of the book (relative to the enchanting table?)
	 * @return Depending on the configuration of the mod, either proper position or original position
	 * @author geni
	 */
	@ModifyVariable(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/math/BlockPos;getZ()I",
			ordinal = 1
		),
		method = "tick"
	)
	private double setX(double orig) {
		if (FarLands.getConfig().fixParticlesEntities.getValue()) {
			return playerEntity.x - (((BlockEntityMixin)this).getPos().getX() + 0.5D);
		}

		return orig;
	}

	/**
	 * Sets the Z position of the book (relative to the enchanting table?) using {@link Double} instead of {@link Float} (casted to {@link Double}) in order to have precise render positions
	 *
	 * @param orig Original Z position of the book (relative to the enchanting table?)
	 * @return Depending on the configuration of the mod, either proper position or original position
	 * @author geni
	 */
	@ModifyVariable(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/math/MathHelper;atan2(DD)D",
			ordinal = 0
		),
		index = 2,
		method = "tick"
	)
	private double setZ(double orig) {
		if (FarLands.getConfig().fixParticlesEntities.getValue()) {
			return playerEntity.z - (((BlockEntityMixin)this).getPos().getZ() + 0.5D);
		}

		return orig;
	}
}
