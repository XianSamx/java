package com.gcc.library1.Controller;

import com.gcc.library1.Model.User;
import com.gcc.library1.Service.BorrowRecordService;
import com.gcc.library1.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * 用户控制器，提供用户注册、登录和删除功能。
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;
    private final BorrowRecordService borrowService;

    /**
     * 注册新用户。
     *
     * @param user 包含用户名和密码的用户对象
     * @return 注册成功返回用户信息，否则返回错误信息及相应状态码
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            // 检查用户是否为空
            if (user == null) {
                return new ResponseEntity<>("用户信息不能为空", HttpStatus.BAD_REQUEST);
            }

            // 检查用户名是否为空
            if (user.getName() == null || user.getName().trim().isEmpty()) {
                return new ResponseEntity<>("用户名不能为空", HttpStatus.BAD_REQUEST);
            }

            // 检查密码是否为空
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return new ResponseEntity<>("密码不能为空", HttpStatus.BAD_REQUEST);
            }

            // 检查用户是否已存在
            try {
                userService.getUserByName(user.getName());
                return new ResponseEntity<>("用户已存在", HttpStatus.CONFLICT);
            } catch (EntityNotFoundException e) {
                // 用户不存在，可以继续注册
                User registeredUser = userService.addUser(user);
                return new ResponseEntity<>(registeredUser, HttpStatus.OK);
            }
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("注册失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 删除指定用户。
     *
     * @param user 要删除的用户对象（需包含有效ID）
     * @return 删除结果消息及HTTP状态码
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestBody User user) {
        // 输入参数校验
        if (user == null || user.getId() == null) {
            return new ResponseEntity<>("用户信息不完整", HttpStatus.BAD_REQUEST);
        }

        Long id = user.getId();

        try {
            // 检查用户是否存在
            userService.getUserById(id);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("找不到用户", HttpStatus.NOT_FOUND);
        }

        // 检查用户是否借书
        boolean hasBorrowedBooks = borrowService.hasBorrowedBooks(id);
        if (hasBorrowedBooks) {
            // 用户正在借书，不能删除
            return new ResponseEntity<>("该用户已借书，无法删除", HttpStatus.CONFLICT);
        }

        // 用户没有借书，可以安全删除
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("用户删除成功", HttpStatus.OK);
        } catch (EntityNotFoundException userNotFound) {
            return new ResponseEntity<>("找不到用户", HttpStatus.NOT_FOUND);
        }
    }



    /**
     * 用户登录验证。
     *
     * @param user 包含用户名和密码的用户对象
     * @return 登录成功返回用户信息，否则返回错误提示及状态码
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();

        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>("用户名不能为空", HttpStatus.BAD_REQUEST);
        }

        if (password == null || password.trim().isEmpty()) {
            return new ResponseEntity<>("密码不能为空", HttpStatus.BAD_REQUEST);
        }

        try {
            User authenticatedUser = userService.getUserByNameAndPassword(name, password);
            return new ResponseEntity<>(authenticatedUser, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            // 统一错误提示，避免用户名枚举攻击
            return new ResponseEntity<>("用户名或密码错误", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("登录失败，请稍后重试", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
