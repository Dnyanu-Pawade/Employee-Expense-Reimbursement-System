package com.enterprise.expense.repository;
import com.enterprise.expense.entity.ClaimDocument;
import com.enterprise.expense.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ClaimDocumentRepository extends JpaRepository<ClaimDocument, Long> {
    List<ClaimDocument> findByClaim(ExpenseClaim claim);
}
