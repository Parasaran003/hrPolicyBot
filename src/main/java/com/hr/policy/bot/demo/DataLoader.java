package com.hr.policy.bot.demo;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final VectorStore vectorStore;
    // Create an hr-policy.txt file in src/main/resources/
    private final Resource policyDoc = new ClassPathResource("hr-policy.txt");

    public DataLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(String... args) {
        TextReader textReader = new TextReader(policyDoc);
        List<Document> documents = textReader.get();
        TokenTextSplitter splitter = TokenTextSplitter.builder().build();
        List<Document> splitDocs = splitter.apply(documents);
        
        vectorStore.add(splitDocs);
    }
}