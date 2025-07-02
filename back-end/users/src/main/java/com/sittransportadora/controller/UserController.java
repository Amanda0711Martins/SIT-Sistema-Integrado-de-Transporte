package com.sittransportadora.controller;

import com.sittransportadora.model.User;
import com.sittransportadora.controller.dto.UserDTO;
import com.sittransportadora.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class UserController {

    private final ClienteService clienteService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserController(ClienteService clienteService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.clienteService = clienteService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }




    @GetMapping("/{id}")
    public ResponseEntity<User> obterCliente(@PathVariable UUID id) {
        Optional<User> cliente = clienteService.findById(id);
        return cliente != null ? ResponseEntity.ok().body(cliente.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> obterClientes(){
        List<User> clientes = clienteService.findAll();
        return ResponseEntity.ok().body(clientes);
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
