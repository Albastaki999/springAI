//import org.springframework.ai.ollama.OllamaChatClient;
//import org.springframework.stereotype.Service;
//import java.util.Base64;
//
//@Service
//public class OllamaService {
//
//    private final OllamaChatClient ollamaChatClient;
//
//    public OllamaService(OllamaChatClient ollamaChatClient) {
//        this.ollamaChatClient = ollamaChatClient;
//    }
//
//    public String generateImage(String prompt) {
//        // Call Ollama and get the base64 image response
//        String base64Image = ollamaChatClient.call(prompt);
//        return "data:image/png;base64," + base64Image;
//    }
//}
