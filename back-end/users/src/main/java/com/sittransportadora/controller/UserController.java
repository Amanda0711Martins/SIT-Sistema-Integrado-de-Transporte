package com.sittransportadora.controller;

import com.sittransportadora.model.User;
import com.sittransportadora.controller.dto.UserDTO;
import com.sittransportadora.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());

        User userSalvo = clienteService.saveCliente(user);
        return ResponseEntity.ok(userSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> obterCliente(@PathVariable UUID id) {
        Optional<User> cliente = clienteService.findById(id);
        return cliente != null ? ResponseEntity.ok().body(cliente.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> atualizarCliente(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        User userAtualizado = new User();
        userAtualizado.setName(userDTO.getName());
        userAtualizado.setEmail(userDTO.getEmail());
        userAtualizado.setPhone(userDTO.getPhone());
        userAtualizado.setAddress(userDTO.getAddress());

        User user = clienteService.updateCliente(id, userAtualizado);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }


}
