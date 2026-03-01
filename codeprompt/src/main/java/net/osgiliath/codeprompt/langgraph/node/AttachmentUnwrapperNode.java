package net.osgiliath.codeprompt.langgraph.node;

import net.osgiliath.acplanggraphlangchainbridge.langgraph.message.ResourceLinkContent;
import net.osgiliath.codeprompt.langgraph.state.CodingPromptState;
import org.bsc.langgraph4j.action.NodeAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AttachmentUnwrapperNode implements NodeAction<CodingPromptState> {
    private static final Logger log = LoggerFactory.getLogger(AttachmentUnwrapperNode.class);

    public AttachmentUnwrapperNode() {
    }

    @Override
    public Map<String, Object> apply(CodingPromptState state) throws IOException {
        log.debug("Filtering metadata for question: {}, with attachments: {}", state.messages(), state.attachmentsMetadata());
        List<byte[]> attachments = new ArrayList<>();

        for (ResourceLinkContent metadata : state.attachmentsMetadata()) {
            log.debug("Evaluating attachment metadata: {}", metadata);
            URI filePath = metadata.uri();
            Path path = Paths.get(filePath);

            byte[] read = Files.readAllBytes(path);
            attachments.add(read);
        }
        return Map.of(
                CodingPromptState.ATTACHMENTS, attachments
        );
    }
}
