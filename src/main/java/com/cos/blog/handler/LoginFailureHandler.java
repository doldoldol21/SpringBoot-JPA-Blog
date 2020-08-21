package com.cos.blog.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blog.service.UserService;

@RestController
public class LoginFailureHandler implements AuthenticationFailureHandler{

	@Autowired
	private UserService userService;
	
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String result = userService.loginFailed(request.getParameter("username"), request.getParameter("password"));
		request.setAttribute("loginFailed", result);
		request.getRequestDispatcher("/auth/loginForm").forward(request, response);	//POST방식으로간다.
	}
	
}
