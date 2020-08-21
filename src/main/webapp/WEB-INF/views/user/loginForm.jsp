<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include file="../layout/header.jsp"%>

<c:if test="${loginFailed eq 'username'}">
	<script>
		alert('존재하지 않는 아이디입니다.');
		history.back();
	</script>
</c:if>
<c:if test="${loginFailed eq 'password'}">
	<script>
		alert('비밀번호를 확인해주세요.');
		history.back();
	</script>
</c:if>
<div class="container">
	<form action="/auth/loginProc" method="post">
		<div class="form-group">
			<label for="username">Username</label> 
			<input name="username" type="text" class="form-control" placeholder="Enter username" id="username" required="required">
		</div>
	
		<div class="form-group">
			<label for="password">Password</label> 
			<input name="password" type="password" class="form-control" placeholder="Enter password" id="password" required="required">
		</div>
		
		<div class="form-group form-check">
			<label class="form-check-label"> 
			<input name="remember" class="form-check-input" type="checkbox"> Remember me
			</label>
		</div>
		
		<button id="btn-login" class="btn btn-primary">로그인</button>
		<a href="https://kauth.kakao.com/oauth/authorize?client_id=61e32ad3b25eee38c08f45e1a9f150c1&redirect_uri=http://localhost:8000/auth/kakao/callback&response_type=code">
		<img height="38" src="/image/kakao_login_button.png"></a>
		
		<!-- csrf 토큰 -->
		<sec:csrfInput/>
	</form>
	
	
</div>
<!-- <script src="/js/user.js"></script> -->
<%@ include file="../layout/footer.jsp"%>