package ru.alishev.springcourse.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;
import ru.alishev.springcourse.repositories.BookRepository;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAllByOrderByTitle();
    }

    // Сортировка
    public List<Book> findAll(String value) {
        return bookRepository.findAll(Sort.by(value));
    }

    // Пагинация
    public List<Book> findAll(int offset, int size) {
        Slice<Book> allByOrderByTitle = bookRepository.findAll(PageRequest.of(offset, size));
        return allByOrderByTitle.getContent();
    }

    // Комбо (сортировка + Пагинация)
    public List<Book> findAll(int page, int booksPerPage, String value) {
        return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by(value))).getContent();
    }

    public Book show(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    @Transactional
    public void update(int id, Book updateBook) {
        updateBook.setId(id);
        bookRepository.save(updateBook);
    }
    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    /**
     * Join'им таблицы Book и Person и получаем человека, которому принадлежит книга с указанным id
     * */
    public Optional<Person> getBookOwner(int bookId) {
        // Выбираем все колонки таблицы Person из объединенной таблицы
        Book book = bookRepository.findById(bookId).orElse(null);
        return Optional.ofNullable(Objects.requireNonNull(book).getOwner());
    }

    /**
     * метод вызывается, когда человек возвращает книгу в библиотеку
     * */
    @Transactional
    public void release(int id) {
        bookRepository.findById(id).ifPresent(book -> book.setOwner(null));
    }

    /**
     * метод вызывается, когда человек забирает книгу из библиотеки
     * */
    @Transactional
    public void assign(int id, Person selectedPerson) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            book.get().setDateTakeBook(new Date());
            book.get().setOwner(selectedPerson);
        }
    }

    public Optional<Book> search(String title) {
       return bookRepository.findAllByTitleStartingWith(title);
    }

}
