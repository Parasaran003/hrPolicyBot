package com.hr.policy.bot.demo.Service;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HrChatService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public HrChatService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @Cacheable(value = "chatResponses", key = "#question")
    public String askQuestion(String question) {
        // 1. Retrieve the top 3 most relevant document chunks
        List<Document> similarDocuments = vectorStore.similaritySearch(question);

        String context = similarDocuments.stream()
            .map(Document::getText)
                .collect(Collectors.joining(System.lineSeparator()));

        String systemPrompt = """
            You are a helpful HR assistant. Answer the user's question based ONLY on the provided HR policy context.
            If the answer is not in the context, politely say that you don't have that information.
                Keep answers concise and directly relevant. Format answers in clear Markdown, using short headings or bullets when useful.
                Do not reproduce the full policy context unless the user asks for it.
            
            CONTEXT:
            {context}
            """;

        // 2. Augment and Generate
        return chatClient.prompt()
                .system(s -> s.text(systemPrompt).param("context", context))
                .user(question)
                .call()
                .content();
    }
}