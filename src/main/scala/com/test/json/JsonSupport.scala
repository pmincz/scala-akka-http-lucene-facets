package com.test.json

import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.apache.lucene.facet.FacetResult
import org.json4s._

/**
 * Created by pmincz on 7/12/15.
 */
trait JsonSupport extends Json4sSupport {

  implicit def formats: Formats = DefaultFormats + facetSerializer
  implicit def jacksonSerialization: Serialization = jackson.Serialization

  val facetSerializer = FieldSerializer[FacetResult](
    FieldSerializer.ignore("path") orElse FieldSerializer.ignore("childCount")
  )

}