package com.gcc.library1.Repository;

import com.gcc.library1.Model.BorrowHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowHistoryRepository extends JpaRepository<BorrowHistory, Long> {
    List<BorrowHistory> findByUserId(Long userId);

    List<BorrowHistory> findByBookId(Long bookId);
}
