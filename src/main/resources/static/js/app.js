if (!localStorage.getItem('token')) window.location.href = '/index.html';
const currentUser = api.getUser();
document.addEventListener('DOMContentLoaded', () => {
  const unEl = document.getElementById('sidebar-username');
  const roleEl = document.getElementById('sidebar-role');
  const deptEl = document.getElementById('sidebar-dept');
  if (unEl && currentUser) unEl.textContent = currentUser.fullName || currentUser.username;
  if (roleEl && currentUser) roleEl.textContent = (currentUser.role||'').replace('ROLE_','');
  if (deptEl && currentUser) deptEl.textContent = currentUser.department || '';
  const logoutBtn = document.getElementById('logout-btn');
  if (logoutBtn) logoutBtn.addEventListener('click', () => { localStorage.clear(); window.location.href = '/index.html'; });
  api.getUnreadCount().then(d => {
    const badge = document.getElementById('notif-badge');
    if (badge && d && d.count > 0) { badge.textContent = d.count; badge.classList.remove('d-none'); }
  }).catch(()=>{});
});
function formatCurrency(v) { return '₹' + parseFloat(v||0).toFixed(2); }
function formatDate(d) { if (!d) return '-'; return new Date(d).toLocaleDateString('en-IN'); }
function formatDateTime(d) { if (!d) return '-'; return new Date(d).toLocaleString('en-IN'); }
function statusBadge(s) {
  const colors = {SUBMITTED:'primary',TEAM_LEAD_APPROVED:'info',TEAM_LEAD_REJECTED:'danger',MANAGER_APPROVED:'purple',MANAGER_REJECTED:'danger',FINANCE_VERIFIED:'success',FINANCE_REJECTED:'danger',PAYMENT_PROCESSED:'success',ESCALATED:'warning',CANCELLED:'secondary',DRAFT:'secondary'};
  return `<span class="badge bg-${colors[s]||'secondary'}">${s.replace(/_/g,' ')}</span>`;
}
function showToast(msg, type='success') {
  const t = document.createElement('div');
  t.className = `toast-msg toast-${type}`; t.textContent = msg;
  document.body.appendChild(t);
  setTimeout(() => t.remove(), 3000);
}
