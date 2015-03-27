package com.paypal.cascade

import scala.util.Try

package object xml {

  implicit class UnmarshallableXML(str: String) {
    def fromXml[T: Manifest]: Try[T] = XmlUtil.fromXml[T](str)
  }

  implicit class ConvertibleXML(convertMe: Any) {
    def convertXmlValue[T: Manifest]: Try[T] = XmlUtil.convertValue[T](convertMe)
  }

  implicit class MarshallableXML[T](marshallMe: T) {
    def toXml: Try[String] = XmlUtil.toXml(marshallMe)
  }
}
