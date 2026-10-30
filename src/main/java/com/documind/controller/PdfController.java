package com.documind.controller;

import com.documind.service.PdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping(
        value = "/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Map<String, Object> uploadPdf(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Please select a PDF file."
            );
        }

        String filename = file.getOriginalFilename();

        if (filename == null ||
                !filename.toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Only PDF files are accepted."
            );
        }

        try {
            String text = pdfService.extractText(file);
            int pages = pdfService.getPageCount(file);

            return Map.of(
                "filename", filename,
                "pageCount", pages,
                "characterCount", text.length(),
                "extractedText", text
            );

        } catch (IOException e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Unable to read this PDF. Check that it is valid and not password-protected."
            );
        }
    }
}

