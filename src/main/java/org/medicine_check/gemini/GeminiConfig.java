package org.medicine_check.gemini;

import com.google.genai.Client;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiConfig {

    public Client geminiClient() {
        return new Client();
    }

}
