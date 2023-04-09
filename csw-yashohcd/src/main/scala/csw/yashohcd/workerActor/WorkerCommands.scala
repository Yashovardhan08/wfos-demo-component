package csw.yashohcd.workerActor

import akka.actor.typed.ActorRef
import csw.params.commands.CommandResponse.ValidateCommandResponse
import csw.params.core.models.Id

sealed trait WorkerCommands

object WorkerCommands {
  case class MoveFront(runId: Id, distance: Long) extends WorkerCommands
  case class MoveBack(runId: Id, distance: Long)  extends WorkerCommands
  case class MoveLeft(runId: Id, distance: Long)  extends WorkerCommands
  case class MoveRight(runId: Id, distance: Long) extends WorkerCommands
//  case class validateMove(runId:Id,replyTo: ActorRef[ValidateCommandResponse]) extends WorkerCommands
}
