package com.enterprise.expense.repository;
import com.enterprise.expense.entity.ApprovalHistory;
import com.enterprise.expense.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ApprovalHistoryRepository extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByClaimOrderByActionAtDesc(ExpenseClaim claim);
    List<ApprovalHistory> findByApproverId(Long approverId);
}
