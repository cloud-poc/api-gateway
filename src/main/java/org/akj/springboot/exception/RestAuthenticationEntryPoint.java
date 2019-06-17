package org.akj.springboot.exception;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akj.springboot.common.exception.BaseResponse;
import org.akj.springboot.common.exception.ResponseInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		if (authException.getCause() instanceof InvalidTokenException) {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.addHeader("Content-type", "application/json;charset=UTF-8");

			BaseResponse baseResponse = new BaseResponse();
			ResponseInfo responseInfo = new ResponseInfo();

			responseInfo.setCode("ERROR-004-001");
			responseInfo.setMessage("invalid token");
			baseResponse.setStatus(response.getStatus());
			baseResponse.setResponseInfo(responseInfo);
			
			printErrorResponse(response, baseResponse);
		} else {
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.addHeader("Content-type", "application/json;charset=UTF-8");

			BaseResponse baseResponse = new BaseResponse();
			ResponseInfo responseInfo = new ResponseInfo();

			responseInfo.setCode("ERROR-004");
			responseInfo.setMessage(authException.getMessage());
			baseResponse.setStatus(response.getStatus());
			baseResponse.setResponseInfo(responseInfo);

			printErrorResponse(response, baseResponse);
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
