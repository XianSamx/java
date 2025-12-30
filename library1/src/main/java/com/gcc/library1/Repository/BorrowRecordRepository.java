package com.gcc.library1.Repository;

import com.gcc.library1.Model.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    Optional<BorrowRecord> findByBookId(Long bookId);
    // 文件：BorrowRecordRepository.java
    List<BorrowRecord> findByUserIdAndReturnDateIsNull(Long userId);
    Optional<List<BorrowRecord>> findByUserId(Long userId);
    // 只查询逾期且未归还的记录
    List<BorrowRecord> findByUserIdAndReturnDateBeforeAndReturnDateIsNotNull(Long userId, LocalDate date);

}