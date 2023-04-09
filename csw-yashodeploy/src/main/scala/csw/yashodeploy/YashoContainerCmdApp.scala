package csw.yashodeploy

import csw.framework.deploy.containercmd.ContainerCmd
import csw.prefix.models.Subsystem

object YashoContainerCmdApp extends App {

  ContainerCmd.start("yasho_container_cmd_app", Subsystem.withNameInsensitive("CSW"), args)

}
