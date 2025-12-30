package com.gcc.library1.Controller;

import com.gcc.library1.Model.BorrowHistory;
import com.gcc.library1.Service.BorrowHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 借阅历史控制器类，用于处理借阅历史相关的HTTP请求
 */
@RestController
@RequestMapping("/api/borrowhistory")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BorrowHistoryController {

    private final BorrowHistoryService borrowHistoryService;

    /**
     * 根据书籍ID获取借阅历史记录
     * @param inputBorrowHistory 包含书籍ID的借阅历史对象
     * @return ResponseEntity 返回借阅历史列表或错误信息
     */
    @PostMapping("/getBorrowHistoryByBookId")
    public ResponseEntity<?> getBorrowHistoryByBookId(@RequestBody BorrowHistory inputBorrowHistory) {
        try {
            // 验证书籍ID是否为空
            Long bookId = inputBorrowHistory.getBookId();
            if (bookId == null) {
                return ResponseEntity.badRequest().body("书籍ID不能为空");
            }

            // 调用服务层方法查询借阅历史
            List<BorrowHistory> historyList = borrowHistoryService.getBorrowHistoryByBookId(bookId);
            return ResponseEntity.ok(historyList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询失败");
        }
    }




    /**
     * 根据用户ID获取借阅历史记录
     * @param inputBorrowHistory 包含用户ID的借阅历史对象
     * @return ResponseEntity 返回借阅历史列表或错误信息
     */
    @PostMapping("/getBorrowHistoryByUserId")
    public ResponseEntity<?> getBorrowHistoryByUserId(@RequestBody BorrowHistory inputBorrowHistory) {
        try {
            // 验证用户ID是否为空
            Long Userid = inputBorrowHistory.getUserId();
            if (Userid == null) {
                return ResponseEntity.badRequest().body("用户ID不能为空");
            }
            // 调用服务层方法查询借阅历史
            List<BorrowHistory> historyList = borrowHistoryService.getBorrowHistoryByUserId(Userid);
            return ResponseEntity.ok(historyList);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("查询失败");
        }
    }

}

