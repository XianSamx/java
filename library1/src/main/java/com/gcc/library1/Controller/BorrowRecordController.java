package com.gcc.library1.Controller;

import com.gcc.library1.Model.BorrowRecord;
import com.gcc.library1.Service.BookService;
import com.gcc.library1.Service.BorrowHistoryService;
import com.gcc.library1.Service.BorrowRecordService;
import com.gcc.library1.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 借阅记录控制器类，用于处理图书借阅、续借和归还相关的HTTP请求。
 */
@RestController
@RequestMapping("/api/borrow")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class BorrowRecordController {

    private final BorrowRecordService borrowService;
    private final BookService bookService;
    private final UserService userService;
    private final BorrowHistoryService borrowHistoryService;

    /**
     * 添加一条新的借阅记录（即借书操作）。
     *
     * @param inputBorrowRecord 包含bookId和userId的借阅信息对象
     * @return ResponseEntity<?> 返回操作结果状态及消息：
     *         - 成功时返回HttpStatus.OK，并附带成功信息；
     *         - 参数缺失时返回HttpStatus.BAD_REQUEST；
     *         - 用户或书籍不存在时返回HttpStatus.NOT_FOUND；
     *         - 书籍已被借出时返回HttpStatus.CONFLICT。
     */
    @PostMapping("/add")
    public ResponseEntity<?> registerBorrow(@RequestBody BorrowRecord inputBorrowRecord) {
        Long bookId = inputBorrowRecord.getBookId();
        Long userId = inputBorrowRecord.getUserId();

        // 参数基础校验
        if (bookId == null || userId == null) {
            return new ResponseEntity<>("bookId或者userId参数缺失", HttpStatus.BAD_REQUEST);
        }

        // 用户存在性检查
        try {
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(userId + "，借书失败，用户不存在", HttpStatus.NOT_FOUND);
        }

        // 图书存在性检查
        try {
            bookService.getBookById(bookId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(bookId + "，已借失败，该书不存在", HttpStatus.NOT_FOUND);
        }

        // 判断图书是否已经被借出
        try {
            borrowService.getBorrowBookByBookId(bookId);
            return new ResponseEntity<>(bookId + "，已借失败，该书已被借", HttpStatus.CONFLICT); // 更准确的状态码
        } catch (EntityNotFoundException e) {
            // 如果没找到，则说明尚未借出，可以继续执行借阅逻辑
            LocalDate borrowDate = LocalDate.now();
            LocalDate returnDate = borrowDate.plusDays(90); // TODO: 后续可通过配置获取天数

            borrowService.addBorrow(bookId, userId, borrowDate, returnDate);
            borrowHistoryService.addBorrowHistory(bookId, userId, "借书90天");
            return new ResponseEntity<>(userId + "," + bookId + "," + "已借成功", HttpStatus.OK);
        }
    }

    /**
     * 更新借阅记录中的归还时间（即续借操作）。
     *
     * @param inputBorrowRecord 包含bookId和userId的对象，用于确认身份并进行续借
     * @return ResponseEntity<?> 返回操作结果状态及消息：
     *         - 成功时返回HttpStatus.OK，并附带新归还日期；
     *         - 找不到对应借阅记录时返回HttpStatus.NOT_FOUND；
     *         - 用户ID与原借阅人不符时返回HttpStatus.NOT_FOUND；
     *         - 出现异常时返回HttpStatus.INTERNAL_SERVER_ERROR。
     */
    @PostMapping("/updateBorrow")
    public ResponseEntity<?> updateBorrow(@RequestBody BorrowRecord inputBorrowRecord) {
        Long bookId = inputBorrowRecord.getBookId();
        Long userId = inputBorrowRecord.getUserId();

        try {
            BorrowRecord borrowRecord = borrowService.getBorrowBookByBookId(bookId);

            if (borrowRecord == null) {
                return new ResponseEntity<>(bookId + "，更新失败，请检查书籍id是否正确", HttpStatus.NOT_FOUND);
            }

            LocalDate returnDate = borrowRecord.getReturnDate().plusDays(90);
            if (Objects.equals(borrowRecord.getUserId(), userId)) {
                borrowService.updateBorrow(bookId, borrowRecord.getUserId(), returnDate);
                borrowHistoryService.addBorrowHistory(bookId, userId, "续借90天");
                return new ResponseEntity<>(bookId+"归还日期已更改为"+returnDate, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(userId + "，更新失败，请检查用户id是否正确", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("系统异常：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 删除指定书籍的借阅记录（即还书操作）。
     *
     * @param inputBorrowRecord 包含bookId的借阅信息对象
     * @return ResponseEntity<?> 返回操作结果状态及消息：
     *         - 成功时返回HttpStatus.OK，并提示“已还成功”；
     *         - 请求体为空或书籍ID为空时返回HttpStatus.BAD_REQUEST；
     *         - 借阅记录未找到时返回HttpStatus.NOT_FOUND；
     *         - 其他异常情况返回HttpStatus.INTERNAL_SERVER_ERROR。
     */
    @DeleteMapping("/back")
    public ResponseEntity<?> BackBook(@RequestBody BorrowRecord inputBorrowRecord) {
        // 输入验证

        if (inputBorrowRecord == null) {
            return new ResponseEntity<>("请求参数不能为空", HttpStatus.BAD_REQUEST);
        }

        Long bookId = inputBorrowRecord.getBookId();

        if (bookId == null) {
            return new ResponseEntity<>("书籍ID不能为空", HttpStatus.BAD_REQUEST);
        }
        try {
            BorrowRecord borrowRecord = borrowService.getBorrowBookByBookId(bookId);
            Long userId = borrowRecord.getUserId();
            borrowService.deleteBorrow(bookId);
            borrowHistoryService.addBorrowHistory(bookId, userId, "还书");
            return new ResponseEntity<>(bookId + "已还成功", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // 捕获其他可能的异常，避免500错误
            return new ResponseEntity<>("还书失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 根据用户ID查询其所有的借阅记录。
     *
     * @param inputBorrowRecord 包含userId的请求体数据
     * @return ResponseEntity<?> 返回操作结果状态及消息：
     *         - 成功时返回HttpStatus.OK，并携带借阅记录列表；
     *         - 用户ID为空时返回HttpStatus.BAD_REQUEST；
     *         - 用户不存在时返回HttpStatus.NOT_FOUND；
     *         - 查询过程中发生异常则返回HttpStatus.INTERNAL_SERVER_ERROR。
     */
    @PostMapping("/user")
    public ResponseEntity<?> getBorrowRecordsByUserId(@RequestBody BorrowRecord inputBorrowRecord) {
        Long userId = inputBorrowRecord.getUserId();
        if (userId == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            // 验证用户是否存在
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            // 获取用户所有借阅记录
            List<BorrowRecord> borrowRecords = borrowService.getBorrowBooksByUserId(userId);
            return new ResponseEntity<>(borrowRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
/**
 * 根据用户ID查询其逾期未还的借阅记录
 *
 * @param inputBorrowRecord 包含userId的请求体数据
 * @return ResponseEntity<?> 返回操作结果状态及消息：
 *         - 成功时返回HttpStatus.OK，并携带逾期借阅记录列表；
 *         - 用户ID为空时返回HttpStatus.BAD_REQUEST；
 *         - 用户不存在时返回HttpStatus.NOT_FOUND；
 *         - 查询过程中发生异常则返回HttpStatus.INTERNAL_SERVER_ERROR。
 */
/**
 * 根据用户ID查询其逾期未还的借阅记录
 *
 * @param inputBorrowRecord 包含userId的请求体数据
 * @return ResponseEntity<?> 返回操作结果状态及消息：
 *         - 成功时返回HttpStatus.OK，并携带逾期借阅记录列表；
 *         - 用户ID为空时返回HttpStatus.BAD_REQUEST；
 *         - 用户不存在时返回HttpStatus.NOT_FOUND；
 *         - 查询过程中发生异常则返回HttpStatus.INTERNAL_SERVER_ERROR。
 */
    @PostMapping("/overdue")
    public ResponseEntity<?> getOverdueBorrowRecordsByUserId(@RequestBody BorrowRecord inputBorrowRecord) {
        Long userId = inputBorrowRecord.getUserId();
        if (userId == null) {
            return new ResponseEntity<>("用户ID不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            // 验证用户是否存在
            userService.getUserById(userId);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("用户不存在", HttpStatus.NOT_FOUND);
        }

        try {
            // 获取用户所有逾期借阅记录
            List<BorrowRecord> overdueRecords = borrowService.getOverdueBorrowRecordsByUserId(userId);
            return new ResponseEntity<>(overdueRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * 获取所有借书记录
     *
     * @return ResponseEntity<?> 返回操作结果状态及消息：
     *         - 成功时返回HttpStatus.OK，并携带所有借阅记录列表；
     *         - 查询过程中发生异常则返回HttpStatus.INTERNAL_SERVER_ERROR。
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllBorrowRecords() {
        try {
            List<BorrowRecord> allBorrowRecords = borrowService.getAllBorrowRecords();
            return new ResponseEntity<>(allBorrowRecords, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("查询失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




}

