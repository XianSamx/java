package com.gcc.library1.Service;

import com.gcc.library1.Model.Book;
import com.gcc.library1.Repository.BookRepository;
import com.gcc.library1.Repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;


    public Book addBook(Book book) {
        return bookRepository.save(book);
    }
    public Book updateBook(Long id, Book Book) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:"+ id));
        book.setTitle(Book.getTitle());
        book.setAuthor(Book.getAuthor());
        book.setDescription(Book.getDescription());
        return bookRepository.save(book);
    }
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)){
            throw new EntityNotFoundException("Book not found with id:"+ id);
        }
        bookRepository.deleteById(id);
    }
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found with id:"+ id));
    }


    public List<Book> getBooksByTitle(String title) {
        List<Book> books = bookRepository.findByTitle(title);
        if (books.isEmpty()) {
            throw new EntityNotFoundException("未找到书名为: " + title + " 的书籍");
        } else{
            return bookRepository.findByTitle(title);
        }
    }

    public List<Book> getBooksByTitleLike(String title){
        List<Book> books = bookRepository.findByTitleLike(title);
        if (books.isEmpty()) {
            throw new EntityNotFoundException("未找到书名为: " + title + " 的书籍");
        } else{
            return bookRepository.findByTitleLike(title);
        }
    }



}
