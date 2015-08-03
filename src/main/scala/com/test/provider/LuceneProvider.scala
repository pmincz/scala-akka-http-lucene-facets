package com.test.provider

import com.test.json.JsonSupport
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.facet.FacetsConfig
import org.apache.lucene.facet.taxonomy.directory.{DirectoryTaxonomyReader, DirectoryTaxonomyWriter}
import org.apache.lucene.index.IndexWriterConfig.OpenMode
import org.apache.lucene.index.{IndexWriterConfig, IndexWriter}
import org.apache.lucene.store.RAMDirectory

/**
 * Created by pmincz on 7/26/15.
 */
object LuceneProperties extends JsonSupport {

  lazy val directory = new RAMDirectory()
  lazy val taxonomyDirectory = new RAMDirectory()
  lazy val analyzer = new StandardAnalyzer()
  lazy val writer = new IndexWriter(directory, new IndexWriterConfig(analyzer).setOpenMode(OpenMode.CREATE)) {
    def withSession[T](f: IndexWriter => T): T = {
      val s = this
      var ok = false
      try {
        val res = f(s)
        ok = true
        res
      } finally {
        if(ok) {
          this.commit()
          this.close() // Let exceptions propagate normally
        } else {
          // f(s) threw an exception, so don't replace it with an Exception from close()
          try s.close() catch { case _: Throwable => }
        }
      }
    }
  }
  lazy val facetsConfig = new FacetsConfig()
  lazy val taxonomyWriter = new DirectoryTaxonomyWriter(taxonomyDirectory) {
    def withSession[T](f: DirectoryTaxonomyWriter => T): T = {
      val s = this
      var ok = false
      try {
        val res = f(s)
        ok = true
        res
      } finally {
        if(ok) {
          this.commit()
          this.close() // Let exceptions propagate normally
        } else {
          // f(s) threw an exception, so don't replace it with an Exception from close()
          try s.close() catch { case _: Throwable => }
        }
      }
    }
  }
  lazy val taxonomyReader = new DirectoryTaxonomyReader(taxonomyDirectory)

}

trait LuceneProvider {

  lazy val directory = LuceneProperties.directory
  lazy val analyzer = LuceneProperties.analyzer
  lazy val writer = LuceneProperties.writer
  lazy val facetsConfig = LuceneProperties.facetsConfig
  lazy val taxonomyWriter = LuceneProperties.taxonomyWriter
  lazy val taxonomyReader = LuceneProperties.taxonomyReader
}
