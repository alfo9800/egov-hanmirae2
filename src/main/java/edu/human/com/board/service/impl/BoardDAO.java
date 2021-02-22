package edu.human.com.board.service.impl;

import org.springframework.stereotype.Repository;

import edu.human.com.common.EgovComAbstractMapper;

@Repository
public class BoardDAO extends EgovComAbstractMapper {
	public Integer delete_board(long nttId) throws Exception {
		//egov매퍼 추상 클래스 사용 : sqlSession템플릿 직접 접근하지 않고 사용
		return delete("boardMapper.delete_board",nttId);
	}
}
