package com.cos.blog.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

//@Getter
//@Setter
@Data						// getter, setter 같이
//@AllArgsConstructor		// 모든생성자
@NoArgsConstructor			// 빈 생성자
//@RequiredArgsConstructor	//final 붙은 애들만 생성자 만들어짐
public class Member {
	
	private int id;
	private String username;
	private String password;
	private String email;

	@Builder
	public Member(int id, String username, String password, String email) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	
}
