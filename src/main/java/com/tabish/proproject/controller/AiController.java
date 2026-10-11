package com.tabish.proproject.controller;

import com.tabish.proproject.constants.AppConstants;
import com.tabish.proproject.dto.request.AiPromptRequest;
import com.tabish.proproject.dto.response.ApiResponse;
import com.tabish.proproject.dto.response.AiResponse;
import com.tabish.proproject.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Tag(name = "AI Services", description = "Endpoints for interacting with Spring AI models")
@SecurityRequirement(name = "Bearer Authentication")
public class AiController {

    private final AiService aiService;

    @PostMapping("/generate")
    @Operation(summary = "Generate text response using AI model")
    public ResponseEntity<ApiResponse<AiResponse>> generateText(@Valid @RequestBody AiPromptRequest promptRequest) {
        AiResponse response = aiService.generateText(promptRequest);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.AI_RESPONSE_SUCCESS, response));
    }

    @GetMapping("/status")
    @Operation(summary = "Check AI service status")
    public ResponseEntity<ApiResponse<String>> getAiStatus() {
        return ResponseEntity.ok(ApiResponse.success("AI Service is active and ready"));
    }
}

