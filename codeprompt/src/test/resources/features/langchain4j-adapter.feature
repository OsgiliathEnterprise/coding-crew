Feature: LangChain4j Adapter Processing
  As an ACP agent support system
  I want to process prompts through the LangChain4j adapter
  So that I can provide AI-powered responses to client requests

  Background:
    Given the CodePrompt application is initialized
    And the LangChain4j adapter is available

  Scenario: Process a simple prompt successfully
    Given I have a prompt "Hello AI, please respond with exactly the word 'ACK'"
    When I process the prompt through the adapter
    Then I should receive a response
    And the response should contain "ACK"
    And the processing should complete without errors

  Scenario: Handle empty prompt gracefully
    Given I have an empty prompt
    When I process the prompt through the adapter
    Then I should receive a response "Please provide a prompt."
    And the processing should complete without errors

  Scenario: Handle blank prompt gracefully
    Given I have a blank prompt "   "
    When I process the prompt through the adapter
    Then I should receive a response "Please provide a prompt."
    And the processing should complete without errors

  Scenario: Process a complex multi-line prompt
    Given I have a multi-line prompt
      """
      Please analyze the following:
      1. What is the ACP protocol?
      2. How does it work?
      """
    When I process the prompt through the adapter
    Then I should receive a response
    And the response should not be empty
    And the processing should complete without errors

  Scenario: Verify adapter streaming capability
    Given I have a prompt "Count from 1 to 5"
    When I process the prompt through the adapter
    Then I should receive multiple tokens
    And the processing should complete without errors

