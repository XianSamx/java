package com.gcc.library1.Controller;

import com.gcc.library1.Model.Book;
import com.gcc.library1.Service.BookService;
import com.gcc.library1.Service.BorrowRecordService;
import com.gcc.library1.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 图书管理控制器类，提供图书的增删查等操作接口。
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class BookController {
    private final BookService bookService;
    private final UserService userService;
    private final BorrowRecordService borrowService;

    /**
     * 添加一本新书到系统中。
     *
     * @param inputbook 输入的书籍对象，包含书籍的基本信息（如标题、作者等）
     * @return 返回创建成功的书籍对象及HTTP状态码；若输入为空或服务异常则返回相应错误状态码
     */
    @PostMapping("/addBook")
    public ResponseEntity<Book> addBook(@RequestBody Book inputbook) {
        try {
            if (inputbook == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Book book = bookService.addBook(inputbook);
            if (book == null) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(book, HttpStatus.CREATED);
        } catch (Exception e) {
            // 根据实际业务需求，可以记录日志或返回特定错误码
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取所有图书列表。
     *
     * @return 所有图书组成的列表
     */
    @GetMapping("/getAllBooks")
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * 根据ID获取指定图书的信息。
     *
     * @param inputbook 包含要查询图书ID的对象
     * @return 查询成功时返回对应图书对象及OK状态；若ID为空、未找到或发生其他异常，则返回相应错误提示与状态码
     */
    @PostMapping("/getBookById")
    public ResponseEntity<?> getBookById(@RequestBody Book inputbook) {
        Long id = inputbook.getId();
        if (id == null) {
            return new ResponseEntity<>("Book ID不能为空", HttpStatus.BAD_REQUEST);
        }
        try {
            return new ResponseEntity<>(bookService.getBookById(id), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 根据书名查询书籍信息
     *
     * @param inputbook 包含书名信息的Book对象，用于查询条件
     * @return ResponseEntity<?> 返回包含查询结果的响应实体，
     *         成功时返回书籍列表和HTTP 200状态码，
     *         书名为空时返回错误信息和HTTP 400状态码，
     *         未找到匹配书籍时返回提示信息和HTTP 404状态码，
     *         发生异常时返回错误信息和HTTP 500状态码
     */
    @PostMapping("/getBookByTitle")
    public ResponseEntity<?> getBookByTitle(@RequestBody Book inputbook) {
        String title = inputbook.getTitle();

        // 验证书名是否为空或空白字符
        if (title == null || title.trim().isEmpty()) {
            return new ResponseEntity<>("Title不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            // 调用服务层根据书名查询书籍
            List<Book> books = bookService.getBooksByTitleLike("%"+title+"%");

            // 检查查询结果是否为空
            if (books.isEmpty()) {
                return new ResponseEntity<>("未找到书名为: " + title + " 的书籍", HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (Exception e) {
            // 记录查询过程中发生的异常
            log.error("查询书籍时发生错误: " + title, e);
            return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 删除指定ID的图书。如果该书已被借出，则不允许删除。
     *
     * @param inputBook 包含待删除图书ID的对象
     * @return 删除成功返回确认消息及OK状态；若图书正在被借阅、不存在或存在其他问题，则返回相应的错误提示与状态码
     */
    @DeleteMapping("/deleteBook")
    public ResponseEntity<?> deleteBook(@RequestBody Book inputBook) {
        Long id = inputBook.getId();

        // 添加空值检查
        if (id == null) {
            return new ResponseEntity<>("bookId不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            borrowService.getBorrowBookByBookId(id);
            return new ResponseEntity<>(id+"该书已经借出", HttpStatus.NOT_ACCEPTABLE);
        } catch (EntityNotFoundException e) {
            try {
                bookService.deleteBook(id);
                return new ResponseEntity<>("bookid为" + id + "已删除", HttpStatus.OK);
            } catch (EntityNotFoundException e1) {
                return new ResponseEntity<>(e1.getMessage(), HttpStatus.NOT_FOUND);
            }
        }
    }
}
