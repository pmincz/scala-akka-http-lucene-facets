package com.test.lucene

import com.test.domain.{SearchResult, ShowDetail, Show}
import com.test.json.JsonSupport
import com.test.provider.LuceneProvider
import org.apache.lucene.facet.{DrillDownQuery, LabelAndValue, FacetsCollector}
import org.apache.lucene.facet.taxonomy.FastTaxonomyFacetCounts
import org.apache.lucene.index.{DirectoryReader, Term}
import org.apache.lucene.search._
import org.json4s.jackson.Serialization.read
import scala.collection.JavaConversions._

/**
 * Created by pmincz on 7/26/15.
 */
object Searcher extends LuceneProvider with JsonSupport {

  private def readSearcher(search: IndexSearcher => SearchResult) = {
    val indexReader = DirectoryReader.open(directory)
    val searcher = new IndexSearcher(indexReader)
    val data = search(searcher)
    indexReader.close()
    data
  }

  def search(indexFields: List[String], searchCriteria: String, facets: Map[String, String]): SearchResult = {
    val query = new DrillDownQuery(facetsConfig, searchCriteria.equals("") match {
      case false => {
        val subQuery = new BooleanQuery
        indexFields.foreach(field => subQuery.add(new TermQuery(new Term(field, searchCriteria)), BooleanClause.Occur.SHOULD))
        subQuery
      }
      case true => new MatchAllDocsQuery
    })
    facets.foreach { facet =>
      query.add(facet._1, facet._2)
    }

    readSearcher(indexSearcher => {

      val fc = new FacetsCollector()
      val dc = TopFieldCollector.create(new Sort(), 100, false, true, false)
      indexSearcher.search(query, MultiCollector.wrap(fc, dc))

      val docs = dc.topDocs().scoreDocs.collect {
        case docId: ScoreDoc => read[ShowDetail](indexSearcher.doc(docId.doc).get("json"))
      }

      val taxonomyFacets = new FastTaxonomyFacetCounts(taxonomyReader, facetsConfig, fc)
      val facets = taxonomyFacets.getAllDims(10).toList

      SearchResult(results = docs toList, facets = facets)
    })
  }

}
