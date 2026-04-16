@RestController
@RequestMapping("/api/test")
public class HelloController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public HelloController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/send")
    public ResponseEntity<String> testConnection(@RequestBody String message) {
        kafkaTemplate.send("sportzone-test-topic", "Evento recebido do Front: " + message);
        return ResponseEntity.accepted().body("Mensagem enviada para o Kafka com sucesso!");
    }
}
