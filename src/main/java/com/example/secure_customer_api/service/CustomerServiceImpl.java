package com.example.secure_customer_api.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secure_customer_api.dto.CustomerRequestDTO;
import com.example.secure_customer_api.dto.CustomerResponseDTO;
import com.example.secure_customer_api.dto.CustomerUpdateDTO;
import com.example.secure_customer_api.entity.Customer;
import com.example.secure_customer_api.exception.DuplicateResourceException;
import com.example.secure_customer_api.exception.ResourceNotFoundException;
import com.example.secure_customer_api.repository.CustomerRepository;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
    
    private final CustomerRepository customerRepository;
    
    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    @Override
    public Page<CustomerResponseDTO> getAllCustomers(int page, int size, Sort sort) {
    Pageable pageable = PageRequest.of(page, size, sort);

    Page<Customer> customerPage = customerRepository.findAll(pageable);

    return customerPage.map(this::convertToResponseDTO);
}

    
    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return convertToResponseDTO(customer);
    }
    
    @Override
    public CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO) {
        // Check for duplicates
        if (customerRepository.existsByCustomerCode(requestDTO.getCustomerCode())) {
            throw new DuplicateResourceException("Customer code already exists: " + requestDTO.getCustomerCode());
        }
        
        if (customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.getEmail());
        }
        
        // Convert DTO to Entity
        Customer customer = convertToEntity(requestDTO);
        
        // Save to database
        Customer savedCustomer = customerRepository.save(customer);
        
        // Convert Entity to Response DTO
        return convertToResponseDTO(savedCustomer);
    }
    
    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        
        // Check if email is being changed to an existing one
        if (!existingCustomer.getEmail().equals(requestDTO.getEmail()) 
            && customerRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + requestDTO.getEmail());
        }
        
        // Update fields
        existingCustomer.setFullName(requestDTO.getFullName());
        existingCustomer.setEmail(requestDTO.getEmail());
        existingCustomer.setPhone(requestDTO.getPhone());
        existingCustomer.setAddress(requestDTO.getAddress());
        
        // Don't update customerCode (immutable)
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToResponseDTO(updatedCustomer);
    }
    
    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
    
    @Override
    public List<CustomerResponseDTO> searchCustomers(String keyword) {
        return customerRepository.searchCustomers(keyword)
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CustomerResponseDTO> getCustomersByStatus(String status) {
        Customer.CustomerStatus enumStatus = Customer.CustomerStatus.valueOf(status.toUpperCase());
        return customerRepository.findByStatus(enumStatus).stream().map(this::convertToResponseDTO).collect(Collectors.toList());
    }
    
    // Helper Methods for DTO Conversion
    
    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();
        dto.setId(customer.getId());
        dto.setCustomerCode(customer.getCustomerCode());
        dto.setFullName(customer.getFullName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setAddress(customer.getAddress());
        dto.setStatus(customer.getStatus().toString());
        dto.setCreatedAt(customer.getCreatedAt());
        return dto;
    }
    
    private Customer convertToEntity(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerCode(dto.getCustomerCode());
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        return customer;
    }

  @Override
public List<CustomerResponseDTO> advancedSearch(String name, String email, String status) {
    List<Customer> customers = customerRepository.findAll();

    return customers.stream()

            // Filter by name
            .filter(c -> name == null 
                    || (c.getFullName() != null 
                        && c.getFullName().toLowerCase().contains(name.toLowerCase())))

            // Filter by email
            .filter(c -> email == null 
                    || (c.getEmail() != null 
                        && c.getEmail().toLowerCase().contains(email.toLowerCase())))

            // Filter by status (ENUM)
            .filter(c -> {
                if (status == null) return true;
                try {
                    return c.getStatus() == Customer.CustomerStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return false; // invalid status ignored
                }
            })
            // Convert to DTO using existing method
            .map(this::convertToResponseDTO)
            .collect(Collectors.toList());
}
    //ex7.2
    @Override
    public CustomerResponseDTO partialUpdateCustomer(Long id, CustomerUpdateDTO updateDTO) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    
    // Only update non-null fields
    if (updateDTO.getFullName() != null) {
        customer.setFullName(updateDTO.getFullName());
    }
    if (updateDTO.getEmail() != null) {
        customer.setEmail(updateDTO.getEmail());
    }
    // ... other fields
     if (updateDTO.getPhone() != null && !updateDTO.getPhone().isBlank()) {
        customer.setPhone(updateDTO.getPhone());
   }

    // Address
    if (updateDTO.getAddress() != null && !updateDTO.getAddress().isBlank()) {
        customer.setAddress(updateDTO.getAddress());
    }
    return convertToResponseDTO(customerRepository.save(customer));
}

}