package miyucomics.hexical.data

import java.util.*

object EvokeState {
	val active: HashMap<UUID, Boolean> = HashMap()
	val duration: HashMap<UUID, Int> = HashMap()

	@JvmStatic
	fun isEvoking(uuid: UUID): Boolean = active.getOrDefault(uuid, false)

	@JvmStatic
	fun getDuration(uuid: UUID): Int = duration.getOrDefault(uuid, -1)
}