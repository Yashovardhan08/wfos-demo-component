package csw.yashohcd

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import csw.command.client.messages.TopLevelActorMessage
import csw.framework.models.CswContext
import csw.framework.scaladsl.ComponentHandlers
import csw.location.api.models.TrackingEvent
import csw.params.commands.CommandResponse._
import csw.params.commands.{CommandIssue, CommandResponse, ControlCommand, Observe, Setup}
import csw.params.core.generics.{Key, KeyType, Parameter}
import csw.time.core.models.UTCTime
import csw.params.core.models.Id
import csw.yashohcd.workerActor.WorkerCommands
import csw.yashohcd.workerActor.WorkerCommands.{MoveBack, MoveFront, MoveLeft, MoveRight}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.{FiniteDuration, SECONDS}

/**
 * Domain specific logic should be written in below handlers.
 * This handlers gets invoked when component receives messages/commands from other component/entity.
 * For example, if one component sends Submit(Setup(args)) command to Yashohcd,
 * This will be first validated in the supervisor and then forwarded to Component TLA which first invokes validateCommand hook
 * and if validation is successful, then onSubmit hook gets invoked.
 * You can find more information on this here : https://tmtsoftware.github.io/csw/commons/framework.html
 */
class YashohcdHandlers(ctx: ActorContext[TopLevelActorMessage], cswCtx: CswContext) extends ComponentHandlers(ctx, cswCtx) {

  import cswCtx._
  implicit val ec: ExecutionContextExecutor = ctx.executionContext
//  private val log                           = loggerFactory.getLogger

  private var x: Long         = 0
  private var y: Long         = 0
  private val XUpperLim: Long = 40
  private val XLowerLim: Long = 0
  private val YUpperLim: Long = 40
  private val YLowerLim: Long = 0
  private val workerActor = ctx.spawn(
    Behaviors.receiveMessage[WorkerCommands]((msg) => {
      msg match {
        case WorkerCommands.MoveFront(runId, distance) => {
//          log.trace(s" Worker actor trying to move ${distance} front")
          if (x + distance <= XUpperLim) {
            val when: UTCTime = UTCTime.after(FiniteDuration(2, SECONDS))
            timeServiceScheduler.scheduleOnce(when) {
              x = x + distance
              commandResponseManager.updateCommand(CommandResponse.Completed(runId))
            }
//            log.trace(s" Worker actor moved ${distance} front")
          }
          else {
            commandResponseManager.updateCommand(
              CommandResponse.Invalid(runId, CommandIssue.UnsupportedCommandInStateIssue(s"Cannot move the object front by ${distance}"))
            )
//            log.trace(s" Worker actor NOT moved ${distance} front")
          }
        }
        case WorkerCommands.MoveBack(runId, distance) => {
//          log.trace(s" Worker actor trying to move ${distance} back")
          if (x - distance >= XLowerLim) {
            val when: UTCTime = UTCTime.after(FiniteDuration(2, SECONDS))
            timeServiceScheduler.scheduleOnce(when) {
              x = x - distance
              commandResponseManager.updateCommand(CommandResponse.Completed(runId))
            }
//            log.trace(s" Worker actor moved ${distance} back")
          }
          else {
            commandResponseManager.updateCommand(
              CommandResponse.Invalid(runId, CommandIssue.UnsupportedCommandInStateIssue(s"Cannot move the object front by ${distance}"))
            )
//            log.trace(s" Worker actor NOT moved ${distance} back")
          }
        }
        case WorkerCommands.MoveLeft(runId, distance) => {
//          log.trace(s" Worker actor trying to move ${distance} left")
          if (y + distance <= YUpperLim) {
            val when: UTCTime = UTCTime.after(FiniteDuration(2, SECONDS))
            timeServiceScheduler.scheduleOnce(when) {
              y = y + distance
              commandResponseManager.updateCommand(CommandResponse.Completed(runId))
            }
//            log.trace(s" Worker actor moved ${distance} left")
          }
          else {
            commandResponseManager.updateCommand(
              CommandResponse.Invalid(runId, CommandIssue.UnsupportedCommandInStateIssue(s"Cannot move the object front by ${distance}"))
            )
//            log.trace(s" Worker actor NOT moved ${distance} left")
          }
        }
        case WorkerCommands.MoveRight(runId, distance) => {
//          log.trace(s" Worker actor trying to move ${distance} right")
          if (y - distance <= YLowerLim) {
            val when: UTCTime = UTCTime.after(FiniteDuration(2, SECONDS))
            timeServiceScheduler.scheduleOnce(when) {
              y = y - distance
              commandResponseManager.updateCommand(CommandResponse.Completed(runId))
            }
//            log.trace(s" Worker actor moved ${distance} left")
          }
          else {
            commandResponseManager.updateCommand(
              CommandResponse.Invalid(runId, CommandIssue.UnsupportedCommandInStateIssue(s"Cannot move the object front by ${distance}"))
            )
//            log.trace(s" Worker actor NOT moved ${distance} left")
          }
        }
      }
      Behaviors.same
    }),
    "workerActor"
  )

