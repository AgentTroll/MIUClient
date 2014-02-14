import netio.ConnectionFactory;

public class MIUClient {
    public static void main(String[] args) throws Throwable {
        ConnectionFactory.newConnection("127.0.0.1");
    }
}