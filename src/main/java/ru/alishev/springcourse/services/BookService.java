package ru.alishev.springcourse.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;
import ru.alishev.springcourse.repositories.BookRepository;

import java.util.Date;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Сортировка
    public List<Book> findAll(boolean sortByYear) {
        if (sortByYear)
            return bookRepository.findAll(Sort.by("year"));
        else
            return bookRepository.findAll();
    }

    // Пагинация
    public List<Book> findWithPagination(Integer page, Integer booksPerPage, boolean sortByYear) {
        if (sortByYear)
            return bookRepository.findAll(PageRequest.of(page, booksPerPage, Sort.by("year"))).getContent();
        else
            return bookRepository.findAll();
    }

    public Book show(int id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Transactional
    public void save(Book book) {
        bookRepository.save(book);
    }

    /**
     * добавляем по сути новую книгу
     * (которая не находится в Persistence context),
     * поэтому нужен save
     * */
    @Transactional
    public void update(int id, Book updateBook) {
        Book bookToUpdate = bookRepository.findById(id).get();
        updateBook.setId(id);
        // чтобы не терялась связь при обновлении
        updateBook.setOwner(bookToUpdate.getOwner());

        bookRepository.save(updateBook);
    }
    @Transactional
    public void delete(int id) {
        bookRepository.deleteById(id);
    }

    /**
     * Returns null if book has no owner
     * Здесь Hibernate.initialize() не нужен, так как владелец (сторона One) загружается не лениво
     * */
    public Person getBookOwner(int bookId) {
        return bookRepository.findById(bookId)
                .map(Book::getOwner)
                .orElse(null);
    }

    /**
     * метод вызывается, когда человек возвращает книгу в библиотеку
     * */
    @Transactional
    public void release(int id) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(null);
                    book.setDateTakeBook(null);
                });
    }

    /**
     * метод вызывается, когда человек забирает книгу из библиотеки
     * */
    @Transactional
    public void assign(int id, Person selectedPerson) {
        bookRepository.findById(id).ifPresent(
                book -> {
                    book.setOwner(selectedPerson);
                    book.setDateTakeBook(new Date());
                }
        );
    }

    public List<Book> searchByTitle(String title) {
       return bookRepository.findAllByTitleStartingWith(title);
    }

}
