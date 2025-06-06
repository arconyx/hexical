package miyucomics.hexical.casting.patterns.vfx

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getPositiveDoubleUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.Vec3Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.hexical.HexicalMain
import miyucomics.hexical.registry.HexicalNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.util.math.Vec3d

class OpConfetti : SpellAction {
	override val argc = 2
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val position = args.getVec3(0, argc)
		env.assertVecInRange(position)

		when (args[1]) {
			is DoubleIota -> {
				val speed = args.getPositiveDoubleUnderInclusive(1, 2.0, argc) / 2
				return SpellAction.Result(Spell(position, Vec3d.ZERO, speed), MediaConstants.DUST_UNIT / 2, listOf())
			}
			is Vec3Iota -> {
				val velocity = args.getVec3(1, argc)
				val speed = velocity.length()
				if (speed > 2)
					throw MishapInvalidIota.of(args[1], 0, "small_vector")
				return SpellAction.Result(Spell(position, velocity.normalize(), speed), MediaConstants.DUST_UNIT / 2, listOf())
			}
			else -> throw MishapInvalidIota.of(args[1], 0, "number_or_vector")
		}
	}

	private data class Spell(val pos: Vec3d, val dir: Vec3d, val speed: Double) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {
			val packet = PacketByteBufs.create()
			packet.writeLong(HexicalMain.RANDOM.nextLong())
			packet.writeDouble(pos.x)
			packet.writeDouble(pos.y)
			packet.writeDouble(pos.z)
			packet.writeDouble(dir.x)
			packet.writeDouble(dir.y)
			packet.writeDouble(dir.z)
			packet.writeDouble(speed)
			env.world.players.forEach { player -> env.world.sendToPlayerIfNearby(player, false, pos.x, pos.y, pos.z, ServerPlayNetworking.createS2CPacket(HexicalNetworking.CONFETTI_CHANNEL, packet)) }
		}
	}
}