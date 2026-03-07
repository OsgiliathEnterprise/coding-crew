package net.osgiliath.codeprompttests.configuration;


import com.openai.models.ChatModel;
import dev.langchain4j.model.chat.Capability;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openaiofficial.OpenAiOfficialChatModel;
import dev.langchain4j.model.openaiofficial.OpenAiOfficialStreamingChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashSet;
import java.util.Set;

import static dev.langchain4j.model.chat.Capability.RESPONSE_FORMAT_JSON_SCHEMA;

/**
 * Optional test-only configuration for GitHub Models runs.
 *
 * Kept out of the production package namespace to avoid overriding
 * the main ChatModelConfiguration during default local tests.
 */
@TestConfiguration
@Profile("github")
public class ChatModelConfiguration {

    @Bean("primaryChatModel")
    public OpenAiOfficialChatModel model() {
        Set<Capability> capabilities = new HashSet<>();
        capabilities.add(RESPONSE_FORMAT_JSON_SCHEMA);
        return OpenAiOfficialChatModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .modelName(ChatModel.GPT_5_NANO)
                .isGitHubModels(true)
                .apiKey(System.getenv("MODEL_TOKEN"))
                .strictJsonSchema(true)
                .supportedCapabilities(capabilities)
        .strictTools(true)
        .build();
    }
    @Bean("primaryStreamingChatModel")
    public OpenAiOfficialStreamingChatModel streamingModel() {
        return OpenAiOfficialStreamingChatModel.builder()
                .baseUrl("https://models.inference.ai.azure.com")
                .modelName(ChatModel.GPT_5_NANO)
                .isGitHubModels(true)
                .apiKey(System.getenv("MODEL_TOKEN"))
        .strictTools(true)
        .build();
    }
}
