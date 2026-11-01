package com.weddinglage.web;

import com.weddinglage.service.ExportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/exports")
public class AdminExportController {

    private final ExportService exportService;

    public AdminExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping(value = "/guests", produces = "text/csv")
    public ResponseEntity<String> guests() {
        return csv("guests.csv", exportService.exportGuests());
    }

    @GetMapping(value = "/catering", produces = "text/csv")
    public ResponseEntity<String> catering() {
        return csv("catering.csv", exportService.exportCatering());
    }

    @GetMapping(value = "/transport", produces = "text/csv")
    public ResponseEntity<String> transport() {
        return csv("transport.csv", exportService.exportTransport());
    }

    @GetMapping(value = "/songs", produces = "text/csv")
    public ResponseEntity<String> songs() {
        return csv("songs.csv", exportService.exportSongs());
    }

    @GetMapping(value = "/ornaments", produces = "text/csv")
    public ResponseEntity<String> ornaments() {
        return csv("ornaments.csv", exportService.exportOrnaments());
    }

    private ResponseEntity<String> csv(String filename, String body) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(new MediaType("text", "csv"))
                .body(body);
    }
}
