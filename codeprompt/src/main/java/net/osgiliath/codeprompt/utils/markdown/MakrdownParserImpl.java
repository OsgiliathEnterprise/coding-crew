package net.osgiliath.codeprompt.utils.markdown;

import dev.langchain4j.data.document.Document;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.List;

@Component
public class MakrdownParserImpl implements MarkdownParser {
    @Override
    public List<Path> listMarkdownFiles(Path folderPath) {
        return List.of();
    }

    @Override
    public MarkdownFile getMarkdownFile(Path folderPath, String fileName) {
        return null;
    }

    @Override
    public MarkdownHeaders getHeaders() {
        return null;
    }

    @Override
    public List<MarkdownSection> getMainSections() {
        return List.of();
    }

    @Override
    public List<MarkdownSection> getSampleSections() {
        return List.of();
    }

    @Override
    public MarkdownSection getSection(String sectionName) {
        return null;
    }

    @Override
    public Document toDocument(MarkdownFile markdownFile, boolean includeHeaders, boolean includeSections, boolean includeSamples) {
        return null;
    }
}
