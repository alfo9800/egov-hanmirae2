package edu.human.com.board.service;

public interface BoardService {
	public Integer delete_board(long nttId) throws Exception;
	public Integer delete_attach(String atchFileId) throws Exception;
}
