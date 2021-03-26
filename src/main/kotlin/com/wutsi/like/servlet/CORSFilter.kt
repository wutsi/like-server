package com.wutsi.like.servlet

import org.springframework.stereotype.Service
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class CORSFilter : Filter {
    override fun doFilter(req: ServletRequest, resp: ServletResponse?, chain: FilterChain) {
        (resp as HttpServletResponse).addHeader("Access-Control-Allow-Origin", "*")
        resp.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE")

        if ((req as HttpServletRequest).getMethod().equals("OPTIONS")) {
            resp.addHeader("Access-Control-Allow-Headers", "*")
            resp.status = HttpServletResponse.SC_ACCEPTED
            return
        }
        chain.doFilter(req, resp)
    }
}
