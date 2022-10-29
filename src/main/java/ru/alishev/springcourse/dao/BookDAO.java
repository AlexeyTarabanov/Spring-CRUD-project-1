package ru.alishev.springcourse.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class BookDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BookDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Book> showAll() {
         List<Book> books = jdbcTemplate.query(
                "select * from book",
                // преобразует строку в экземпляр класса
                new BeanPropertyRowMapper<>(Book.class));

        Comparator<Book> personNameComparator
                = Comparator.comparing(Book::getTitle);

        books.sort(personNameComparator);

        return books;
    }

    public Book show(int id) {
        return jdbcTemplate.query(
                        "select * from book where id=?",
                        new Object[]{id},
                        new BeanPropertyRowMapper<>(Book.class))
                .stream()
                .findAny()
                .orElse(null);
    }

    public void save(Book book) {
        jdbcTemplate.update("insert into book(title, author, year) values (?, ?, ?)",
                book.getTitle(),
                book.getAuthor(),
                book.getYear());
    }

    public void update(int id, Book updateBook) {
        jdbcTemplate.update("update book set title=?, author=?, year=? where id=?",
                updateBook.getTitle(),
                updateBook.getAuthor(),
                updateBook.getYear(),
                id);
    }

    public void delete(int id) {
        jdbcTemplate.update("delete from book where id=?", id);
    }

    // Join'им таблицы Book и Person и получаем человека, которому принадлежит книга с указанным id
    public Optional<Person> getBookOwner(int id) {
        // Выбираем все колонки таблицы Person из объединенной таблицы
        return jdbcTemplate.query(
                            "SELECT p.* " +
                                "FROM Book " +
                                "    join person p on p.person_id = book.person_id " +
                                "WHERE Book.id = ?",
                        new Object[]{id},
                        new BeanPropertyRowMapper<>(Person.class))
                .stream()
                .findAny();
    }

    // этот метод вызывается, когда человек возвращает книгу в библиотеку
    public void release(int id) {
        jdbcTemplate.update("update book set person_id=null where id=?",
                id);
    }

    // этот метод вызывается, когда человек забирает книгу из библиотеки)
    public void assign(int id, Person selectedPerson) {
        jdbcTemplate.update("update book set person_id=? where id=?",
                selectedPerson.getPersonId(),
                id);
    }
}
