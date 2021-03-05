package edu.human.com.home.web;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springmodules.validation.commons.DefaultBeanValidator;

import edu.human.com.board.service.BoardService;
import edu.human.com.util.CommonUtil;
import egovframework.com.cmm.EgovMessageSource;
import egovframework.com.cmm.LoginVO;
import egovframework.com.cmm.service.EgovFileMngService;
import egovframework.com.cmm.service.EgovFileMngUtil;
import egovframework.com.cmm.service.FileVO;
import egovframework.com.cmm.util.EgovUserDetailsHelper;
import egovframework.let.cop.bbs.service.Board;
import egovframework.let.cop.bbs.service.BoardMaster;
import egovframework.let.cop.bbs.service.BoardMasterVO;
import egovframework.let.cop.bbs.service.BoardVO;
import egovframework.let.cop.bbs.service.EgovBBSAttributeManageService;
import egovframework.let.cop.bbs.service.EgovBBSManageService;
import egovframework.rte.fdl.property.EgovPropertyService;
import egovframework.rte.ptl.mvc.tags.ui.pagination.PaginationInfo;

@Controller
public class HomeController {
	@Autowired //자바8버전 나오기전 많이 사용 //외부에서 가져다 쓰는 건 Autowired로 지정함
	private EgovBBSAttributeManageService bbsAttrbService;
	
	@Autowired
	private EgovPropertyService propertyService;
	
	@Autowired
	private EgovBBSManageService bbsMngService;
	
	@Autowired
	private EgovMessageSource egovMessageSource;
	
	@Autowired
	private DefaultBeanValidator beanValidator;
	
	@Autowired
	private EgovFileMngUtil fileUtil;
	
	@Autowired
	private EgovFileMngService fileMngService;
	
	@Inject //직접 클래스를 만든 것은 Inject로 지정해서 사용함.
	private CommonUtil commUtil;
	
	@Inject
	private BoardService boardService;
	
	
	//==================================================
	@RequestMapping("/tiles/board/delete_board.do")
	public String delete_board(FileVO fileVO, BoardVO boardVO, RedirectAttributes rdat) throws Exception {
		if(boardVO.getAtchFileId()!=null && !"".equals(boardVO.getAtchFileId()) ) {
			System.out.println("디버그:첨부파일ID "+boardVO.getAtchFileId());
			//fileVO.setAtchFileId(boardVO.getAtchFileId());
			//fileMngService.deleteAllFileInf(fileVO);//USE_AT='N'삭제X
			//물리파일지우려면 2가지값 필수: file_stre_cours, stre_file_nm
			
			//실제 폴더에서 파일도 삭제 (1개-> 여러개 삭제하는 로직 변경)
			List<FileVO> fileList = fileMngService.selectFileInfs(fileVO);
			for(FileVO oneFileVO:fileList) {
				FileVO delfileVO = fileMngService.selectFileInf(oneFileVO);
				File target = new File(delfileVO.getFileStreCours(),delfileVO.getStreFileNm());
				if(target.exists()) {
					target.delete();//폴더에서 기존첨부파일 지우기
					System.out.println("디버그:첨부파일삭제OK");
				}
			}
						
			//첨부파일 레코드삭제
			boardService.delete_attach(boardVO.getAtchFileId());//게시물에 딸린 첨부파일테이블 2개 레코드삭제
		}
		//게시물 레코드삭제
		boardService.delete_board((int)boardVO.getNttId());
		rdat.addFlashAttribute("msg", "삭제");
		return "redirect:/tiles/board/list_board.do?bbsId="+boardVO.getBbsId();
	}
	
