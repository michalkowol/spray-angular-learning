package pl.learning.sprayio

import com.paypal.cascade.json.JsonUtil
import com.paypal.cascade.xml.XmlUtil
import com.paypal.cascade.yaml.YamlUtil
import spray.http.ContentType
import spray.httpx.marshalling.Marshaller
import spray.http.MediaTypes._

package object marshallers {

  private val xmlSupportedContentTypes: Seq[ContentType] = Seq(
    `text/xml`,
    `application/xml`,
    `application/xhtml+xml`
  )

  private val supportedContentTypes =
    ContentType(`application/json`) +: (xmlSupportedContentTypes ++ YamlMediaTypes.yamlSupportedContentTypes)

  private val jsonMarshaller =
    Marshaller.delegate[Any, String](`application/json`) { value => JsonUtil.toJson(value).get }

  private val xmlMarshaller =
    Marshaller.delegate[Any, String](xmlSupportedContentTypes: _*) { value => XmlUtil.toXml(value).get }

  private val yamlMarshaller =
    Marshaller.delegate[Any, String](YamlMediaTypes.yamlSupportedContentTypes: _*) { value => YamlUtil.toYaml(value).get }

  implicit val marshaller = Marshaller[Any] { (value, ctx) =>
    ctx.tryAccept(supportedContentTypes) match {
      case Some(contentType) if contentType.mediaType.value.contains("json") =>
        jsonMarshaller(value, ctx)
      case Some(contentType) if contentType.mediaType.value.contains("xml") =>
        xmlMarshaller(value, ctx)
      case Some(contentType) if contentType.mediaType.value.contains("yaml") =>
        yamlMarshaller(value, ctx)
      case _ =>
        ctx.rejectMarshalling(supportedContentTypes)
    }
  }
}
