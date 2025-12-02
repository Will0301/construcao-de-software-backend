package br.com.construcao.controller;

import br.com.construcao.model.dto.request.UserRequest;
import br.com.construcao.model.dto.response.UserResponse;
import br.com.construcao.service.UserService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.addUser(request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@PathParam("email") String email) {
        userService.delete(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/healthz")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Tudo jóia chefia");
    }
}

