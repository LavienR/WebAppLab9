package com.example.secure_customer_api.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import com.example.secure_customer_api.dto.CustomerRequestDTO;
import com.example.secure_customer_api.dto.CustomerResponseDTO;
import com.example.secure_customer_api.dto.CustomerUpdateDTO;

public interface CustomerService {
    
    List<CustomerResponseDTO> getAllCustomers();
    
    CustomerResponseDTO getCustomerById(Long id);
    
    CustomerResponseDTO createCustomer(CustomerRequestDTO requestDTO);
    
    CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO requestDTO);
    
    void deleteCustomer(Long id);
    
    List<CustomerResponseDTO> searchCustomers(String keyword);
    
    List<CustomerResponseDTO> getCustomersByStatus(String status);

    //ex5.3
    List<CustomerResponseDTO> advancedSearch(String name, String email, String status);
    //ex6.1
    //ex6.2
    Page<CustomerResponseDTO> getAllCustomers(int page, int size, Sort sort);
    //ex7.2
    CustomerResponseDTO partialUpdateCustomer(Long id, CustomerUpdateDTO updateDTO);


}
