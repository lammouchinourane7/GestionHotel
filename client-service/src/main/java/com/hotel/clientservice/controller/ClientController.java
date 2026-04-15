package com.hotel.clientservice.controller;

import com.hotel.clientservice.dto.ClientExistenceResponse;
import com.hotel.clientservice.dto.ClientChangeResponse;
import com.hotel.clientservice.dto.ClientHistoryResponse;
import com.hotel.clientservice.dto.ClientLoyaltyResponse;
import com.hotel.clientservice.dto.ClientPageResponse;
import com.hotel.clientservice.dto.ClientPageSearchMode;
import com.hotel.clientservice.dto.ClientProfileResponse;
import com.hotel.clientservice.dto.ClientRequest;
import com.hotel.clientservice.dto.ClientResponse;
import com.hotel.clientservice.dto.ClientStatisticsResponse;
import com.hotel.clientservice.dto.ClientStayPreferencesRequest;
import com.hotel.clientservice.dto.ClientStayPreferencesResponse;
import com.hotel.clientservice.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Client Service", description = "Gestion complete des clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "Recuperer tous les clients")
    public ResponseEntity<List<ClientResponse>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/page")
    @Operation(summary = "Recuperer les clients avec pagination et tri")
    public ResponseEntity<ClientPageResponse> getClientsPage(@org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
                                                             @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size,
                                                             @org.springframework.web.bind.annotation.RequestParam(defaultValue = "createdAt") String sortBy,
                                                             @org.springframework.web.bind.annotation.RequestParam(defaultValue = "desc") String direction,
                                                             @org.springframework.web.bind.annotation.RequestParam(defaultValue = "ALL") ClientPageSearchMode mode,
                                                             @org.springframework.web.bind.annotation.RequestParam(defaultValue = "") String query) {
        return ResponseEntity.ok(clientService.getClientsPage(page, size, sortBy, direction, mode, query));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Recuperer les statistiques clients")
    public ResponseEntity<ClientStatisticsResponse> getClientStatistics() {
        return ResponseEntity.ok(clientService.getClientStatistics());
    }

    @GetMapping("/export/csv")
    @Operation(summary = "Exporter les clients au format CSV")
    public ResponseEntity<byte[]> exportClientsCsv() {
        byte[] csvBytes = clientService.exportClientsCsv().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=clients-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Recuperer un client par son id")
    public ResponseEntity<ClientResponse> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping("/{id}/profile")
    @Operation(summary = "Recuperer le profil client enrichi")
    public ResponseEntity<ClientProfileResponse> getClientProfile(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientProfile(id));
    }

    @GetMapping("/{id}/loyalty")
    @Operation(summary = "Recuperer la synthese de fidelisation d'un client")
    public ResponseEntity<ClientLoyaltyResponse> getClientLoyalty(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientLoyalty(id));
    }

    @GetMapping("/{id}/history")
    @Operation(summary = "Recuperer l'historique de reservation d'un client")
    public ResponseEntity<ClientHistoryResponse> getClientHistory(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientHistory(id));
    }

    @GetMapping("/{id}/preferences")
    @Operation(summary = "Recuperer les preferences de sejour d'un client")
    public ResponseEntity<ClientStayPreferencesResponse> getClientPreferences(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientPreferences(id));
    }

    @PutMapping("/{id}/preferences")
    @Operation(summary = "Mettre a jour les preferences de sejour d'un client")
    public ResponseEntity<ClientStayPreferencesResponse> updateClientPreferences(@PathVariable Long id,
                                                                                 @Valid @RequestBody ClientStayPreferencesRequest request) {
        return ResponseEntity.ok(clientService.updateClientPreferences(id, request));
    }

    @GetMapping("/{id}/changes")
    @Operation(summary = "Recuperer l'historique des changements d'un client")
    public ResponseEntity<List<ClientChangeResponse>> getClientChanges(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientChangeHistory(id));
    }

    @PostMapping
    @Operation(summary = "Creer un nouveau client")
    public ResponseEntity<ClientResponse> createClient(@Valid @RequestBody ClientRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createClient(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour un client")
    public ResponseEntity<ClientResponse> updateClient(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/firstname/{firstName}")
    @Operation(summary = "Rechercher des clients par prenom")
    public ResponseEntity<List<ClientResponse>> searchByFirstName(@PathVariable String firstName) {
        return ResponseEntity.ok(clientService.searchByFirstName(firstName));
    }

    @GetMapping("/search/lastname/{lastName}")
    @Operation(summary = "Rechercher des clients par nom")
    public ResponseEntity<List<ClientResponse>> searchByLastName(@PathVariable String lastName) {
        return ResponseEntity.ok(clientService.searchByLastName(lastName));
    }

    @GetMapping("/search/email/{email:.+}")
    @Operation(summary = "Rechercher un client par email")
    public ResponseEntity<ClientResponse> searchByEmail(@PathVariable String email) {
        return ResponseEntity.ok(clientService.searchByEmail(email));
    }

    @GetMapping("/{id}/exists")
    @Operation(summary = "Verifier si un client existe")
    public ResponseEntity<ClientExistenceResponse> existsById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.existsById(id));
    }
}