	@RequestMapping("/tiles/board/insert_board.do")
	public String insert_board(final MultipartHttpServletRequest multiRequest, @ModelAttribute("searchVO") BoardVO boardVO,
		    @ModelAttribute("bdMstr") BoardMaster bdMstr, @ModelAttribute("board") Board board, BindingResult bindingResult, SessionStatus status,
		    ModelMap model) throws Exception {
			// 사용자권한 처리
			if(!EgovUserDetailsHelper.isAuthenticated()) {
				model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
		    	return "login.tiles";
			}

			LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();
			Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();

			beanValidator.validate(board, bindingResult); 
			if (bindingResult.hasErrors()) { //만약 전송값이 문자인데 필드 값은 날짜 일 때 바인딩 에러가 발생 시 -> 아래 if문 실행

			    BoardMasterVO master = new BoardMasterVO();
			    BoardMasterVO vo = new BoardMasterVO();

			    vo.setBbsId(boardVO.getBbsId());
			    vo.setUniqId(user.getUniqId());

			    master = bbsAttrbService.selectBBSMasterInf(vo);

			    model.addAttribute("bdMstr", master);

			    //----------------------------
			    // 기본 BBS template 지정
			    //----------------------------
			    if (master.getTmplatCours() == null || master.getTmplatCours().equals("")) {
				master.setTmplatCours("/css/egovframework/cop/bbs/egovBaseTemplate.css");
			    }

			    model.addAttribute("brdMstrVO", master);
			    ////-----------------------------

			    return "board/insert_board.tiles";
			}

			if (isAuthenticated) {
			    List<FileVO> result = null;
			    String atchFileId = "";

			    final Map<String, MultipartFile> files = multiRequest.getFileMap();
			    if (!files.isEmpty()) {
				result = fileUtil.parseFileInf(files, "BBS_", 0, "", "");
				atchFileId = fileMngService.insertFileInfs(result);
			    }
			    board.setAtchFileId(atchFileId);
			    board.setFrstRegisterId(user.getUniqId());
			    board.setBbsId(board.getBbsId());

			    board.setNtcrNm("");	// dummy 오류 수정 (익명이 아닌 경우 validator 처리를 위해 dummy로 지정됨)
			    board.setPassword("");	// dummy 오류 수정 (익명이 아닌 경우 validator 처리를 위해 dummy로 지정됨)
			    //board.setNttCn(unscript(board.getNttCn()));	// XSS 방지

			    bbsMngService.insertBoardArticle(board);
			}
		
		return "redirect:/tiles/board/list_board.do?bbsId="+board.getBbsId();
	}
	
	@RequestMapping("/tiles/board/insert_board_form.do")
	public String insert_board_form(@ModelAttribute("searchVO") BoardVO boardVO, ModelMap model) throws Exception {
		// 사용자권한 처리: 로그인상태가 아니면 if문 안쪽 실행
		if(!EgovUserDetailsHelper.isAuthenticated()) {
			model.addAttribute("message", egovMessageSource.getMessage("fail.common.login"));
	    	return "login.tiles";
		}

	    LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();
		Boolean isAuthenticated = EgovUserDetailsHelper.isAuthenticated();

		BoardMasterVO bdMstr = new BoardMasterVO();

		if (isAuthenticated) {

		    BoardMasterVO vo = new BoardMasterVO();
		    vo.setBbsId(boardVO.getBbsId());
		    vo.setUniqId(user.getUniqId());

		    bdMstr = bbsAttrbService.selectBBSMasterInf(vo);
		    model.addAttribute("bdMstr", bdMstr);
		}

		//----------------------------
		// 기본 BBS template 지정
		//----------------------------
		if (bdMstr.getTmplatCours() == null || bdMstr.getTmplatCours().equals("")) {
		    bdMstr.setTmplatCours("/css/egovframework/cop/bbs/egovBaseTemplate.css");
		}

		model.addAttribute("brdMstrVO", bdMstr);
		////-----------------------------
		
		return "board/insert_board.tiles";
	}
	
