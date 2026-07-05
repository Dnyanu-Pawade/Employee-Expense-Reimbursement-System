package com.enterprise.expense.repository;
import com.enterprise.expense.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByClaimId(Long claimId);
    @Query("SELECT COALESCE(SUM(p.amount),0) FROM Payment p")
    BigDecimal totalPaid();
    @Query("SELECT MONTH(p.processedAt), SUM(p.amount) FROM Payment p WHERE YEAR(p.processedAt)=:year GROUP BY MONTH(p.processedAt)")
    List<Object[]> monthlyPayments(int year);
}
