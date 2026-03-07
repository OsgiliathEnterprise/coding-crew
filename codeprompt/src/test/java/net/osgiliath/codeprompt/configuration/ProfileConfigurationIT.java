package net.osgiliath.codeprompt.configuration;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openaiofficial.OpenAiOfficialChatModel;
import dev.langchain4j.model.openaiofficial.OpenAiOfficialStreamingChatModel;
import net.osgiliath.codeprompt.CodePromptFrameworkApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify correct profile configuration for chat models.
 */
@SpringBootTest(classes = CodePromptFrameworkApplication.class, properties = {
    "spring.main.web-application-type=none"
})
public class ProfileConfigurationIT {

        // Mock CommandLineRunners to prevent them from starting and blocking stdin
    @MockitoBean
    private CommandLineRunner commandLineRunner;

    @Autowired
    private Environment environment;

    @Autowired
    @Qualifier("primaryChatModel")
    private ChatModel chatModel;

    @Autowired
    @Qualifier("primaryStreamingChatModel")
    private StreamingChatModel streamingChatModel;

    @Test
    void verifyChatModelBeansExist() {
        assertThat(chatModel).isNotNull();
        assertThat(streamingChatModel).isNotNull();

        String[] activeProfiles = environment.getActiveProfiles();
        String profileInfo = activeProfiles.length > 0 ? String.join(",", activeProfiles) : "default";

        System.out.println("===== PROFILE CONFIGURATION TEST =====");
        System.out.println("Active profiles: " + profileInfo);
        System.out.println("ChatModel class: " + chatModel.getClass().getName());
        System.out.println("StreamingChatModel class: " + streamingChatModel.getClass().getName());

        if (profileInfo.contains("github")) {
            assertThat(chatModel).isInstanceOf(OpenAiOfficialChatModel.class);
            assertThat(streamingChatModel).isInstanceOf(OpenAiOfficialStreamingChatModel.class);
            System.out.println("✅ GitHub profile: Using OpenAiOfficialChatModel");
        } else {
            System.out.println("✅ Default profile: Using auto-configured model");
        }
        System.out.println("======================================");
    }
}

