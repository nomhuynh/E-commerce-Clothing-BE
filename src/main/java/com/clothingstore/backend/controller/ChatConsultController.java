package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.chat.ChatConsultRequest;
import com.clothingstore.backend.dto.chat.ChatConsultResponse;
import com.clothingstore.backend.service.chat.ChatConsultService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatConsultController {

    private final ChatConsultService chatConsultService;

    @PostMapping("/consult")
    public ResponseEntity<ApiResponse<ChatConsultResponse>> consult(@Valid @RequestBody ChatConsultRequest request) {
        ChatConsultResponse data = chatConsultService.consult(request);
        return ResponseEntity.ok(ApiResponse.success("OK", data));
    }
}
