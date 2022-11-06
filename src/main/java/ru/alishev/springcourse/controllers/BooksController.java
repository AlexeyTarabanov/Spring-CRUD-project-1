package ru.alishev.springcourse.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.alishev.springcourse.models.Book;
import ru.alishev.springcourse.models.Person;
import ru.alishev.springcourse.services.BookService;
import ru.alishev.springcourse.services.PeopleService;

import javax.validation.Valid;
import java.util.Optional;


@Controller
@RequestMapping("/books")
public class BooksController {

    private final BookService bookService;
    private final PeopleService peopleService;

    @Autowired
    public BooksController(BookService bookService, PeopleService peopleService) {
        this.bookService = bookService;
        this.peopleService = peopleService;
    }

    /**
     * Показывает все книги на странице.
     * @param page страница
     * @param booksPerPage количество книг на странице
     * @param sortByYear сортировка по году
     *
     * ?sort_by_year=true
     * ?page=1&books_per_page=4
     * ?page=1&books_per_page=3&sort_by_year=true
     * */
    @GetMapping()
    public String showAll(Model model,
                          @RequestParam(value = "page", required = false) Integer page,
                          @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
                          @RequestParam(value = "sort_by_year", required = false, defaultValue = "false") boolean sortByYear) {


        if (page != null && booksPerPage != null && sortByYear)
            model.addAttribute("books", bookService.findAll(page, booksPerPage, "year"));
        else if (page != null && booksPerPage != null)
            model.addAttribute("books", bookService.findAll(page, booksPerPage));
        else if (sortByYear) {
            model.addAttribute("books", bookService.findAll("year"));
        } else
            model.addAttribute("books", bookService.findAll());

        return "books/show_all";
    }

    /**
     * Показывает выбранную книгу.
     *
     * @param id книги
     * @param person владелец книги
     * */
    @GetMapping("{id}")
    private String show(@PathVariable("id") int id,
                        Model model,
                        @ModelAttribute("person") Person person) {

        model.addAttribute("book", bookService.show(id));

        Optional<Person> bookOwner = bookService.getBookOwner(id);

        if (bookOwner.isPresent())
            model.addAttribute("owner", bookOwner.get());
        else
            model.addAttribute("people", peopleService.findAll());

        return "books/show";
    }

    /**
     * страница поиска книги
     *
     * @param bookName то что пользователь вводит в поле поиска
     * */
    @GetMapping("/search")
    private String search(Model model,
                          @RequestParam(value = "bookName", required = false) String bookName) {

        if (bookName == null || bookName.isEmpty())
            return "books/search";

        Optional<Book> findBook = bookService.search(bookName);

        if (findBook.isPresent()) {
            Book book = findBook.get();
            model.addAttribute("findBook", book);
            Optional<Person> bookOwner = bookService.getBookOwner(book.getId());
            if (bookOwner.isPresent()) {
                Person owner = bookOwner.get();
                model.addAttribute("person", owner);
            }
        } else {
            model.addAttribute("bookNotFound", true);
        }

        return "books/search";
    }

    /**
     * возвращает форму для создания книги
     * */
    @GetMapping("/new")
    private String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    /**
     * страница создания новой книги
     *
     * */
    @PostMapping()
    private String create(@ModelAttribute("book") @Valid Book book,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "books/new";

        bookService.save(book);

        return "redirect:/books";
    }

    /**
     * возвращает форму для редактирования книги
     * */
    @GetMapping("/{id}/edit")
    private String edit(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookService.show(id));
        return "books/edit";
    }

    /**
     * страница для редактирования книг
     *
     * @param id книги
     * */
    @PatchMapping("/{id}")
    private String update(@PathVariable("id") int id,
                          @ModelAttribute("book") @Valid Book book,
                          BindingResult bindingResult) {

        if (bindingResult.hasErrors())
            return "books/edit";

        bookService.update(id, book);

        return "redirect:/books";
    }

    /**
     * метод для удаления книг
     * */
    @DeleteMapping("/{id}")
    private String delete(@PathVariable("id") int id) {
        bookService.delete(id);

        return "redirect:/books";
    }

    /**
     * освобождает книгу
     * */
    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        bookService.release(id);
        return "redirect:/books/" + id;
    }

    /**
     * назначает книге нового пользователя
     * */
    @PatchMapping ("/{id}/assign")
    public String assign(@PathVariable("id") int id,
                         @ModelAttribute("person") Person selectedPerson) {

        bookService.assign(id, selectedPerson);

        return "redirect:/books/" + id;
    }
}
