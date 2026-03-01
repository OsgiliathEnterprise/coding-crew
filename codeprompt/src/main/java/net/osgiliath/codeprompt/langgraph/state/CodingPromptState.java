package net.osgiliath.codeprompt.langgraph.state;

import net.osgiliath.acplanggraphlangchainbridge.langgraph.serializer.AcpLangChain4jStateSerializer;
import net.osgiliath.acplanggraphlangchainbridge.langgraph.state.ChatState;
import org.bsc.langgraph4j.serializer.StateSerializer;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodingPromptState extends ChatState {

    public static final String ATTACHMENTS = "attachments";

    public static final Map<String, Channel<?>> SCHEMA;

    static {
        Map<String, Channel<?>> map = new HashMap<>(ChatState.SCHEMA);
        map.put(ATTACHMENTS, Channels.appender(ArrayList::new));
        SCHEMA = Map.copyOf(map);
    }

    public CodingPromptState(Map<String, Object> initData) {
        super(initData);
    }

    public static StateSerializer<CodingPromptState> codePromptSerializer() {
        return new AcpLangChain4jStateSerializer<>(CodingPromptState::new);
    }

    public List<byte[]> attachments() {
        return this.<List<byte[]>>value(ATTACHMENTS).orElse(List.of());
    }

}
