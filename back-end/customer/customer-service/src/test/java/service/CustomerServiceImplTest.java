package service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistica.customer.dto.CustomerDTO;
import com.logistica.customer.exception.CustomerNotFoundException;
import com.logistica.customer.exception.ValidationException;
import com.logistica.customer.model.Customer;
import com.logistica.customer.repository.CustomerRepository;
import com.logistica.customer.service.AuditService;
import com.logistica.customer.service.CustomerServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @Spy // Usamos @Spy para ter um ObjectMapper real, necessário para o método toJsonString
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Cliente Teste");
        customer.setCnpj("12345678000199");
        customer.setEmail("teste@cliente.com");
        customer.setStatus(Customer.CustomerStatus.ACTIVE);

        customerDTO = CustomerDTO.fromEntity(customer);
    }

    @Test
    void testCreateCustomer_Success() {
        // Arrange
        when(customerRepository.findByCnpj(anyString())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        CustomerDTO result = customerService.createCustomer(customerDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Cliente Teste", result.getName());
        verify(auditService).logEvent(eq("Customer"), eq(1L), eq("CREATE"), isNull(), anyString());
    }

    @Test
    void testCreateCustomer_ThrowsException_WhenCnpjExists() {
        // Arrange
        when(customerRepository.findByCnpj(customerDTO.getCnpj())).thenReturn(Optional.of(customer));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            customerService.createCustomer(customerDTO);
        });
        assertEquals("Já existe um cliente cadastrado com este CNPJ", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testCreateCustomer_ThrowsException_WhenEmailExists() {
        // Arrange
        when(customerRepository.findByCnpj(customerDTO.getCnpj())).thenReturn(Optional.empty());
        when(customerRepository.findByEmail(customerDTO.getEmail())).thenReturn(Optional.of(customer));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            customerService.createCustomer(customerDTO);
        });
        assertEquals("Já existe um cliente cadastrado com este e-mail", exception.getMessage());
        verify(customerRepository, never()).save(any());
    }

    @Test
    void testGetCustomerById_ThrowsNotFoundException() {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(99L);
        });
    }
    
    @Test
    void testUpdateCustomer_ThrowsNotFoundException() {
        // Arrange
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> {
            customerService.updateCustomer(99L, customerDTO);
        });
    }

    @Test
    void testDeleteCustomer_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository).delete(customer);
        verify(auditService).logEvent(eq("Customer"), eq(1L), eq("DELETE"), anyString(), isNull());
    }

    @Test
    void testChangeCustomerStatus_Success() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        customerService.changeCustomerStatus(1L, Customer.CustomerStatus.INACTIVE);

        // Assert
        assertEquals(Customer.CustomerStatus.INACTIVE, customer.getStatus());
        verify(customerRepository).save(customer);
        verify(auditService).logEvent(eq("Customer"), eq(1L), eq("STATUS_CHANGE"), anyString(), anyString());
    }
}
