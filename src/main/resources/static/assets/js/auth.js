function getJwtToken() {
    const token = sessionStorage.getItem('authToken');

    // 토큰이 없으면? 로그인 페이지로 쫓아냄
    if (!token) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login.html";
    }
}

function jwtLogout() {
    if (confirm('로그아웃 하시겠습니까?')) {
        sessionStorage.removeItem('authToken');
        window.location.href = '/login.html';
    }
}