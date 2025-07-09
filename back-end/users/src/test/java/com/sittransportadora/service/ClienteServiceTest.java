package com.sittransportadora.service;

import com.sittransportadora.model.User;
import com.sittransportadora.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente cliente;

    @BeforeEach
    void setup() {
        cliente = new Cliente();
        cliente.setUuid(UUID.randomUUID());
        cliente.setName("João Teste");
    }

    @Test
    void testSaveCliente() {
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(i -> i.getArgument(0));

        Cliente saved = clienteService.saveCliente(cliente);

        assertNotNull(saved.getCreateDate());
        verify(clienteRepository).save(cliente);
    }

    @Test
    void testFindAll() {
        List<Cliente> lista = Arrays.asList(cliente);
        when(clienteRepository.findAll()).thenReturn(lista);

        List<Cliente> result = clienteService.findAll();

        assertEquals(1, result.size());
        assertEquals("João Teste", result.get(0).getName());
    }

    @Test
    void testFindByIdFound() {
        UUID id = cliente.getUuid();
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        Optional<Cliente> result = clienteService.findById(id);

        assertTrue(result.isPresent());
        assertEquals("João Teste", result.get().getName());
    }

    @Test
    void testUpdateCliente() {
        UUID id = cliente.getUuid();
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente updated = clienteService.updateCliente(id, cliente);

        verify(clienteRepository).save(cliente);
        assertEquals("João Teste", updated.getName());
    }

    @Test
    void testDeleteCliente() {
        UUID id = cliente.getUuid();
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        clienteService.deleteCliente(id);

        verify(clienteRepository).delete(cliente);
    }
}        

