package ru.alexey.springcourse.Project2Boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.alexey.springcourse.Project2Boot.models.Book;
import ru.alexey.springcourse.Project2Boot.models.Person;
import ru.alexey.springcourse.Project2Boot.services.BooksService;
import ru.alexey.springcourse.Project2Boot.services.PeopleService;

@Controller
@RequestMapping("/books")
public class BookController {

    private final PeopleService peopleService;
    private final BooksService booksService;

    @Autowired
    public BookController(PeopleService peopleService, BooksService booksService) {
        this.peopleService = peopleService;
        this.booksService = booksService;
    }

    @GetMapping()
    public String index(
            Model model,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "books_per_page", required = false) Integer booksPerPage,
            @RequestParam(value = "sort_by_year", required = false) boolean sortByYear
    ) {

        if (page == null || booksPerPage == null) {
            model.addAttribute("books", booksService.findAll(sortByYear));
        } else {
            model.addAttribute("books", booksService.findWidthPagination(page, booksPerPage, sortByYear));
        }

        return "books/index";
    }

    @GetMapping("/new")
    public String newBook(@ModelAttribute("book") Book book) {
        return "books/new";
    }

    @PostMapping()
    public String create(
            @Valid
            @ModelAttribute("book") Book book,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return "books/new";
        }

        booksService.save(book);

        return "redirect:/books";
    }

    @GetMapping("/{id}")
    public String show(
            Model model,
            @PathVariable("id") int id,
            @ModelAttribute("person") Person person
    ) {
        model.addAttribute("book", booksService.findOne(id));

        Person booksOwner = booksService.getBookOwner(id);

        if (booksOwner!=null){
            model.addAttribute("owner", booksOwner);
        } else {
            model.addAttribute("people", peopleService.findAll());
        }

        return "/books/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("book", booksService.findOne(id));
        return "books/edit";
    }

    @PatchMapping("/{id}")
    public String update(
            @ModelAttribute("book") @Valid Book book,
            BindingResult bindingResult,
            @PathVariable("id") int id
    ) {
        if (bindingResult.hasErrors()) {
            return "books/edit";
        }

        booksService.update(id, book);

        return "redirect:/books";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        booksService.delete(id);
        return "redirect:/books";
    }

    @PatchMapping("/{id}/release")
    public String release(@PathVariable("id") int id) {
        booksService.release(id);

        return "redirect:/books/" + id;
    }

    @PatchMapping("/{id}/assign")
    public String assign(
            @PathVariable("id") int id,
            @ModelAttribute("person") Person selectedPerson
    ) {
        booksService.assign(id, selectedPerson);
        return "redirect:/books/" + id;
    }

    @GetMapping("/search")
    public String searchPage(){
        return "books/search";
    }

    @PostMapping("/search")
    public String makeSearch(Model model, @RequestParam("query") String query){
        System.out.println(query);
        model.addAttribute("books", booksService.searchByName(query));

        return "books/search";
    }
}
