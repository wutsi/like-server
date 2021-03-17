package com.wutsi.like.model

import javax.validation.constraints.NotBlank
import kotlin.String

public data class CreateLikeRequest(
    @get:NotBlank
    public val link: String? = null
)
