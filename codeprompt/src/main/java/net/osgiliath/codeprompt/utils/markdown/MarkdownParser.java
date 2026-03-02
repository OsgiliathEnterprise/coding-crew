package net.osgiliath.codeprompt.utils.markdown;

import dev.langchain4j.data.document.Document;

import java.nio.file.Path;
import java.util.List;

/**
 * Interface for parsing markdown files and extracting structured content.
 */
public interface MarkdownParser {

    /**
     * Lists all markdown files in a given folder.
     *
     * @param folderPath the path to the folder to scan
     * @return list of markdown file names
     */
    List<Path> listMarkdownFiles(Path folderPath);

    /**
     * Retrieves the content of a markdown file and parses it into a structured format.
     *
     * @param folderPath the path to the folder containing the file
     * @param fileName the name of the markdown file to retrieve
     * @return a MarkdownFile object containing headers and sections
     */
    MarkdownFile getMarkdownFile(Path folderPath, String fileName);

    /* Retrieves the headers from a markdown file.
     *
     * @return a MarkdownHeaders object containing the headers
     */
    MarkdownHeaders getHeaders();

    /*
     * Retrieves the consolidated main sections from a markdown file.
     *
     * @return list of main sections
     */
    List<MarkdownSection> getMainSections();

    /*
     * Retrieves the consolidated Sample sections from a markdown file.
     *
     * @return list of main sections
     */
    List<MarkdownSection> getSampleSections();

    /*
     * Retrieves the consolidated section or subsection with name
     * @param sectionName from a markdown file.
     * @return list of main sections
     */
    MarkdownSection getSection(String sectionName);
    Document toDocument(MarkdownFile markdownFile, boolean includeHeaders, boolean includeSections, boolean includeSamples);
}
