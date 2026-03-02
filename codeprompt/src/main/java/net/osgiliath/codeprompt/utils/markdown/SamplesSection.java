package net.osgiliath.codeprompt.utils.markdown;


import java.util.List;

/**
 * Represents a parsed Samples section from a markdown file.
 */
public class SamplesSection extends AbstractSection {
    public SamplesSection(String title, String content) {
        super(title, content, List.of());
    }
}

