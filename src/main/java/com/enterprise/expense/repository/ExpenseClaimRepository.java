package com.enterprise.expense.repository;
import com.enterprise.expense.entity.ExpenseClaim;
import com.enterprise.expense.entity.User;
import com.enterprise.expense.enums.ClaimStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, Long> {
    List<ExpenseClaim> findByEmployeeOrderByCreatedAtDesc(User employee);
    List<ExpenseClaim> findByStatusOrderByCreatedAtDesc(ClaimStatus status);
    List<ExpenseClaim> findByStatusInOrderByCreatedAtDesc(List<ClaimStatus> statuses);
    long countByStatus(ClaimStatus status);
    long countByEmployee(User employee);
    @Query("SELECT COALESCE(SUM(e.amount),0) FROM ExpenseClaim e WHERE e.status=:status")
    BigDecimal sumAmountByStatus(@Param("status") ClaimStatus status);
    @Query("SELECT COALESCE(SUM(e.amount),0) FROM ExpenseClaim e WHERE e.employee=:emp")
    BigDecimal sumAmountByEmployee(@Param("emp") User employee);
    @Query("SELECT e FROM ExpenseClaim e WHERE e.employee=:emp AND (:status IS NULL OR e.status=:status) AND (:category IS NULL OR CAST(e.category AS string)=:category) AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%',:keyword,'%'))) ORDER BY e.createdAt DESC")
    Page<ExpenseClaim> searchByEmployee(@Param("emp") User employee, @Param("status") ClaimStatus status, @Param("category") String category, @Param("keyword") String keyword, Pageable pageable);
    @Query("SELECT e FROM ExpenseClaim e WHERE (:status IS NULL OR e.status=:status) AND (:category IS NULL OR CAST(e.category AS string)=:category) AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND (:deptId IS NULL OR e.employee.department.id=:deptId) ORDER BY e.createdAt DESC")
    Page<ExpenseClaim> searchAll(@Param("status") ClaimStatus status, @Param("category") String category, @Param("keyword") String keyword, @Param("deptId") Long deptId, Pageable pageable);
    @Query("SELECT e FROM ExpenseClaim e WHERE e.status=:status AND e.createdAt < :deadline")
    List<ExpenseClaim> findOverdueClaims(@Param("status") ClaimStatus status, @Param("deadline") java.time.LocalDateTime deadline);
    @Query("SELECT COALESCE(SUM(e.amount),0) FROM ExpenseClaim e WHERE e.employee.department.id=:deptId AND e.status='PAYMENT_PROCESSED'")
    BigDecimal sumByDepartment(@Param("deptId") Long deptId);
    List<ExpenseClaim> findByEmployeeDepartmentIdAndStatus(Long deptId, ClaimStatus status);
    @Query("SELECT e.category, COUNT(e), SUM(e.amount) FROM ExpenseClaim e WHERE e.status='PAYMENT_PROCESSED' GROUP BY e.category")
    List<Object[]> getCategoryStats();
    @Query("SELECT MONTH(e.expenseDate), SUM(e.amount) FROM ExpenseClaim e WHERE e.status='PAYMENT_PROCESSED' AND YEAR(e.expenseDate)=:year GROUP BY MONTH(e.expenseDate)")
    List<Object[]> getMonthlyStats(@Param("year") int year);
}
