package com.sittransportadora.controller;

import com.sittransportadora.model.Cliente;
import com.sittransportadora.model.Role;
import com.sittransportadora.controller.dto.ClienteDTO;
import com.sittransportadora.service.ClienteService;
import com.sittransportadora.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private RoleService roleService;

    @PostMapping("/signup")
    public ResponseEntity<Cliente> signup(@RequestBody ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setName(clienteDTO.getName());
        cliente.setEmail(clienteDTO.getEmail());
        cliente.setPhone(clienteDTO.getPhone());
        cliente.setAddress(clienteDTO.getAddress());

        Role defaultRole = roleService.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found"));
        cliente.setRoles(Set.of(defaultRole));

        Cliente clienteSalvo = clienteService.saveCliente(cliente);
        return ResponseEntity.ok(clienteSalvo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obterCliente(@PathVariable UUID id) {
        Optional<Cliente> cliente = clienteService.findById(id);
        return cliente != null ? ResponseEntity.ok().body(cliente.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizarCliente(@PathVariable UUID id, @RequestBody ClienteDTO clienteDTO) {
        Cliente clienteAtualizado = new Cliente();
        clienteAtualizado.setName(clienteDTO.getName());
        clienteAtualizado.setEmail(clienteDTO.getEmail());
        clienteAtualizado.setPhone(clienteDTO.getPhone());
        clienteAtualizado.setAddress(clienteDTO.getAddress());

        Cliente cliente = clienteService.updateCliente(id, clienteAtualizado);
        return cliente != null ? ResponseEntity.ok(cliente) : ResponseEntity.notFound().build();
    }
}        
