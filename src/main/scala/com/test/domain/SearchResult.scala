package com.test.domain

import org.apache.lucene.facet.FacetResult

/**
 * Created by pmincz on 7/31/15.
 */
case class SearchResult(facets: List[FacetResult], results: List[ShowDetail])
