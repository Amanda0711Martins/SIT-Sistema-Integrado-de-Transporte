package com.sittransportadora.service;

import com.sittransportadora.model.Cliente;
import com.sittransportadora.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;


    public Cliente saveCliente(Cliente cliente){
        cliente.setCreateDate(LocalDateTime.now());
        return clienteRepository.save(cliente);
    }

    public List<Cliente> findAll(){
        return clienteRepository.findAll();
    }

    public Optional<Cliente> findById(UUID id)  {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente;
    }

    //For now all data are updated
    public Cliente updateCliente(UUID id, Cliente clienteAtualizado){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            clienteRepository.save(cliente.get());
        }
        return cliente.get();
    }

    public void deleteCliente(UUID id){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            clienteRepository.delete(cliente.get());
        }
    }
}
