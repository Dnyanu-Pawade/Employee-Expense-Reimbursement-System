if (localStorage.getItem('token')) redirectByRole();
document.getElementById('login-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const btn = document.getElementById('login-btn');
  const errEl = document.getElementById('login-error');
  errEl.classList.add('d-none');
  btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Signing in...'; btn.disabled = true;
  try {
    const data = await api.login(document.getElementById('username').value, document.getElementById('password').value);
    if (data.token) {
      localStorage.setItem('token', data.token);
      localStorage.setItem('refreshToken', data.refreshToken);
      localStorage.setItem('user', JSON.stringify(data));
      redirectByRole(data.role);
    } else { errEl.textContent = data.message || 'Invalid credentials'; errEl.classList.remove('d-none'); }
  } catch(err) { errEl.textContent = err.message || 'Login failed'; errEl.classList.remove('d-none'); }
  finally { btn.innerHTML = '<i class="fas fa-sign-in-alt me-2"></i>Sign In'; btn.disabled = false; }
});
function redirectByRole(role) {
  const r = role || (JSON.parse(localStorage.getItem('user')||'{}').role);
  const map = { ROLE_ADMIN:'/html/admin-dashboard.html', ROLE_MANAGER:'/html/manager-dashboard.html', ROLE_TEAM_LEAD:'/html/teamlead-dashboard.html', ROLE_FINANCE:'/html/finance-dashboard.html', ROLE_AUDITOR:'/html/auditor-dashboard.html', ROLE_EMPLOYEE:'/html/employee-dashboard.html' };
  window.location.href = map[r] || '/html/employee-dashboard.html';
}
