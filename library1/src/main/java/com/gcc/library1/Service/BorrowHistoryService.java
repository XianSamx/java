package com.gcc.library1.Service;

import com.gcc.library1.Model.BorrowHistory;
import com.gcc.library1.Repository.BorrowHistoryRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowHistoryService {
    private final BorrowHistoryRepository BorrowHistoryRepository;

    public BorrowHistory addBorrowHistory(Long bookId, Long userId, String behaviour) {
        BorrowHistory borrowHistory = new BorrowHistory();
        borrowHistory.setBookId(bookId);
        borrowHistory.setUserId(userId);
        borrowHistory.setDate(LocalDate.now());
        borrowHistory.setBehavour(behaviour);
        return BorrowHistoryRepository.save(borrowHistory);
    }
public List<BorrowHistory> getBorrowHistoryByUserId(Long userId) {
    if (userId == null) {
        throw new IllegalArgumentException("用户ID不能为空");
    }
    List<BorrowHistory> result = BorrowHistoryRepository.findByUserId(userId);
    if (result.isEmpty()) {
        throw new EntityNotFoundException("未找到用户ID为 " + userId + " 的借阅历史记录");
    }
    return result;
}

public List<BorrowHistory> getBorrowHistoryByBookId(Long bookId) {
    if (bookId == null) {
        throw new IllegalArgumentException("书籍ID不能为空");
    }
    List<BorrowHistory> result = BorrowHistoryRepository.findByBookId(bookId);
    if (result.isEmpty()) {
        throw new EntityNotFoundException("未找到书籍ID为 " + bookId + " 的借阅历史记录");
    }
    return result;
}

}
