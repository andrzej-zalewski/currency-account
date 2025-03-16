package com.nn_group.currencyaccount.ui.rest;

import com.nn_group.currencyaccount.application.service.AccountService;
import com.nn_group.currencyaccount.domain.model.Account;
import com.nn_group.currencyaccount.domain.model.Currency;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "API for managing currency accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Create a new currency account", description = "Creates a new account with a specified target currency and initial base amount. The base currency is set to the default value from configuration (e.g., PLN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest request) {
        Account account = accountService.createAccount(
                request.firstName(),
                request.lastName(),
                Currency.valueOf(request.targetCurrency()),
                request.initialBaseAmount()
        );
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account details", description = "Retrieves the details of an account by its unique identifier.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<Account> getAccount(
            @Parameter(description = "Unique identifier of the account", required = true) @PathVariable UUID id) {
        try {
            Account account = accountService.getAccount(id);
            return ResponseEntity.ok(account);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/exchange")
    @Operation(summary = "Exchange currency", description = "Exchanges an amount from one currency to another within the account. Only exchanges between the account's base and target currencies are supported.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currency exchanged successfully", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = Account.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient balance", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "503", description = "Exchange rate service unavailable", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<Account> exchangeCurrency(
            @Parameter(description = "Unique identifier of the account", required = true) @PathVariable UUID id,
            @Valid @RequestBody ExchangeRequest request) {
        Account account = accountService.exchangeCurrency(
                id,
                Currency.valueOf(request.fromCurrency()),
                Currency.valueOf(request.toCurrency()),
                request.amount()
        );
        return ResponseEntity.ok(account);
    }

    record AccountRequest(
            @NotBlank(message = "First name is required") String firstName,
            @NotBlank(message = "Last name is required") String lastName,
            @NotBlank(message = "Target currency is required") String targetCurrency,
            @NotNull(message = "Initial base amount is required")
            @DecimalMin(value = "0.0", inclusive = true, message = "Initial base amount must be non-negative")
            BigDecimal initialBaseAmount) {}

    record ExchangeRequest(
            @NotBlank(message = "Source currency is required") String fromCurrency,
            @NotBlank(message = "Target currency is required") String toCurrency,
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
            BigDecimal amount) {}
}