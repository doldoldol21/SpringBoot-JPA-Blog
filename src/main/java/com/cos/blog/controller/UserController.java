package com.cos.blog.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.cos.blog.model.KakaoProfile;
import com.cos.blog.model.OAuthToken;
import com.cos.blog.model.User;
import com.cos.blog.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

//인증이 안된 사용자들이 출입할 수 있는 경로를 /auth/** 허용
// 그냥 주소가 / 이면 index.jsp 허용
// static이하에 있는 /js/**, /css/**, /image/**

@Controller
public class UserController {

	@Value(value = "${haeje.key}")
	private String key;
	
	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@GetMapping("/auth/joinForm")
	public String joinForm() {
		return "user/joinForm";
	}

	@GetMapping("/auth/loginForm")
	public String loginFormGet() {
		return "user/loginForm";
	}

	@PostMapping("/auth/loginForm") // POST(로그인실패시 쓰고있따.)
	public String loginFormPost() {
		return "user/loginForm";
	}

	@GetMapping("/user/updateForm")
	public String updateForm() {
		return "user/updateForm";
	}

	// 카카오
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) { // @ResponseBody를 붙이면 Data를 리턴해주는 컨트롤러 함수

		// POST방식으로 key=value 데이터를 요청 (카카오쪽으로)
		// Retrofit2
		// OkHttp
		// RestTemplate

		RestTemplate rt = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "61e32ad3b25eee38c08f45e1a9f150c1");
		params.add("redirect_uri", "http://localhost:8000/auth/kakao/callback");
		params.add("code", code);

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoToKenRequest = new HttpEntity<>(params, headers);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음
		ResponseEntity<String> response = rt.exchange("https://kauth.kakao.com/oauth/token", HttpMethod.POST,
				kakaoToKenRequest, String.class);

		// Gson, Json, Simple, ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();

		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		RestTemplate rt2 = new RestTemplate();

		// HttpHeader 오브젝트 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoToKenRequest2 = new HttpEntity<>(headers2);

		// Http 요청하기 - Post방식으로 - 그리고 response 변수의 응답 받음
		ResponseEntity<String> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
				kakaoToKenRequest2, String.class);

		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;

		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		System.out.println(kakaoProfile.getId());
//		System.out.println(kakaoProfile.getKakao_account().getEmail());
//		System.out.println("블로그 아이디 : " + kakaoProfile.getKakao_account().getEmail() + "-" + kakaoProfile.getId());
//		System.out.println("블로그 이메일 : " + kakaoProfile.getKakao_account().getEmail());
		// UUID란 -> 중복되지 않는 어떤 특정 값을 만들어내는 알고리즘
//		UUID garbagePassword = UUID.randomUUID();
//		System.out.println("비밀번호 : " + key);

		User kakaoUser = User.builder()
				.username(kakaoProfile.getKakao_account().getEmail() + "-" + kakaoProfile.getId())
				.password(key)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();

		// 가입자 혹은 비가입자 체크 처리
		User originUser = userService.find(kakaoUser.getUsername());
		
		if (originUser == null) {
			System.out.println("기존회원이 아니라 자동 회원가입");
			userService.join(kakaoUser);
		}
		

		// 세션 등록
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), key));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		return "redirect:/";
	}
}
