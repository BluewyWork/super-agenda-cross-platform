package data.models

import domain.models.Membership
import domain.models.TasksStatistics
import domain.models.UserForAdminView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import util.ObjectIdSerializer

@Serializable
enum class MembershipModel {
   FREE,
   PREMIUM
}

@Serializable
data class UserForAdminViewModel(
   @Serializable(with = ObjectIdSerializer::class)
   val id: ObjectId,
   val username: String,
   @SerialName(value = "tasks_statistics") val tasksStatistics: TasksStatisticsModel,
   val membership: MembershipModel
)

@Serializable
data class TasksStatisticsModel(
   @SerialName(value = "num_not_started") val numNotStarted: Int,
   @SerialName(value = "num_ongoing") val numOngoing: Int,
   @SerialName(value = "num_completed") val numCompleted: Int
)

fun UserForAdminViewModel.toDomain() = UserForAdminView(
   id = id,
   username = username,
   tasksStatistics = tasksStatistics.toDomain(),

   membership = when (membership) {
      MembershipModel.FREE -> Membership.FREE
      MembershipModel.PREMIUM -> Membership.PREMIUM
   }
)

fun TasksStatisticsModel.toDomain() = TasksStatistics(
   numNotStarted = numNotStarted,
   numOngoing = numOngoing,
   numCompleted = numCompleted
)
