package com.pt.Capstone.controllers;

import com.cloudinary.Cloudinary;
import com.pt.Capstone.requests.*;
import com.pt.Capstone.responses.AuthResponse;
import com.pt.Capstone.responses.CustomerResponse;
import com.pt.Capstone.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Validated
public class CustomerController {

    private final CustomerService customerService;
    private final Cloudinary cloudinary;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PERSONALTRAINER')")
    public ResponseEntity<String> register(@RequestBody @Valid CustomerRegisterRequest customerRegisterRequest) {
        customerService.register(customerRegisterRequest);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        String token = customerService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ).getToken();
        List<String> roles = customerService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ).getRoles();
        Long id = customerService.authenticateUser(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ).getId();
        return ResponseEntity.ok().body(new AuthResponse(token, roles, id));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('PERSONALTRAINER')")
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('CUSTOMER' , 'PERSONALTRAINER')")
    public CustomerResponse findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('CUSTOMER' , 'PERSONALTRAINER')")
    public CustomerResponse update(@PathVariable Long id, @RequestBody @Valid CustomerPutRequest customerPutRequest) {
        return customerService.update(id, customerPutRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('PERSONALTRAINER')")
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    @PutMapping("/password/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    public CustomerResponse updatePassword(@PathVariable Long id, @RequestBody @Valid PasswordRequest passwordRequest) {
        String password = passwordRequest.getPassword();
        String oldPassword = passwordRequest.getOldPassword();
        return customerService.updatePassword(id, password, oldPassword);
    }

    @PutMapping("/lastPaymentDate/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('PERSONALTRAINER')")
    public CustomerResponse updateLastPaymentDate(@PathVariable Long id, @RequestBody LastPaymentRequest lastPaymentRequest) {
        LocalDate lastPaymentDate = lastPaymentRequest.getLastPaymentDate();
        return customerService.updateLastPaymentDate(id, lastPaymentDate);
    }

    @PutMapping(value ="/avatar/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('CUSTOMER')")
    public CustomerResponse updateAvatar(@PathVariable Long id, @RequestPart("profile") MultipartFile file) {
        try{ var result = cloudinary.uploader().upload(
                file.getBytes(), Cloudinary.asMap("folder", "CustomerAvatar", "public_id", file.getOriginalFilename())
        );
            String url = result.get("secure_url").toString();
            return this.customerService.updateAvatar(id, url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
