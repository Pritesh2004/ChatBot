package com.bluchink.ChatBot.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;

@Component
public class CommandLineChatbot  implements CommandLineRunner {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;


    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Chatbot CLI started. Type 'exit' to stop.");

        while (true) {
            System.out.print("You: ");
            String query = scanner.nextLine();

            if ("exit".equalsIgnoreCase(query)) {
                System.out.println("Chatbot terminated.");
                break;
            }

            String response = chatService.sendMessage(query, "cli-user");
            if (response == null){
                response = "No response received.";
                System.out.println("Bot: " + response);
                return;
            }
            try{
                JsonNode jsonNode = objectMapper.readTree(response);
                if (jsonNode.has("answer")) {
                    response =  jsonNode.get("answer").asText();
                    response = convertMarkdownToStyledText(response);
                }
                System.out.println("Bot: " + response);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        scanner.close();
    }

    private String convertMarkdownToStyledText(String markdown) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);

        TextRenderer renderer = new TextRenderer();
        return renderer.render(document);
    }

    // Custom Markdown to ANSI Text Renderer
    static class TextRenderer extends AbstractVisitor {
        private final StringBuilder output = new StringBuilder();

        @Override
        public void visit(StrongEmphasis strongEmphasis) {
            output.append("\033[1m"); // ANSI Bold
            visitChildren(strongEmphasis);
            output.append("\033[0m");
        }

        @Override
        public void visit(Emphasis emphasis) {
            output.append("\033[3m"); // ANSI Italic
            visitChildren(emphasis);
            output.append("\033[0m");
        }

        @Override
        public void visit(Paragraph paragraph) {
            visitChildren(paragraph);
            output.append("\n\n");
        }

        @Override
        public void visit(Text text) {
            output.append(text.getLiteral());
        }

        @Override
        public void visit(BulletList bulletList) {
            visitChildren(bulletList);
        }

        @Override
        public void visit(ListItem listItem) {
            output.append("• "); // Use bullet symbol for CLI
            visitChildren(listItem);
            output.append("\n");
        }

        public String render(Node node) {
            node.accept(this);
            return output.toString();
        }
    }
}
