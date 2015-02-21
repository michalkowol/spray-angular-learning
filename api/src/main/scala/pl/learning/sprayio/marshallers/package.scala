package pl.learning.sprayio

import pl.learning.sprayio.marshallers.YamlMediaTypes._

import com.paypal.cascade.json.JsonUtil
import com.paypal.cascade.xml.XmlUtil
import com.paypal.cascade.yaml.YamlUtil
import spray.http.{MediaType, ContentTypeRange, HttpEntity, ContentType}
import spray.httpx.marshalling.Marshaller
import spray.http.MediaTypes._
import spray.httpx.unmarshalling.Unmarshaller

package object marshallers {

  private val  xmlSupportedMediaTypes = Seq(
    `text/xml`,
    `application/xml`,
    `application/xhtml+xml`
  )

  private val supportedMediaTypes =
    `application/json` +: (xmlSupportedMediaTypes ++ yamlSupportedMediaTypes)

  private def mapToContentType(mediaType: MediaType): ContentType = ContentType(mediaType)
  private val supportedContentTypes = supportedMediaTypes.map(mapToContentType)
  private val xmlSupportedContentTypes = xmlSupportedMediaTypes.map(mapToContentType)
  private val yamlSupportedContentTypes = yamlSupportedMediaTypes.map(mapToContentType)

  private val supportedContentTypeRange = supportedMediaTypes.map(mediaType => ContentTypeRange(mediaType))

  private val jsonMarshaller =
    Marshaller.delegate[Any, String](`application/json`) { value => JsonUtil.toJson(value).get }

  private val xmlMarshaller =
    Marshaller.delegate[Any, String](xmlSupportedContentTypes: _*) { value => XmlUtil.toXml(value).get }

  private val yamlMarshaller =
    Marshaller.delegate[Any, String](yamlSupportedContentTypes: _*) { value => YamlUtil.toYaml(value).get }

  implicit val marshaller = Marshaller[Any] { (value, ctx) =>
    ctx.tryAccept(supportedContentTypes) match {
      case Some(contentType) if jsonContentType(contentType) =>
        jsonMarshaller(value, ctx)
      case Some(contentType) if xmlContentType(contentType) =>
        xmlMarshaller(value, ctx)
      case Some(contentType) if yamlContentType(contentType) =>
        yamlMarshaller(value, ctx)
      case _ =>
        ctx.rejectMarshalling(supportedContentTypes)
    }
  }

  implicit def unmarshaller[T: Manifest]: Unmarshaller[T] = Unmarshaller[T](supportedContentTypeRange : _*) {
    case HttpEntity.NonEmpty(contentType, data) if jsonContentType(contentType) =>
      JsonUtil.fromJson[T](data.asString).get
    case HttpEntity.NonEmpty(contentType, data) if xmlContentType(contentType) =>
      XmlUtil.fromXml[T](data.asString).get
    case HttpEntity.NonEmpty(contentType, data) if yamlContentType(contentType) =>
      YamlUtil.fromYaml[T](data.asString).get
  }

  private def jsonContentType(contentType: ContentType): Boolean = contentType.mediaType.value.contains("json")
  private def xmlContentType(contentType: ContentType): Boolean = contentType.mediaType.value.contains("xml")
  private def yamlContentType(contentType: ContentType): Boolean = contentType.mediaType.value.contains("yaml")
}
