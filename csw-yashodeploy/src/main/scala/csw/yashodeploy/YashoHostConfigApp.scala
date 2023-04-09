package csw.yashodeploy

import csw.framework.deploy.hostconfig.HostConfig
import csw.prefix.models.Subsystem

object YashoHostConfigApp extends App {

  HostConfig.start("yasho_host_config_app", Subsystem.withNameInsensitive("CSW"), args)

}
