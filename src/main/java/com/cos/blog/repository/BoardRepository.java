package com.cos.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.blog.model.Board;

// DAO
// 자동으로 bean등록이 된다.
//@Repository	//생략가능하다.
public interface BoardRepository extends JpaRepository<Board, Integer>{
	
}
