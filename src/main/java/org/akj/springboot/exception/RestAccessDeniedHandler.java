package org.akj.springboot.exception;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akj.springboot.common.exception.BaseResponse;
import org.akj.springboot.common.exception.ResponseInfo;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.addHeader("Content-type", "application/json;charset=UTF-8");
		
		BaseResponse baseResponse = new BaseResponse();
		ResponseInfo responseInfo = new ResponseInfo();
		
		responseInfo.setCode("ERROR-004");
		responseInfo.setMessage(accessDeniedException.getMessage());
		baseResponse.setStatus(response.getStatus());
		baseResponse.setResponseInfo(responseInfo);
		
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, baseResponse);
        out.flush();
	}

}
