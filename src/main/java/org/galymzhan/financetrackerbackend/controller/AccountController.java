package org.galymzhan.financetrackerbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.galymzhan.financetrackerbackend.dto.AccountRequestDto;
import org.galymzhan.financetrackerbackend.dto.AccountResponseDto;
import org.galymzhan.financetrackerbackend.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAll() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> create(@Valid @RequestBody AccountRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AccountResponseDto> update(@PathVariable Long id, @Valid @RequestBody AccountRequestDto dto) {
        return ResponseEntity.ok(accountService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
