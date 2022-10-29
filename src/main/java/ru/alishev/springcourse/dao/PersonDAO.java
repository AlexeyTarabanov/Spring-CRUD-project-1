package ru.alishev.springcourse.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;


import java.util.*;

@Component
public class PersonDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> showAll() {
        List<Person> people = jdbcTemplate.query(
                "select * from person",
                // преобразует строку в экземпляр класса
                new BeanPropertyRowMapper<>(Person.class));

        Comparator<Person> personNameComparator
                = Comparator.comparing(Person::getFullName);

        people.sort(personNameComparator);

        return people;
    }

    public Person show(int id) {
        return jdbcTemplate.query(
                "select * from person where person_id=?",
                new Object[]{id},
                new BeanPropertyRowMapper<>(Person.class))
                .stream()
                .findAny()
                .orElse(null);
    }

    public void save(Person person) {
        jdbcTemplate.update("insert into person(full_name, year_of_birth) values (?, ?)",
                person.getFullName(),
                person.getYearOfBirth());
    }

    public void update(int id, Person updatePerson) {
        jdbcTemplate.update("update person set full_name=?, year_of_birth=? where person_id=?",
                updatePerson.getFullName(),
                updatePerson.getYearOfBirth(),
                id);
    }

    public void delete(int id) {
        jdbcTemplate.update("delete from person where person_id=?", id);
    }

    // Для валидации уникальности ФИО
    public Optional<Person> getPersonByFullName(String fullName) {
        return jdbcTemplate.query(
                        "select * from person where full_name = ?",
                        new Object[]{fullName},
                        new BeanPropertyRowMapper<>(Person.class))
                .stream()
                .findAny();
    }

    public List<Book> getBooksByPersonId(int id) {
        return jdbcTemplate.query("select * from book where person_id=?",
                new Object[]{id},
                new BeanPropertyRowMapper<>(Book.class));
    }
}
