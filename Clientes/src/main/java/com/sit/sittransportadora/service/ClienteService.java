package com.sit.sittransportadora.service;

import com.sit.sittransportadora.domain.Cliente;
import com.sit.sittransportadora.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public Optional<Cliente> findById(Long id)  {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        return cliente;
    }

    //For now all data are updated
    public Cliente updateCliente(Long id, Cliente clienteAtualizado){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            clienteRepository.save(cliente.get());
        }
        return cliente.get();
    }

    public void deleteCliente(Long id){
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            clienteRepository.delete(cliente.get());
        }
    }
}
