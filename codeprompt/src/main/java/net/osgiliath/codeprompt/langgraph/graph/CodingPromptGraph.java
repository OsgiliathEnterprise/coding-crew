package net.osgiliath.codeprompt.langgraph.graph;

import net.osgiliath.acplanggraphlangchainbridge.langgraph.graph.PromptGraph;
import net.osgiliath.codeprompt.langgraph.edge.LLMToToolEdge;
import net.osgiliath.codeprompt.langgraph.node.AttachmentFilterNode;
import net.osgiliath.codeprompt.langgraph.node.AttachmentUnwrapperNode;
import net.osgiliath.codeprompt.langgraph.node.LLMProcessorNode;
import net.osgiliath.codeprompt.langgraph.state.CodingPromptState;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.bsc.langgraph4j.GraphDefinition.END;
import static org.bsc.langgraph4j.GraphDefinition.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Component
public class CodingPromptGraph implements PromptGraph {
    private final LLMProcessorNode llmProcessorNode;
    private final LLMToToolEdge edge;
    private final AttachmentFilterNode attachmentFilterNode;
    private final AttachmentUnwrapperNode attachmentUnwrapperNode;

    public CodingPromptGraph(LLMProcessorNode llmProcessorNode,
                              LLMToToolEdge edge, AttachmentFilterNode attachmentFilterNode,
                              AttachmentUnwrapperNode attachmentUnwrapperNode) {
       this.attachmentFilterNode = attachmentFilterNode;
        this.attachmentUnwrapperNode = attachmentUnwrapperNode;
        this.llmProcessorNode = llmProcessorNode;
        this.edge = edge;
    }

    @Override
    public StateGraph buildGraph() throws GraphStateException {
        return new StateGraph<>(CodingPromptState.SCHEMA, CodingPromptState.codePromptSerializer())
                .addNode("filter", node_async(attachmentFilterNode))
                .addNode("unwrapper", node_async(attachmentUnwrapperNode))
                .addNode("agent", node_async(llmProcessorNode::apply))
                .addEdge(START, "filter")
                .addEdge("filter", "unwrapper")
                .addEdge("unwrapper", "agent")
                .addConditionalEdges("agent",
                        edge_async(edge::apply),
                        Map.of("next", "agent", "exit", END));
    }
}
