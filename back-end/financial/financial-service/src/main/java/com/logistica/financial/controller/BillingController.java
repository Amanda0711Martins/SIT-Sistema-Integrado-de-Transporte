package com.logistics.financial.controller;

// import com.logistics.financial.dto.BillingDTO;
// import com.logistics.financial.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// import com.logistics.financial.service.BillingService;

// import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/billings")
@RequiredArgsConstructor
@Validated
public class BillingController {

    private final BillingService billingService;

    @PostMapping
    public ResponseEntity<BillingDTO> createBilling(@Valid @RequestBody BillingDTO billingDTO) {
        BillingDTO createdBilling = billingService.createBilling(billingDTO);
        return new ResponseEntity<>(createdBilling, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingDTO> getBillingById(@PathVariable Long id) {
        BillingDTO billing = billingService.getBillingById(id);
        return ResponseEntity.ok(billing);
    }

    @GetMapping("/number/{billingNumber}")
    public ResponseEntity<BillingDTO> getBillingByNumber(@PathVariable String billingNumber) {
        BillingDTO billing = billingService.getBillingByNumber(billingNumber);
        return ResponseEntity.ok(billing);
    }

    @GetMapping
    public ResponseEntity<Page<BillingDTO>> getAllBillings(Pageable pageable) {
        Page<BillingDTO> billings = billingService.getAllBillings(pageable);
        return ResponseEntity.ok(billings);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingDTO> updateBilling(@PathVariable Long id, @Valid @RequestBody BillingDTO billingDTO) {
        BillingDTO updatedBilling = billingService.updateBilling(id, billingDTO);
        return ResponseEntity.ok(updatedBilling);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBilling(@PathVariable Long id) {
        billingService.deleteBilling(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<Page<BillingDTO>> getBillingsByClientId(@PathVariable Long clientId, Pageable pageable) {
        Page<BillingDTO> billings = billingService.getBillingsByClientId(clientId, pageable);
        return ResponseEntity.ok(billings);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<BillingDTO>> getBillingsByStatus(@PathVariable String status, Pageable pageable) {
        Page<BillingDTO> billings = billingService.getBillingsByStatus(status, pageable);
        return ResponseEntity.ok(billings);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BillingDTO>> getOverdueBillings() {
        List<BillingDTO> overdueBillings = billingService.getOverdueBillings();
        return ResponseEntity.ok(overdueBillings);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<BillingDTO> markAsPaid(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String paymentMethod = payload.get("paymentMethod");
        BillingDTO paidBilling = billingService.markAsPaid(id, paymentMethod);
        return ResponseEntity.ok(paidBilling);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<BillingDTO> cancelBilling(@PathVariable Long id) {
        BillingDTO cancelledBilling = billingService.cancelBilling(id);
        return ResponseEntity.ok(cancelledBilling);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BillingDTO>> getBillingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<BillingDTO> billings = billingService.getBillingsByDateRange(startDate, endDate);
        return ResponseEntity.ok(billings);
    }
}