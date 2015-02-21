package pl.learning.sprayio.marshallers

import spray.http.{ContentType, MediaType, MediaTypes}

object YamlMediaTypes {

  val `text/yaml` = MediaTypes.register(MediaType.custom("text/yaml"))
  val `text/x-yaml` = MediaTypes.register(MediaType.custom("text/x-yaml"))
  val `application/yaml` = MediaTypes.register(MediaType.custom("application/yaml"))
  val `application/x-yaml` = MediaTypes.register(MediaType.custom("application/x-yaml"))

  val yamlSupportedMediaTypes = Seq(
    `text/yaml`,
    `text/x-yaml`,
    `application/yaml`,
    `application/x-yaml`
  )
}
