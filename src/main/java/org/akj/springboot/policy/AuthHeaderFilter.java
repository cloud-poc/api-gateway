package org.akj.springboot.policy;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * @author Jamie Zhang
 * inbound filter, to check if Authorization header presented in request or not
 */
@Component
@RefreshScope
public class AuthHeaderFilter extends ZuulFilter {
	
	@Value("${zuul.pre.authFilter.enabled:false}")
	private boolean enabled = false;
	
	@Value("${zuul.pre.authFilter.headers:null}")
	private String[] authHeaders = null;
	
	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return 0;
	}

	@Override
	public boolean shouldFilter() {
		return this.enabled;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();
		
		boolean retValue = Stream.of(authHeaders).anyMatch(h -> { return request.getHeader(h) != null;});
		
		if(!retValue) {
			ZuulException zuulException = new ZuulException("Access Denied", 401, "Authorization header missing.");
			
			throw zuulException;
		}
		
		return null;
	}
}