	@RequestMapping("/tiles/board/view_board.do")
	public String view_board(@ModelAttribute("searchVO") BoardVO boardVO, ModelMap model) throws Exception {
		LoginVO user = new LoginVO();
	    if(EgovUserDetailsHelper.isAuthenticated()){
	    	user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();
		}

		// 조회수 증가 여부 지정
		boardVO.setPlusCount(true);

		//---------------------------------
		// 2009.06.29 : 2단계 기능 추가
		//---------------------------------
		if (!boardVO.getSubPageIndex().equals("")) {
		    boardVO.setPlusCount(false);
		}
		////-------------------------------

		boardVO.setLastUpdusrId(user.getUniqId());
		BoardVO vo = bbsMngService.selectBoardArticle(boardVO);
		//시큐어코딩 시작(게시물제목/내용에서 자바스크립트 코드의 꺽쇠태그를 특수문자로 바꿔서 실행하지 못하는 코드로 변경)
		//egov 저장할때, 시큐어코딩으로 저장하는 방식을 사용, 문제있음. 우리방식으로 적용
		String subject = commUtil.unscript(vo.getNttSj());//게시물제목
		String content = commUtil.unscript(vo.getNttCn());//게시물내용
		vo.setNttSj(subject);
		vo.setNttCn(content);
		model.addAttribute("result", vo);
		
		model.addAttribute("sessionUniqId", user.getUniqId());

		//----------------------------
		// template 처리 (기본 BBS template 지정  포함)
		//----------------------------
		BoardMasterVO master = new BoardMasterVO();

		master.setBbsId(boardVO.getBbsId());
		master.setUniqId(user.getUniqId());

		BoardMasterVO masterVo = bbsAttrbService.selectBBSMasterInf(master);

		if (masterVo.getTmplatCours() == null || masterVo.getTmplatCours().equals("")) {
		    masterVo.setTmplatCours("/css/egovframework/cop/bbs/egovBaseTemplate.css");
		}

		model.addAttribute("brdMstrVO", masterVo);
		
		return "board/view_board.tiles"; //.tiles로 리턴 받으면, 루트가 tiles폴더가 루트가 되고, view_board.jsp의 내용이 content에 나오게 됨.
	}
	
	@RequestMapping("/tiles/board/list_board.do")
	public String list_board(@ModelAttribute("searchVO") BoardVO boardVO, ModelMap model) throws Exception {
		LoginVO user = (LoginVO)EgovUserDetailsHelper.getAuthenticatedUser();

		boardVO.setBbsId(boardVO.getBbsId());
		boardVO.setBbsNm(boardVO.getBbsNm());

		BoardMasterVO vo = new BoardMasterVO();
		System.out.println("디버그: 게시판아이디는 "+boardVO.getBbsId());
		vo.setBbsId(boardVO.getBbsId());
		vo.setUniqId(user.getUniqId());

		BoardMasterVO master = bbsAttrbService.selectBBSMasterInf(vo);

		//-------------------------------
		// 방명록이면 방명록 URL로 forward
		//-------------------------------
		if (master.getBbsTyCode().equals("BBST04")) {
		    return "forward:/cop/bbs/selectGuestList.do";
		}
		////-----------------------------

		boardVO.setPageUnit(propertyService.getInt("pageUnit"));
		boardVO.setPageSize(propertyService.getInt("pageSize"));

		PaginationInfo paginationInfo = new PaginationInfo();

		paginationInfo.setCurrentPageNo(boardVO.getPageIndex());
		paginationInfo.setRecordCountPerPage(boardVO.getPageUnit());
		paginationInfo.setPageSize(boardVO.getPageSize());

		boardVO.setFirstIndex(paginationInfo.getFirstRecordIndex());
		boardVO.setLastIndex(paginationInfo.getLastRecordIndex());
		boardVO.setRecordCountPerPage(paginationInfo.getRecordCountPerPage());

		Map<String, Object> map = bbsMngService.selectBoardArticles(boardVO, vo.getBbsAttrbCode());
		int totCnt = Integer.parseInt((String)map.get("resultCnt"));

		paginationInfo.setTotalRecordCount(totCnt);

		//-------------------------------
		// 기본 BBS template 지정
		//-------------------------------
		if (master.getTmplatCours() == null || master.getTmplatCours().equals("")) {
		    master.setTmplatCours("/css/egovframework/cop/bbs/egovBaseTemplate.css");
		}
		////-----------------------------

		model.addAttribute("resultList", map.get("resultList"));
		model.addAttribute("resultCnt", map.get("resultCnt"));
		model.addAttribute("boardVO", boardVO);
		model.addAttribute("brdMstrVO", master);
		model.addAttribute("paginationInfo", paginationInfo);
		
		return "board/list_board.tiles";
	}
	
	@RequestMapping("/tiles/login.do")
	public String login() throws Exception {
		
		return "login.tiles";
	}
	
	@RequestMapping("/logout.do")
	public String logout() throws Exception {
		RequestContextHolder.getRequestAttributes().removeAttribute("LoginVO", RequestAttributes.SCOPE_SESSION);
		return "redirect:/";
	}
	
	@RequestMapping("/tiles/home.do")
	public String home() throws Exception {
		
		return "home.tiles";
	}
	
	
	//method, RequestMethod=GET[POST]없이 사용하면, 둘 다 허용이 되는 매핑이 됨.
}
