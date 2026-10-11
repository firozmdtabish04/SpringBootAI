package com.tabish.proproject.service;

import com.tabish.proproject.dto.request.AiPromptRequest;
import com.tabish.proproject.dto.response.AiResponse;

public interface AiService {

    AiResponse generateText(AiPromptRequest promptRequest);
}

