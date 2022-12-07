package ru.alexey.springcourse.Project2Boot.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.alexey.springcourse.Project2Boot.dao.PersonDAO;
import ru.alexey.springcourse.Project2Boot.models.Person;
import ru.alexey.springcourse.Project2Boot.services.PeopleService;
import ru.alexey.springcourse.Project2Boot.utils.PersonValidator;

import java.util.Optional;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;
    private final PersonValidator validator;

    @Autowired
    public PeopleController(PeopleService peopleService, PersonValidator validator) {
        this.peopleService = peopleService;
        this.validator = validator;
    }

    @GetMapping()
    public String index(Model model) {
        model.addAttribute("people", peopleService.findAll());

        return "people/index";
    }

    @GetMapping("/new")
    public String newPerson(Model model) {
        model.addAttribute("person", new Person());

        return "people/new";
    }

    @PostMapping()
    public String create(
            @ModelAttribute("person")
            @Valid Person person,
            BindingResult bindingResult
    ) {
        validator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "/people/new";
        }

        peopleService.save(person);

        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(
            Model model,
            @PathVariable("id") int id
    ) {
        Person person = peopleService.findOne(id);

        model.addAttribute("person", person);

        return "people/edit";
    }

    @GetMapping("/{id}")
    public String show(
            @PathVariable("id") int id,
            Model model
    ) {
        Person person = peopleService.findOne(id);

        model.addAttribute("person", person);
        model.addAttribute("books", peopleService.getBooksByPersonId(id));

        return "people/show";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        peopleService.delete(id);

        return "redirect:/people";
    }

    @PatchMapping("/{id}")
    public String update(
            @ModelAttribute("person")
            @Valid Person person,
            BindingResult bindingResult,
            @PathVariable("id") int id
    ) {
        validator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "people/edit";
        }

        peopleService.update(id, person);

        return "redirect:/people";
    }
}
