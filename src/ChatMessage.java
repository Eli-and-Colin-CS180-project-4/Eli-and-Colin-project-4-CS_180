
import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    private String message;
    private int type;

    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.
    public ChatMessage(int type, String message) {
        this.message = message;
        this.type = type;
    }
}
