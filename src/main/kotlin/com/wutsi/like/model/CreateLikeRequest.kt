package com.wutsi.like.model

import javax.validation.constraints.NotBlank
import kotlin.Long
import kotlin.String

public data class CreateLikeRequest(
    @get:NotBlank
    public val canonicalUrl: String = "",
    public val deviceUUID: String? = null,
    public val userId: Long? = null
)
