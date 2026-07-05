const BASE_URL = '';
const api = {
  getToken() { return localStorage.getItem('token'); },
  getUser() { return JSON.parse(localStorage.getItem('user') || 'null'); },
  getRefreshToken() { return localStorage.getItem('refreshToken'); },
  headers() { return { 'Content-Type': 'application/json', 'Authorization': `Bearer ${this.getToken()}` }; },
  async request(method, path, body = null, silent = false) {
    const opts = { method, headers: this.headers() };
    if (body) opts.body = JSON.stringify(body);
    let res;
    try { res = await fetch(BASE_URL + path, opts); } catch(e) { if (!silent) throw new Error('Network error'); return null; }
    if (res.status === 401) {
      if (this.getRefreshToken()) {
        const ok = await this.tryRefresh();
        if (ok) { opts.headers = this.headers(); res = await fetch(BASE_URL + path, opts); }
        else { if (!silent) { localStorage.clear(); window.location.href = '/index.html'; } return null; }
      } else { if (!silent) { localStorage.clear(); window.location.href = '/index.html'; } return null; }
    }
    if (res.status === 403) { if (!silent) throw new Error('Access denied'); return null; }
    if (res.status === 204) return null;
    const data = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(data.message || 'Request failed');
    return data;
  },
  async tryRefresh() {
    try {
      const res = await fetch('/api/auth/refresh-token', { method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify({refreshToken: this.getRefreshToken()}) });
      if (!res.ok) return false;
      const d = await res.json();
      localStorage.setItem('token', d.token);
      if (d.refreshToken) localStorage.setItem('refreshToken', d.refreshToken);
      return true;
    } catch { return false; }
  },
  get(path, silent=false) { return this.request('GET', path, null, silent); },
  post(path, body) { return this.request('POST', path, body); },
  put(path, body) { return this.request('PUT', path, body); },
  patch(path, body) { return this.request('PATCH', path, body); },
  delete(path) { return this.request('DELETE', path); },
  login(u, p) { return fetch('/api/auth/login',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify({username:u,password:p})}).then(r=>r.json()); },
  // Auth
  forgotPassword(email) { return this.post('/api/auth/forgot-password',{email}); },
  resetPassword(token,newPassword) { return this.post('/api/auth/reset-password',{token,newPassword}); },
  // Expenses
  submitExpense(data) { return this.post('/api/expenses',data); },
  getMyExpenses() { return this.get('/api/expenses'); },
  getExpenseById(id) { return this.get(`/api/expenses/${id}`); },
  searchExpenses(p) { return this.get(`/api/expenses/search?${new URLSearchParams(p)}`); },
  // Team Lead
  getTeamLeadPending() { return this.get('/api/team-lead/pending'); },
  teamLeadApprove(id,comment) { return this.put(`/api/team-lead/approve/${id}`,{comment}); },
  teamLeadReject(id,comment) { return this.put(`/api/team-lead/reject/${id}`,{comment}); },
  // Manager
  getManagerPending() { return this.get('/api/manager/pending'); },
  managerApprove(id,comment) { return this.put(`/api/manager/approve/${id}`,{comment}); },
  managerReject(id,comment) { return this.put(`/api/manager/reject/${id}`,{comment}); },
  // Finance
  getFinancePending() { return this.get('/api/finance/pending'); },
  getFinanceVerified() { return this.get('/api/finance/verified'); },
  financeVerify(id,comment) { return this.put(`/api/finance/verify/${id}`,{comment}); },
  financeReject(id,comment) { return this.put(`/api/finance/reject/${id}`,{comment}); },
  processPayment(id,data) { return this.post(`/api/finance/payment/${id}`,data); },
  getPayments() { return this.get('/api/finance/payments'); },
  // Admin
  getDashboard() { return this.get('/api/admin/dashboard'); },
  getAllUsers() { return this.get('/api/admin/users'); },
  updateUserRole(id,role) { return this.patch(`/api/admin/users/${id}/role?role=${role}`); },
  toggleUserStatus(id) { return this.patch(`/api/admin/users/${id}/toggle`); },
  assignManager(id,managerId) { return this.patch(`/api/admin/users/${id}/manager?managerId=${managerId}`); },
  getAllClaims() { return this.get('/api/admin/claims'); },
  searchAllClaims(p) { return this.get(`/api/admin/claims/search?${new URLSearchParams(p)}`); },
  getDepartments() { return this.get('/api/admin/departments'); },
  createDepartment(data) { return this.post('/api/admin/departments',data); },
  getAuditLogs(limit=50) { return this.get(`/api/admin/audit-logs?limit=${limit}`,true); },
  getCategoryChart() { return this.get('/api/admin/charts/category',true); },
  getStatusChart() { return this.get('/api/admin/charts/status',true); },
  getMonthlyChart() { return this.get('/api/admin/charts/monthly',true); },
  // Notifications
  getNotifications() { return this.get('/api/notifications',true); },
  getUnreadCount() { return this.get('/api/notifications/unread-count',true); },
  markNotificationsRead() { return this.patch('/api/notifications/mark-read'); },
  // Comments
  getComments(claimId) { return this.get(`/api/comments/${claimId}`); },
  addComment(claimId,content) { return this.post(`/api/comments/${claimId}`,{content}); },
  // Profile
  getProfile() { return this.get('/api/users/me'); },
  updateProfile(data) { return this.put('/api/users/me',data); },
  // Export
  async downloadFile(path, filename) {
    const res = await fetch(BASE_URL+path,{headers:{'Authorization':`Bearer ${this.getToken()}`}});
    if (!res.ok) throw new Error('Download failed');
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href=url; a.download=filename; a.click(); URL.revokeObjectURL(url);
  },
  downloadMyExcel() { return this.downloadFile('/api/export/my-claims/excel','my-claims.xlsx'); },
  downloadAllExcel() { return this.downloadFile('/api/export/all-claims/excel','all-claims.xlsx'); },
  downloadPdf(id,num) { return this.downloadFile(`/api/export/claim/${id}/pdf`,`claim-${num}.pdf`); },
};
