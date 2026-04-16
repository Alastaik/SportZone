@Component
public class TestConsumer {

    @KafkaListener(topics = "sportzone-test-topic", groupId = "sportzone-group")
    public void listen(String message) {
        System.out.println("LOG KAFKA: Mensagem consumida com sucesso: " + message);
    }
}
