package org.akj.springboot.exception;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.akj.springboot.common.exception.BaseResponse;
import org.akj.springboot.common.exception.ResponseInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.GenericFilterBean;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomOAuth2ExceptionFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			chain.doFilter(request, response);

			logger.debug("Chain processed normally");
		} catch (Exception ex) {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			if (ex instanceof AccessDeniedException || ex instanceof AuthenticationException) {
				httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
				BaseResponse baseResponse = new BaseResponse();
				ResponseInfo responseInfo = new ResponseInfo();

				responseInfo.setCode("ERROR-004");
				responseInfo.setMessage(ex.getMessage());
				baseResponse.setStatus(HttpStatus.UNAUTHORIZED.value());

				printErrorResponse(response, baseResponse);
			} else {
				httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

				BaseResponse baseResponse = new BaseResponse();
				ResponseInfo responseInfo = new ResponseInfo();

				responseInfo.setCode("ERROR-000");
				responseInfo.setMessage(ex.getMessage());
				baseResponse.setStatus(httpServletResponse.getStatus());
				baseResponse.setResponseInfo(responseInfo);

				printErrorResponse(response, baseResponse);
			}
		}
	}

	private void printErrorResponse(ServletResponse response, BaseResponse baseResponse)
			throws IOException, JsonGenerationException, JsonMappingException {
		OutputStream out = response.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(out, baseResponse);
		out.flush();
	}

}
