package ru.alishev.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.alishev.springcourse.dao.BookDAO;
import ru.alishev.springcourse.dao.PersonDAO;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;

import javax.validation.Valid;
import java.util.Optional;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookDAO bookDAO;
    private final PersonDAO personDAO;

    @Autowired
    public BooksController(BookDAO bookDAO, PersonDAO personDAO) {
        this.bookDAO = bookDAO;
        this.personDAO = personDAO;
    }

    @GetMapping()
    public String showAll(Model model) {
        model.addAttribute("books", bookDAO.showAll());
        return "books/show_all";
    }

    @GetMapping("{id}")
    private String show(@PathVariable("id") int id,
                        Model model,
                        @ModelAttribute("person") Person person) {

        model.addAttribute("book", bookDAO.show(id));

        Optional<Person> bookOwner = bookDAO.getBookOwner(id);

        if (bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", personDAO.showAll());

        return "books/show";
    }


    @GetMapping("/new")
    private String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    private String create(@ModelAttribute("book") @Valid Book book,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "books/new";

        bookDAO.save(book);

        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    private String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.show(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    private String update(@PathVariable("id") int id,
                          @ModelAttribute("book") @Valid Book book,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "books/edit";

        bookDAO.update(id, book);

        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    private String delete(@PathVariable("id") int id) {
        bookDAO.delete(id);

        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        bookDAO.release(id);
        return "redirect:/books/" + id;
    }

    @PatchMapping ("/{id}/assign")
    public String assign(@PathVariable("id") int id,
                         @ModelAttribute("person") Person selectedPerson) {

        bookDAO.assign(id, selectedPerson);

        return "redirect:/books/" + id;
    }
}
