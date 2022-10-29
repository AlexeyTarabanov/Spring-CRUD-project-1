package ru.alishev.springcourse.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.alishev.springcourse.dao.PersonDAO;
import ru.alishev.springcourse.models.Person;

@Component
public class PersonValidator implements Validator {

    private final PersonDAO personDAO;

    @Autowired
    public PersonValidator(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    // указываем к какому классу этот Validator относится
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Person.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Person person = (Person) target;

        // fullName -на каком поле случилась ошибка
        // второй аргумент - код ошибки
        // сообщение об ошибке
        if (personDAO.getPersonByFullName(person.getFullName()).isPresent()) {
            errors.rejectValue("fullName", "",
                    "Пользователь с таким ФИО уже существует");
        }
    }
}
