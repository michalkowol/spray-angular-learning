package com.paypal.cascade

import scala.util.Try

package object yaml {

  implicit class UnmarshallableYAML(str: String) {
    def fromYaml[T: Manifest]: Try[T] = YamlUtil.fromYaml[T](str)
  }

  implicit class ConvertibleYAML(convertMe: Any) {
    def convertYamlValue[T: Manifest]: Try[T] = YamlUtil.convertValue[T](convertMe)
  }

  implicit class MarshallableYAML[T](marshallMe: T) {
    def toYaml: Try[String] = YamlUtil.toYaml(marshallMe)
  }
}
