function logout() {
    localStorage.removeItem('token');
    window.location.href = "/client/login.html";
}