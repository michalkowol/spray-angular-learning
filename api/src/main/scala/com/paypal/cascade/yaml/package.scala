package com.paypal.cascade

import scala.util.Try

package object yaml {

  implicit class UnmarshallableXML(str: String) {
    def fromYaml[T: Manifest]: Try[T] = YamlUtil.fromYaml[T](str)
  }

  implicit class ConvertibleXML(convertMe: Any) {
    def convertYamlValue[T: Manifest]: Try[T] = YamlUtil.convertValue[T](convertMe)
  }

  implicit class MarshallableXML[T](marshallMe: T) {
    def toYaml: Try[String] = YamlUtil.toYaml(marshallMe)
  }
}
