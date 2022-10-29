package ru.alishev.springcourse.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.alishev.springcourse.dao.BookDAO;
import ru.alishev.springcourse.dao.PersonDAO;
import ru.alishev.springcourse.models.Person;
import ru.alishev.springcourse.util.PersonValidator;

import javax.validation.Valid;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonDAO personDAO;
    private final PersonValidator personValidator;

    @Autowired
    public PeopleController(PersonDAO personDAO, PersonValidator personValidator) {
        this.personDAO = personDAO;
        this.personValidator = personValidator;
    }

    @GetMapping()
    private String showAll(Model model) {
        model.addAttribute("people", personDAO.showAll());
        return "people/show_all";
    }

    /**
     * получает одного человека по его id из DAO
     * и передает на отображение в представление
     * */
    @GetMapping("/{person_id}")
    private String show(@PathVariable("person_id") int id,
                        Model model) {
        model.addAttribute("person", personDAO.show(id));
        model.addAttribute("books", personDAO.getBooksByPersonId(id));

        return "people/show";
    }

    /** возвращает HTML форму для создания человека */
    @GetMapping("/new")
    private String newPerson(@ModelAttribute("person") Person person) {
        //model.addAttribute("person", new Person());
        return "people/new";
    }

    @PostMapping
    public String create(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {

        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/new";
        }

        personDAO.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{person_id}/edit")
    public String edit(@PathVariable("person_id") int id, Model model) {
        model.addAttribute("person", personDAO.show(id));
        return "people/edit";
    }

    @PatchMapping("/{person_id}")
    public String update(@PathVariable("person_id") int id,
                         @ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "people/edit";

        personDAO.update(id, person);
        return "redirect:/people";
    }

    @DeleteMapping("/{person_id}")
    public String delete(@PathVariable("person_id") int id) {
        personDAO.delete(id);
        return "redirect:/people";
    }
}
