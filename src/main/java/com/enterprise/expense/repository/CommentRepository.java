package com.enterprise.expense.repository;
import com.enterprise.expense.entity.Comment;
import com.enterprise.expense.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByClaimOrderByCreatedAtAsc(ExpenseClaim claim);
}
