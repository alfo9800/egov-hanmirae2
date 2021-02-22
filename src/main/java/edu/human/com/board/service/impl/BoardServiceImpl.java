package edu.human.com.board.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import edu.human.com.board.service.BoardService;

@Service
public class BoardServiceImpl implements BoardService {
	
	@Inject
	private BoardDAO boardDAO; //같은 패키지 안에 있어서 import필요 없음

	@Override
	public Integer delete_board(long nttId) throws Exception {
		//DAO호출
		return boardDAO.delete_board(nttId);
	}
	
	
}
