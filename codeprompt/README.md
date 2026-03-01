# Code Prompt Framework

tA minimal **Agent Client Protocol (ACP)** JSON-RPC stdio server with AI integration for IntelliJ AI Assistant.

## ⚡ New: Koog Native ACP Support

**Good news!** You can replace the custom JSON-RPC code with Koog's native ACP features, reducing code by 60% and getting better integration.

📖 **[Read the Quick Start Guide](QUICKSTART-KOOG.md)** to migrate to Koog native features.

### Documentation

- **[QUICKSTART-KOOG.md](QUICKSTART-KOOG.md)** - 3-step quick start guide
- **[KOOG-NATIVE-MIGRATION.md](KOOG-NATIVE-MIGRATION.md)** - Complete migration guide with all code examples
- **[KOOG-NATIVE-SUMMARY.md](KOOG-NATIVE-SUMMARY.md)** - Benefits and architecture comparison

### Why Migrate to Koog?

| Current (Custom Java) | Koog Native (Kotlin) |
|----------------------|---------------------|
| 500 lines of code | 200 lines of code |
| Manual JSON-RPC | Automatic protocol handling |
| You maintain | JetBrains maintains |
| Basic ACP | Full Koog ecosystem |

**Java can seamlessly call Kotlin code**, so migration is straightforward!

---

## Current Implementation (LangChain4j)

The current implementation uses custom Java code with LangChain4j for AI.

## Features

- ✅ ACP-compliant JSON-RPC over stdio
- ✅ Handles `initialize`, `session/new`, and `session/prompt` methods
- ✅ Emits `session/update` notifications
- ✅ **LangChain4j integration** for AI-powered responses
- ✅ Falls back to echo/template mode when AI is not configured
- ✅ Spring Boot autoconfiguration

## Quick Start

### 1. Set Up API Key (Optional)

For AI-powered responses, configure OpenAI:

```bash
export OPENAI_API_KEY=your-api-key-here
```

Without an API key, the server operates in **echo mode**.

### 2. Run the Server

```bash
./gradlew :codeprompt:bootRun
```

The server reads JSON-RPC requests from stdin and writes responses to stdout.

## LangChain4j Integration

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# OpenAI Configuration (required for AI mode)
langchain4j.open-ai.chat-model.api-key=${OPENAI_API_KEY}
langchain4j.open-ai.chat-model.model-name=gpt-4o-mini
langchain4j.open-ai.chat-model.temperature=0.7
langchain4j.open-ai.chat-model.log-requests=true
langchain4j.open-ai.chat-model.log-responses=true

# Run LLM demo on startup (optional)
demo.llm.enabled=false
```

### How It Works

The `SimpleAssistant` interface is automatically implemented by Spring Boot using LangChain4j:

```java
@AiService
public interface SimpleAssistant {
    @SystemMessage("You are a helpful coding assistant specialized in Java, Spring Boot, and enterprise applications.")
    String chat(String userMessage);
}
```

The `JsonRpcHandler` automatically uses the AI assistant when:
- The `SimpleAssistant` bean is available (OPENAI_API_KEY is set)
- A prompt is received via `session/prompt`
- No template is specified

### Demo Mode

Enable the startup demo to test AI integration:

```bash
# In application.properties
demo.llm.enabled=true

