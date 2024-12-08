package domain.models

import org.bson.types.ObjectId

enum class Membership {
   FREE,
   PREMIUM,
}

data class UserForAdminView(
   val id: ObjectId,
   val username: String,
   val tasksStatistics: TasksStatistics,
   val membership: Membership
)

data class TasksStatistics (val numNotStarted: Int, val numOngoing: Int, val numCompleted: Int)