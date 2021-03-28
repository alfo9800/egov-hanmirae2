<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/header.jsp" %>

<!-- 대시보드 본문 Content Wrapper. Contains page content -->
  <div class="content-wrapper">
    <!-- 본문헤더 Content Header (Page header) -->
    <div class="content-header">
      <div class="container-fluid">
        <div class="row mb-2">
          <div class="col-sm-6">
            <h1 class="m-0">
           		 화면권한리스트
            </h1>
          </div><!-- /.col -->
          <div class="col-sm-6">
            <ol class="breadcrumb float-sm-right">
              <li class="breadcrumb-item"><a href="#">Home</a></li>
              <li class="breadcrumb-item active">화면권한리스트</li>
            </ol>
          </div><!-- /.col -->
        </div><!-- /.row -->
        <div style="font-size:12px;color:red;">
        	주) 추가/수정/삭제 시 톰캣을 restart해야 하지만, 권한 적용 됩니다.
        </div>
      </div><!-- /.container-fluid -->
    </div>
    <!-- /.content-header -->

    <!-- 본문내용 Main content -->
    <section class="content">
      <div class="container-fluid">

        <div class="row"><!-- 부트스트랩의 디자인 클래스 row -->
          <div class="col-12"><!-- 그리드시스템중 12가로칼럼 width:100% -->
            <div class="card"><!-- 부트스트랩의 카드 클래스:네모난 디자인 -->
              <div class="card-header">
                <h3 class="card-title">권한 검색</h3>

                <div class="card-tools">

                  <form name="search_form" action="<c:url value='/' />admin/authorrole/list_author.do" method="get">
                  <div class="input-group input-group-sm">
                    <!-- 부트스트랩 템플릿만으로는 디자인처리가 부족한 경우가 있기 때문에 종종 인라인 스타일 사용 -->
                    <div>
                        <select name="search_type" class="form-control">
                            <option value="all" selected>-전체-</option>
                            <option value="author_code" data-select2-id="8">권한 이름</option>
                            <option value="role_pttrn" data-select2-id="16">화면 경로</option>
                        </select>
                    </div>
                    <div>
                    <input type="text" name="search_keyword" class="form-control float-right" placeholder="Search">
					</div>
                    <div class="input-group-append">
                      <button type="submit" class="btn btn-default">
                        <i class="fas fa-search"></i>
                      </button>
                    </div>
                  </div>
                  </form>

                </div>
              </div>
              <!-- /.card-header -->
              <div class="card-body table-responsive p-0">
                <table class="table table-hover text-nowrap">
                  <thead>
                    <tr>
                      <th>ROLE_PTTRN</th><!-- 테이블 헤드 타이틀태그th -->
                      <th>AUTHOR_CODE</th>
                      <th>AUTHORROLE_DC</th>
                      <th>SORT_ORDR</th>
                      <th>USE_AT</th>
                    </tr>
                  </thead>
                  <tbody>
                  <c:forEach items="${authorRoleList}" var="vo">
                    <tr>
                      <td>
	                      <a href="<c:url value='/admin/authorrole/view_authorrole.do?authorrole_id=${vo.AUTHORROLE_ID}&page=${pageVO.page}&search_type=${pageVO.search_type}&search_keyword=${search_keyword}' />">
	                      ${vo.ROLE_PTTRN}
	                      </a>
                      </td>
                      <!-- 위에 a링크값은 리스트가 늘어날 수록 동적으로 user_id값이 변하게 됩니다. 개발자가 jsp처리 -->
                      <td>${vo.AUTHOR_CODE}</td>
                      <td>${vo.AUTHORROLE_DC}</td>
                      <td>${vo.SORT_ORDR}</td>
                      <td>${vo.USE_AT}</td>
                    </tr>
                  </c:forEach>
                  </tbody>
                </table>
              </div>
              <!-- /.card-body -->

            </div>
            <!-- /.card -->

            <!-- 버튼영역 시작 -->
              <div class="card-body">
              	<a href="<c:url value='/admin/authorrole/insert_author.do' />" class="btn btn-primary float-right">등록</a>
              </div>
            <!-- 버튼영역 끝 -->

            <!-- 페이징처리 시작 -->
            <div class="pagination justify-content-center">
            	<ul class="pagination">
            	 <c:if test="${pageVO.prev}">
	            	 <li class="paginate_button page-item previous" id="example2_previous">
	            	 <a href="<c:url value='/' />admin/authorrole/list_author.do?page=${pageVO.startPage-1}&search_type=${pageVO.search_type}&search_keyword=${pageVO.search_keyword}" aria-controls="example2" data-dt-idx="0" tabindex="0" class="page-link">Previous</a>
	            	 </li>
	            	 <!-- 위 이전게시물링크 -->
            	 </c:if>

            	 <!-- jstl for문이고, 향상된 for문이아닌 고전for문으로 시작값, 종료값 var변수idx는 인덱스값이 저장되어 있습니다. -->
            	 <c:forEach begin="${pageVO.startPage}" end="${pageVO.endPage}" var="idx">
            	 	<li class='paginate_button page-item <c:out value="${idx==pageVO.page?'active':''}" />'>
            	 	<a href="<c:url value='/' />admin/authorrole/list_author.do?page=${idx}&search_type=${pageVO.search_type}&search_keyword=${pageVO.search_keyword}" aria-controls="example2" data-dt-idx="1" tabindex="0" class="page-link">${idx}</a></li>
            	 </c:forEach>

            	 <c:if test="${pageVO.next}">
	            	 <!-- 아래 다음게시물링크 -->
	            	 <li class="paginate_button page-item next" id="example2_next">
	            	 <a href="<c:url value='/' />admin/authorrole/list_author.do?page=${pageVO.endPage+1}&search_type=${pageVO.search_type}&search_keyword=${pageVO.search_keyword}" aria-controls="example2" data-dt-idx="7" tabindex="0" class="page-link">Next</a>
	            	 </li>
            	 </c:if>
            	 </ul>
            </div>
	  		<!-- 페이징처리 끝 -->       

          </div>
        </div>

      </div><!-- /.container-fluid -->
    </section>
    <!-- /.content -->
  </div>
  <!-- /.content-wrapper -->

<%@ include file="../include/footer.jsp" %>