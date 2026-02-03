package com.asdf.todo.controller;

import com.asdf.todo.model.Todo;
import com.asdf.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos/v1")
public class TodoController {
    @Autowired private TodoService todoService;

    @GetMapping
    @Operation(summary = "전체 작업 조회", description = "전체 작업 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "204", description = "내용 없음")
    })
    public ResponseEntity<List<Todo>> getAllTodos() {
        List<Todo> todos = todoService.findAll();
        if (todos == null || todos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // 1. Spring: "오, 리스트(자바 객체)를 반환하네? 근데 이 컨트롤러는 @RestController네?"
        // 2. Spring: "Jackson아, 이 'todos' 리스트 좀 JSON으로 바꿔줘(직렬화)."
        // 3. Jackson: "오케이~ 해당 todos 객체 안에 있는 Getter들 다 호출해서 JSON 문자열 만들었어."
        // 4. 응답: HTTP 200 OK + [ { "id": 1, ... }, { "id": 2, ... } ]
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "작업 조회", description = "ID로 작업 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "작업 없음")
    })
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todo = todoService.findById(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(todo);
    }

    @PostMapping
    @Operation(summary = "작업 생성", description = "새로운 작업 생성")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "생성됨")})
    public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
        return ResponseEntity.status(201).body(todoService.save(todo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "작업 수정", description = "ID로 작업 수정")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "성공"),
        @ApiResponse(responseCode = "404", description = "작업 없음")
    })
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo todo) {
        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(todoService.update(id, todo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "작업 삭제", description = "ID로 작업 삭제")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "내용 없음"),
        @ApiResponse(responseCode = "404", description = "작업 없음")
    })
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        Todo existingTodo = todoService.findById(id);
        if (existingTodo == null) {
            return ResponseEntity.notFound().build();
        }

        // 내 실수! 어떻게 반환해야할지 몰랐음
        // return new ResponseEntity.status(204).body(todoService.delete(id));
        todoService.delete(id);
        return ResponseEntity.noContent().build(); // 204 : 응답은 성공했지만 반환 내용은 없다는 의미!(삭제 성공했으니깐)
    }
}