# Then run
./gradlew :codeprompt:bootRun
```

## Using with IntelliJ AI Assistant

### Configure ACP Agent

Add to `~/.jetbrains/acp.json`:

```json
{
  "agent_servers": {
    "Code Prompt Framework": {
      "command": "/path/to/gradlew",
      "args": ["-p", "/path/to/erp", ":codeprompt:bootRun", "-q"],
      "env": [
        {"name": "OPENAI_API_KEY", "value": "your-key-here"}
      ]
    }
  }
}
```

### Select in IntelliJ

1. Open the AI Chat tool window
2. Click the agent selector dropdown
3. Choose "Code Prompt Framework"
4. Start chatting!

## API Examples

### Initialize Connection

```json
{"jsonrpc":"2.0","id":1,"method":"initialize"}
```

**Response:**
```json
{
  "jsonrpc":"2.0",
  "id":1,
  "result":{
    "serverInfo":{"name":"code-prompt-framework","version":"0.1.0"},
    "capabilities":{"session":{"update":true},"prompt":{}}
  }
}
```

### Create Session

```json
{"jsonrpc":"2.0","id":2,"method":"session/new"}
```

**Response:**
```json
{"jsonrpc":"2.0","method":"session/update","params":{"sessionId":"...","text":"Session created"}}
{"jsonrpc":"2.0","id":2,"result":{"sessionId":"...","createdAt":"2026-02-14T..."}}
```

### AI-Powered Prompt

```json
{
  "jsonrpc":"2.0",
  "id":3,
  "method":"session/prompt",
  "params":{
    "sessionId":"abc123",
    "prompt":"Explain Spring Boot dependency injection"
  }
}
```

**Response** (with AI):
```json
{
  "jsonrpc":"2.0",
  "method":"session/update",
  "params":{
    "sessionId":"abc123",
    "update":{
      "sessionUpdate":"agent_message_chunk",
      "content":{
        "type":"text",
        "text":"Spring Boot's dependency injection..."
      }
    }
  }
}
{
  "jsonrpc":"2.0",
  "id":3,
  "result":{
    "stopReason":"end_turn"
  }
}
```

### Template Mode (No AI)

```json
{
  "jsonrpc":"2.0",
  "id":4,
  "method":"session/prompt",
  "params":{
    "template":"Hello {{name}}, welcome to {{place}}!",
    "variables":{"name":"Ada","place":"Wonderland"}
  }
}
```

**Response:**
```json
{
  "jsonrpc":"2.0",
  "method":"session/update",
  "params":{
    "sessionId":"...",
    "update":{
      "sessionUpdate":"agent_message_chunk",
      "content":{
        "type":"text",
        "text":"Hello Ada, welcome to Wonderland!"
      }
    }
  }
}
{
  "jsonrpc":"2.0",
  "id":4,
  "result":{
    "stopReason":"end_turn"
  }
}
```

## Architecture

```
┌─────────────────────────────────────┐
│   IntelliJ AI Assistant (Client)   │
└──────────────┬──────────────────────┘
               │ JSON-RPC over stdio
               ▼
┌─────────────────────────────────────┐
│    StdioJsonRpcServer (Spring)      │
│  ┌───────────────────────────────┐  │
│  │    JsonRpcHandler             │  │
│  │  ┌─────────────────────────┐  │  │
│  │  │  SimpleAssistant (AI)   │  │  │
│  │  │  ↓                       │  │  │
│  │  │  LangChain4j            │  │  │
│  │  │  ↓                       │  │  │
│  │  │  OpenAI API             │  │  │
│  │  └─────────────────────────┘  │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

## Development

### Run Tests

```bash
./gradlew :codeprompt:test
```

### Build

```bash
./gradlew :codeprompt:build
```

### Interactive Testing

Use the included demo script:

```bash
./codeprompt/demo-acp.sh
```

Or manually pipe commands:

```bash
echo '{"jsonrpc":"2.0","id":1,"method":"initialize"}' | ./gradlew :codeprompt:bootRun -q
```

## Dependencies

- **Spring Boot 3.4.2** - Application framework
- **LangChain4j 1.11.0** - AI/LLM integration
- **LangChain4j Spring Boot Starter** - Autoconfiguration
- **LangChain4j OpenAI** - OpenAI model integration
- **Jackson** - JSON processing

## Troubleshooting

### AI Assistant Not Working

1. Verify `OPENAI_API_KEY` is set:
   ```bash
   echo $OPENAI_API_KEY
   ```

2. Check logs for errors:
   ```bash
   ./gradlew :codeprompt:bootRun
   # Look for "Using LangChain4j assistant" messages
   ```

3. Fallback behavior: If AI fails, server automatically falls back to echo mode

### IntelliJ Not Detecting Agent

1. Verify `acp.json` path and syntax
2. Restart IntelliJ IDE
3. Check agent appears in AI Chat selector

### Build Issues

Ensure Java 25 is available:
```bash
java -version  # Should show Java 25+
```

## License

Part of the ERP project.

## Example

Initialize:

```json
{"jsonrpc":"2.0","id":1,"method":"initialize"}
```

Create session:

```json
{"jsonrpc":"2.0","id":2,"method":"session/new"}
```

Prompt (echo):

```json
{"jsonrpc":"2.0","id":3,"method":"session/prompt","params":{"sessionId":"SESSION_ID","prompt":"Hello"}}
```

Prompt (template):

```json
{"jsonrpc":"2.0","id":4,"method":"session/prompt","params":{"template":"Hi {{name}}","variables":{"name":"Ada"}}}
```
