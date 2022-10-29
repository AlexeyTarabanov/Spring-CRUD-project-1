package ru.alishev.springcourse.models;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
import java.time.Year;


public class Person {
    private int personId;

    @Pattern(regexp = "^(?=.{1,40}$)[а-яёА-ЯЁ]+(?:[-' ][а-яёА-ЯЁ]+)*$",
            message = "Введите ФИО в формате: Фамилия Имя Отчество")
    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов длиной")
    private String fullName;

    @Min(value = 1900, message = "Год рождения должен быть больше, чем 1900")
    private int yearOfBirth;

    // нужен для Spring
    public Person() {}

    public Person(String fullName, int yearOfBirth) {
        this.fullName = fullName;
        this.yearOfBirth = yearOfBirth;
    }

    public int getPersonId() {
        return personId;
    }

    public void setPersonId(int personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

}
