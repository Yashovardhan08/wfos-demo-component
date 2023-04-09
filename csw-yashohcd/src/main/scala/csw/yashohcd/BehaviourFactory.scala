//package csw.yashohcd
//
//import akka.actor.typed.scaladsl.ActorContext
//import csw.framework.models.CswContext
//import csw.framework.internal.container.ContainerBehaviourFactory
//import csw.framework.scaladsl.{ ComponentHandlers}
//import csw.command.client.messages.TopLevelActorMessage
//
//class YashohckBehaviourFactory extends ComponentBehaviourFactory {
//  override def handlers(ctx: ActorContext[TopLevelActorMessage], cswCtx: CswContext): ComponentHandlers =
//    new YashohcdHandlers(ctx, cswCtx)
//
//}
//
//BehaviourFactory
//
