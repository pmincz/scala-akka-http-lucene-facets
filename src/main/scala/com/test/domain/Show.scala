package com.test.domain

/**
 * Created by pmincz on 7/26/15.
 */
case class Show(id: Long, original_name: String, name: String, overview: String, popularity: Double, vote_average: Double, vote_count: Int)

case class ShowResult(results: List[Show])

case class ShowDetail(id: Long, in_production: Boolean, name: String, number_of_episodes: Int = 0, number_of_seasons: Int, original_name: String,
                      overview: String, popularity: Double, vote_average: Double, vote_count: Int, last_air_date: String, status: String)