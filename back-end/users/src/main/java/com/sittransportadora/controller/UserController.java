package com.sittransportadora.controller;

import com.sittransportadora.controller.dto.userdto.UserDTO;
import com.sittransportadora.model.User;
import com.sittransportadora.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
public class UserController {

    private final UserService clienteService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(UserService clienteService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.clienteService = clienteService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }




    @GetMapping("/{id}")
    public ResponseEntity<User> obterUser(@PathVariable UUID id) {
        Optional<User> cliente = clienteService.findById(id);
        return cliente != null ? ResponseEntity.ok().body(cliente.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> obterUsers(){
        List<User> clientes = clienteService.findAll();
        return ResponseEntity.ok().body(clientes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> atualizarUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        User userAtualizado = new User();
        userAtualizado.setName(userDTO.getName());
        userAtualizado.setEmail(userDTO.getEmail());
        userAtualizado.setPhone(userDTO.getPhone());
        userAtualizado.setAddress(userDTO.getAddress());

        User user = clienteService.updateUser(id, userAtualizado);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }


}
