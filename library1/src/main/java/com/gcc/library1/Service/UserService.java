package com.gcc.library1.Service;

import com.gcc.library1.Model.User;
import com.gcc.library1.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User addUser(User user) {
            return userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"+ id));
        existingUser.setName(user.getName());
        existingUser.setPassword(user.getPassword());
        return userRepository.save(existingUser);
    }
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"+ id));
        userRepository.deleteById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"+ id));
    }
    public User getUserByName(String name) {
        return userRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("User not found"+ name));
    }

    public User getUserByNameAndPassword(String name, String password) {
        return userRepository.findByNameAndPassword(name, password)
                .orElseThrow(() -> new EntityNotFoundException("User or Password is mistake"));
    }


}
