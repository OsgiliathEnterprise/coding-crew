Feature: ACP ResourceLinks Attachment Support
  As an ACP server with LangChain4j adapter
  I want to support ResourceLinks (file references) in prompts and responses
  So that users can reference files and resources with AI capabilities

  Background:
    Given the ACP bridge is initialized
    And the LangChain4j adapter is available
    And the attachment support system is initialized
    And the test dataset directory exists at "src/test/resources/dataset"

  # Scenario 1: Basic ResourceLink handling
  Scenario: Bridge accepts and processes a single ResourceLink with a prompt
    Given an active ACP session
    And The user asks "Analyze the attached code file"
    And I have a ResourceLink pointing to file "file://src/test/resources/dataset/Thread.java" with name "Thread.java"
    And the ResourceLink has mimeType "text/java"
    When the client sends the prompt with the ResourceLink
    Then the bridge should receive the ResourceLink
    And the ResourceLink should be added to the graph state
    And the graph state contains attachments metadata with the ResourceLink name
    And the prompt should be processed with attachment context
    And the stream processing completes successfully

  # Scenario 2: Multiple ResourceLinks in single request
  Scenario: Bridge accepts and forwards multiple ResourceLinks
    Given an active ACP session
    And The user asks "Analyze these attached files"
    And I have ResourceLinks pointing to dataset files:
      | file                                          | name              | mimeType         |
      | file://src/test/resources/dataset/Thread.java        | Thread.java       | text/java        |
      | file://src/test/resources/dataset/config.json        | config.json       | application/json |
      | file://src/test/resources/dataset/test-image.png     | test-image.png    | image/png        |
    When the client sends the prompt with all ResourceLinks
    Then the adapter receives all 3 ResourceLinks
    And the graph state contains all 3 attachment metadata entries
    And each ResourceLink retains its name and URI
    And all attachments should be processed
    And the stream processing completes successfully

  # Scenario 3: ResourceLink with complete metadata
  Scenario: ResourceLink with all metadata fields is preserved
    Given an active ACP session
    And The user asks "Process the configuration file"
    And I have a ResourceLink pointing to file "file://src/test/resources/dataset/config.json"
    And the ResourceLink has name "prod-config.json"
    And the ResourceLink has description "Production configuration settings"
    And the ResourceLink has mimeType "application/json"
    When the client sends the prompt with the ResourceLink
    Then the adapter receives the ResourceLink
    And the ResourceLink name is preserved as "prod-config.json"
    And the ResourceLink description is preserved as "Production configuration settings"
    And the ResourceLink mimeType is preserved as "application/json"
    And the stream processing completes successfully

  # Scenario 4: ResourceLinks are available in graph state
  Scenario: ResourceLinks are injected into graph state during execution
    Given an active ACP session
    And The user asks "Use the attachment metadata"
    And I have a ResourceLink with name "config.json" and URI "file://src/test/resources/dataset/config.json"
    When the client sends the prompt with the ResourceLink
    And the LangChain4j adapter processes the prompt with attachment
    Then the graph state property "attachmentsMeta" contains the ResourceLink
    And the graph nodes can access the ResourceLink from the state
    And the ResourceLink is available throughout the graph execution
    And attachment context should be maintained throughout streaming
    And the stream processing completes successfully

  # Scenario 6: HTTP URI scheme handling
  Scenario: ResourceLink with http:// URI scheme is processed
    Given an active ACP session
    And The user asks "Fetch and analyze the remote resource"
    And I have a ResourceLink with URI "http://example.com/api/resource.json"
    And the ResourceLink has mimeType "application/json"
    When the client sends the prompt with the ResourceLink
    Then the adapter preserves the http URI scheme
    And the URI remains "http://example.com/api/resource.json"
    And the mimeType is available to graph nodes
    And the stream processing completes successfully

  # Scenario 7: Archive reference handling
  Scenario: ResourceLink pointing into a ZIP archive is processed
    Given an active ACP session
    And The user asks "Analyze Java source from SDK"
    And I have a ResourceLink with name "Thread.java"
    And the ResourceLink has URI "file://src/test/resources/dataset/test-archive.zip!/Thread.java"
    And the ResourceLink has mimeType "text/java"
    When the client sends the prompt with the ResourceLink
    Then the adapter receives the ResourceLink with nested archive path
    And the URI contains the ZIP file path and internal entry path
    And the adapter preserves the complete URI with !/ separator
    And the stream processing completes successfully

  # Scenario 8: ResourceLink integrity preservation
  Scenario: Adapter preserves ResourceLink integrity without modification
    Given an active ACP session
    And The user asks "Process with resource integrity check"
    And I have 2 ResourceLinks with specific properties:
      | name         | uri                                           | mimeType         |
      | Thread.java  | file://src/test/resources/dataset/Thread.java | text/java        |
      | config.json  | file://src/test/resources/dataset/config.json | application/json |
    When the client sends the prompt with the ResourceLinks
    Then the adapter does not modify the ResourceLink names
    And the adapter does not modify the ResourceLink URIs
    And the adapter does not modify the ResourceLink mimeTypes
    And the ResourceLinks are passed to the graph state unmodified
    And the stream processing completes successfully

  # Scenario 9: Assistant processes attachments
  Scenario: JavaSpringBootAssistant reads ResourceLink attachment data
    Given an active ACP session
    And the JavaSpringBootAssistant is initialized
    And The user asks "If you succeed to read the attached Java file, answer exactly this sentence: 'content attachment considered'"
    And I have a ResourceLink with name "Thread.java"
    And the ResourceLink has URI "file://src/test/resources/dataset/Thread.java"
    And the ResourceLink has description "Java Thread class implementation"
    When the Assistant receives a prompt with the ResourceLink
    Then the graph state contains the ResourceLink metadata
    And the agent nodes can access the ResourceLink information
    And the assistant should take into consideration the attachment content in its response
    And the stream response may reference the attached resource
    And the stream processing completes successfully

  # Scenario 10: Empty ResourceLink list handling
  Scenario: Bridge handles prompts without ResourceLinks gracefully
    Given an active ACP session
    And The user asks "Simple prompt without attachments"
    And I have an empty ResourceLink list
    When the client sends the prompt with empty ResourceLink list
    Then the adapter processes the prompt normally
    And the graph state attachments metadata is empty
    And the stream processing completes successfully
    And the response is generated without errors

  # Scenario 11: Minimal ResourceLink fields
  Scenario: ResourceLink with minimal fields (name and uri) is processed
    Given an active ACP session
    And The user asks "Minimal resource link test"
    And I have a ResourceLink with only:
      | field | value                                         |
      | name  | config.json                                   |
      | uri   | file://src/test/resources/dataset/config.json |
    When the client sends the prompt with the ResourceLink
    Then the adapter accepts the minimal ResourceLink
    And the ResourceLink name and uri are preserved
    And null fields are handled gracefully
    And the stream processing completes successfully

  # Scenario 12: ResourceLink logging and debugging
  Scenario: ResourceLink details are logged for debugging
    Given an active ACP session
    And The user asks "Test logging of ResourceLink"
    And I have a ResourceLink with name "Thread.java"
    And the ResourceLink has URI "file://src/test/resources/dataset/Thread.java"
    When the client sends the prompt with the ResourceLink
    And the adapter processes the request
    Then the adapter logs the ResourceLink name
    And the adapter logs the ResourceLink URI
    And the adapter logs the total number of ResourceLinks
    And the stream processing completes successfully

  # Scenario 13: ResourceLink metadata persistence
  Scenario: Persist ResourceLink metadata to database
    Given an active ACP session
    And The user asks "Save attachment metadata"
    And I have a ResourceLink with name "test-document.pdf"
    And the ResourceLink has URI "file://src/test/resources/dataset/test-document.pdf"
    And the ResourceLink has mimeType "application/pdf"
    When the client sends the prompt with the ResourceLink
    Then the bridge should receive and store the attachment
    And the metadata includes name, URI, mimeType, and description
    And the stream processing completes successfully

  # Scenario 14: Follow-up prompts with ResourceLink reference
  Scenario: Support ResourceLink reference in follow-up prompts
    Given an active ACP session
    And I have an initial prompt "Analyze this code"
    And I have a ResourceLink with name "file://src/test/resources/dataset/Thread.java" that was previously sent
    When the client sends a follow-up prompt "What design patterns are used in Thread.java?"
    Then the system should retrieve and use the ResourceLink context
    And the ResourceLink remains available in the session
    And the stream processing completes successfully

  # Scenario 15: ResourceLink streaming integration
  Scenario: ResourceLinks are available before streaming begins
    Given an active ACP session
    And The user asks "Stream response with resource context"
    And I have a ResourceLink with name "config.json"
    And I have a TokenConsumer that collects streamed tokens
    And the ResourceLink has URI "file://src/test/resources/dataset/config.json"
    When the client calls streamPrompt with the prompt and ResourceLink
    Then the ResourceLink is available to the graph before first token
    And the adapter injects ResourceLink into state before streaming
    And tokens are streamed with resource context available
    And the TokenConsumer receives all tokens
    And the stream completes successfully

  # Scenario 16: Bridge method signature with ResourceLinks
  Scenario: AcpSessionBridge.streamPrompt accepts ResourceLinks parameter
    Given the ACP bridge session is created
    When I inspect the streamPrompt method signature
    Then the method accepts: String promptText
    And the method accepts: List<ContentBlock.ResourceLink> resourceLinks
    And the method accepts: TokenConsumer consumer
    And all parameters are properly forwarded to the adapter

  # Scenario 17: Easter Egg - LLM reads file content
  Scenario: LLM extracts secret code from attached Java file
    Given an active ACP session
    And The user asks "What is the secret code in the attached file?"
    And I have a ResourceLink pointing to file "file://src/test/resources/dataset/Thread.java"
    And the ResourceLink has mimeType "text/java"
    When the client sends the prompt with the ResourceLink
    And the LLM processes the attached file content
    Then the LLM response should contain "CUCUMBER_BDD_ROCKS_2026"
    And this validates that file content is being read by the AI
    And the stream processing completes successfully

  # Scenario 18: PDF document attachment
  Scenario: Bridge handles PDF document attachments
    Given an active ACP session
    And The user asks "Analyze the attached PDF document"
    And I have a ResourceLink pointing to file "file://src/test/resources/dataset/test-document.pdf"
    And the ResourceLink has mimeType "application/pdf"
    When the client sends the prompt with the ResourceLink
    Then the bridge should receive the PDF ResourceLink
    And the ResourceLink should be added to the graph state
    And the mimeType "application/pdf" is preserved
    And the stream processing completes successfully

  # Scenario 19: Video file attachment
  Scenario: Bridge handles video file attachments
    Given an active ACP session
    And The user asks "Process the attached video file"
    And I have a ResourceLink pointing to file "file://src/test/resources/dataset/test-video.webm"
    And the ResourceLink has mimeType "video/webm"
    When the client sends the prompt with the ResourceLink
    Then the bridge should receive the video ResourceLink
    And the ResourceLink should be added to the graph state
    And the mimeType "video/webm" is preserved
    And the stream processing completes successfully

  # Scenario 20: ZIP archive with nested path reference
  Scenario: Bridge handles archive references with nested paths
    Given an active ACP session
    And The user asks "Extract and analyze file from archive"
    And I have a ResourceLink with name "archive-content.txt"
    And the ResourceLink has URI "file://src/test/resources/dataset/test-archive.zip!/archive-content.txt"
    And the ResourceLink has mimeType "text/plain"
    When the client sends the prompt with the ResourceLink
    Then the adapter receives the ResourceLink with archive path
    And the URI contains the ZIP file path and internal entry path
    And the adapter preserves the complete URI with !/ separator
    And the nested path "archive-content.txt" is accessible
    And the stream processing completes successfully

  # Scenario 21: Java AST parsing capability
  Scenario: System can parse Java file for AST analysis
    Given an active ACP session
    And The user asks "Parse the attached Java file and identify all method names"
    And I have a ResourceLink pointing to file "file://src/test/resources/dataset/Thread.java"
    And the ResourceLink has mimeType "text/java"
    When the client sends the prompt with the ResourceLink
    And the system performs AST parsing on the Java file
    Then the AST parser should identify class "Thread"
    And the AST parser should identify methods like "start", "run", "getName"
    And the parsed structure should be available to the LLM
    And the stream processing completes successfully