  override def initialize(): Unit = {
//    log.info("Initializing YashoHcd...")
    println("HCD initialized!!")
  }

  override def onLocationTrackingEvent(trackingEvent: TrackingEvent): Unit = {}

  override def validateCommand(runId: Id, controlCommand: ControlCommand): ValidateCommandResponse = {
//    log.info(s"Validating command ${controlCommand.commandName.name}")
    controlCommand.commandName.name match {
      case "moveFront" => Accepted(runId)
      case "moveRight" => Accepted(runId)
      case "moveLeft"  => Accepted(runId)
      case "moveBack"  => Accepted(runId)
//      case _ => Invalid(runId,CommandIssue.UnsupportedCommandIssue(s"Command ${_} not supported"))
    }
  }

  override def onSubmit(runId: Id, controlCommand: ControlCommand): SubmitResponse = {
    controlCommand match {
      case setupCommand: Setup     => onSetup(runId, setupCommand)
      case observeCommand: Observe => Error(runId, s"Observe command not supported (runId: $runId) ")
    }
  }

  def onSetup(runId: Id, setup: Setup): SubmitResponse = {
    setup.commandName.name match {
      case "moveFront" => {
        val distanceKey: Key[Long]         = KeyType.LongKey.make("distance")
        val distanceParam: Parameter[Long] = setup(distanceKey)
        val distance: Long                 = distanceParam.head
        workerActor ! MoveFront(runId, distance)
        Started(runId)
//        Completed(runId)
      }
      case "moveLeft" => {
        val distanceKey: Key[Long]         = KeyType.LongKey.make("distance")
        val distanceParam: Parameter[Long] = setup(distanceKey)
        val distance: Long                 = distanceParam.head
        workerActor ! MoveLeft(runId, distance)
        Started(runId)
      }
      case "moveRight" => {
        val distanceKey: Key[Long]         = KeyType.LongKey.make("distance")
        val distanceParam: Parameter[Long] = setup(distanceKey)
        val distance: Long                 = distanceParam.head
        workerActor ! MoveRight(runId, distance)
        Started(runId)
      }
      case "moveBack" => {
        val distanceKey: Key[Long]         = KeyType.LongKey.make("distance")
        val distanceParam: Parameter[Long] = setup(distanceKey)
        val distance: Long                 = distanceParam.head
        workerActor ! MoveBack(runId, distance)
        Started(runId)
      }
//      case _ => Invalid(runId,CommandIssue.UnsupportedCommandIssue(s"${_} not supported(runId: ${runId}"))
    }
  }

  override def onOneway(runId: Id, controlCommand: ControlCommand): Unit = {}

  override def onShutdown(): Unit = {
//    log.info("YashoHcd shutting down")
  }

  override def onGoOffline(): Unit = {}

  override def onGoOnline(): Unit = {}

  override def onDiagnosticMode(startTime: UTCTime, hint: String): Unit = {}

  override def onOperationsMode(): Unit = {}

}
