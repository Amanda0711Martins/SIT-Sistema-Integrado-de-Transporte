package com.sittransportadora.service;

import com.sittransportadora.model.User;
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


    public User saveCliente(User user){
        user.setCreateDate(LocalDateTime.now());
        return clienteRepository.save(user);
    }

    public List<User> findAll(){
        return clienteRepository.findAll();
    }

    public Optional<User> findById(UUID id)  {
        Optional<User> cliente = clienteRepository.findById(id);
        return cliente;
    }

    //For now all data are updated
    public User updateCliente(UUID id, User userAtualizado){
        Optional<User> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            clienteRepository.save(cliente.get());
        }
        return cliente.get();
    }

    public void deleteCliente(UUID id){
        Optional<User> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()){
            clienteRepository.delete(cliente.get());
        }
    }
}
