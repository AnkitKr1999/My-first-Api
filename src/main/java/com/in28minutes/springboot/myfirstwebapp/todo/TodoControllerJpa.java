package com.in28minutes.springboot.myfirstwebapp.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Controller
@SessionAttributes("name")
public class TodoControllerJpa {
    private TodoService todoService;
    private TodoRepository todoRepository;
    @Autowired
    public TodoControllerJpa(TodoService todoService,TodoRepository todoRepository) {
        this.todoService = todoService;
        this.todoRepository=todoRepository;
    }
    @RequestMapping("list-todos")
    public String listAllTodos(ModelMap model)
    {
        String username = getLoggedInUsername(model);
//        List<Todo> todos = todoService.findByUsername(username); // getting static list from todoService
        List<Todo> todos=todoRepository.findByUsername(username); // getting the list from JPA
        model.put("todos",todos);
        return "listTodos";
    }

    private static String getLoggedInUsername(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @RequestMapping(value="add-todo", method= RequestMethod.GET)
    public String showNewTodo(ModelMap model)
    {
        String username = getLoggedInUsername(model);
        Todo todo = new Todo(0, username, "", LocalDate.now().plusYears(1), false);
        model.put("todo", todo);
        return "Todo";
    }

    @RequestMapping(value="add-todo", method= RequestMethod.POST)
    public String addNewTodo(ModelMap model, @Valid  Todo todo, BindingResult result)
    {
        if(result.hasErrors())
            return "Todo";
        String username = getLoggedInUsername(model);
        todo.setUsername(username);
        todoRepository.save(todo); // saving in database
//        todoService.addTodo(username, todo.getDescription(), todo.getTargetDate(), todo.isDone());
        return "redirect:list-todos";
    }

    @RequestMapping("delete-todo")
    public String deleteTodo(@RequestParam int id)
    {
        todoRepository.deleteById(id); // deleting from the database
//        todoService.deleteById(id);
        return "redirect:list-todos";
    }

    @RequestMapping(value="update-todo",method = RequestMethod.GET)
    public String showUpdateTodoPage(@RequestParam int id,ModelMap model)
    {
        Todo todo=todoRepository.findById(id).get();
//        Todo todo=todoService.findById(id);
        model.addAttribute(todo);
        return "Todo";
    }

    @RequestMapping(value="update-todo", method= RequestMethod.POST)
    public String updateTodo(ModelMap model, @Valid  Todo todo, BindingResult result)
    {
        if(result.hasErrors())
            return "Todo";
        String username = getLoggedInUsername(model);
        todo.setUsername(username);
//        todoService.updateTodo(todo);
        todoRepository.save(todo);
        return "redirect:list-todos";
    }
}
