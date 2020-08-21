package com.cos.blog.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blog.dto.ReplySaveRequestDto;
import com.cos.blog.model.Board;
import com.cos.blog.model.Reply;
import com.cos.blog.model.User;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;
import com.cos.blog.repository.UserRepository;

// 스프링이 컴포넌트 스캔을 통해서 Bean에 등록을 해줌. IoC를 해준다.
@Service
public class BoardService {

	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private ReplyRepository replyRepository;
	
	@Transactional
	public void write(Board board, User user) {	// title, content
		board.setCount(0);
		board.setUser(user);
		boardRepository.save(board);
	}
	
	@Transactional(readOnly = true)
	public Page<Board> list(Pageable pageable){
		return boardRepository.findAll(pageable);
	}
	
	@Transactional(readOnly = true)
	public Board detail(int id) {
		return boardRepository.findById(id).orElseThrow( () -> {
			return new IllegalArgumentException("글 상세보기 실패 : 아이디를 찾을 수 없습니다.");
		});
	}
	
	@Transactional
	public void delete(int id) {
		boardRepository.deleteById(id);
	}
	 
	@Transactional
	public void update(int id, Board requestBoard) {
		Board board = boardRepository.findById(id).orElseThrow( () -> {
			return new IllegalArgumentException("글 찾기 실패 : 아이디를 찾을 수 없습니다.");
		});	// 영속화 완료
		board.setTitle(requestBoard.getTitle());
		board.setContent(requestBoard.getContent());
		
		// 해당 함수로 종료시에 (Service가 종료될때) 트랜잭션이 종료됩니다. 이때 더티체킹 - 자동 업데이트 db flush
	}
	
	@Transactional
	public void reply_write(ReplySaveRequestDto replySaveRequestDto) {

		replyRepository.mSave(replySaveRequestDto.getUserId(), replySaveRequestDto.getBoardId(), replySaveRequestDto.getContent());
		
	}
	
	@Transactional
	public void reply_delete(int replyId) {
		replyRepository.deleteById(replyId);
	}
}
 