package com.cos.blog.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.model.RoleType;
import com.cos.blog.model.User;
import com.cos.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌. IoC를 해준다.
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Transactional
	public void join(User user) {
		String rawPassword = user.getPassword();	//1234 원문
		String encPassword = encoder.encode(rawPassword);	//암호와 패스워드
		user.setPassword(encPassword);
		user.setRole(RoleType.USER);
		userRepository.save(user);
	}
	
	@Transactional
	public void update(User user) {
		// 수정시에는 JPA 영속성 컨텍스트 User 오브젝트를 영속화시키고, 영속화 된 User 오브젝트를 수정
		// select를 해서 User 오브젝트를 DB로 부터 가져오는 이유는 영속화를 하기 위해서!!
		// 영속화된 오브젝트를 변경하면 자동으로 DB에 update문을 날려주거든요.
		User persistance = userRepository.findById(user.getId()).orElseThrow( () -> {
			return new IllegalArgumentException("회원 찾기 실패");
		});
		
		// Validate 체크 => oauth 필드에 값이 없으면 수정 가능
		if(persistance.getOauth() == null || persistance.getOauth().equals("")) {
			String rawPassword = user.getPassword();
			String encPassword = encoder.encode(rawPassword);
			persistance.setPassword(encPassword);
			persistance.setEmail(user.getEmail());
		}
		// update함수 종료시 = 서비스 종료 = 트랜잭션 종료 = commit 이 자동으로 된다.
		// 영속화 된 persistance 객체의 변화가 감지되면 더티체킹이 되어 update문을 날려줌.
	}
	
	@Transactional(readOnly = true)	//Select 할 때 트랜잭션 시작, 서비스 종료시에 트랜잭션 종료(정합성)
	public String loginFailed(String username, String password) {
		if(userRepository.findByUsername(username).orElse(null) == null) {
			return "username";
		}else if(userRepository.findByUsernameAndPassword(username, password).orElse(null) == null) {
			return "password";
		}
		return "username";
	}
	
	@Transactional(readOnly = true)
	public User find(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}
}
 