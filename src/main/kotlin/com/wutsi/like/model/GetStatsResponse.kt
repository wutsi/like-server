package com.wutsi.like.model

import kotlin.Long
import kotlin.String

public data class GetStatsResponse(
  public val link: String = "",
  public val count: Long = 0,
  public val countText: String = ""
)
