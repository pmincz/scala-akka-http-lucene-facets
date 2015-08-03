package com.test.service

import scala.collection.JavaConversions._

import com.test.client.ShowClient
import com.test.domain.{SearchResult, ShowDetail, Show}
import com.test.lucene.{Indexer, Searcher}
import com.test.lucene.Searcher._
import com.test.provider.ActorSystemProvider
import org.apache.lucene.index.DirectoryReader

import scala.concurrent.{Promise, Future}

/**
 * Created by pmincz on 7/29/15.
 */
object ShowService extends ActorSystemProvider {

  lazy val fields = config.getStringList("lucene.index.fields") toList
  lazy val facets = config.getStringList("lucene.facets") toList

  def indexShows = {
    ShowClient.getShows flatMap { shows =>
      Future.sequence(shows.map(show => ShowClient.getShowDetail(show.id))).map(Indexer.index(_, fields, facets))
    }
  }

  def searchShows(query: String, parameters: Map[String, String]): Future[SearchResult] = {
    val facetFilters = parameters.filter(f => facets.contains(f._1))
    DirectoryReader.indexExists(directory) match {
      case true => Future { Searcher.search(fields, query.toLowerCase, facetFilters) }
      case false => {
        for {
          index <- indexShows
          results <- searchShows(query, parameters)
        } yield results
      }
    }
  }

}
