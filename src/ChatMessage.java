
import java.io.Serializable;

final class ChatMessage implements Serializable {
    private static final long serialVersionUID = 6898543889087L;
    int num;
    String str;
    String recipient;


    // Here is where you should implement the chat message object.
    // Variables, Constructors, Methods, etc.
    public ChatMessage(int num, String str){
        this.str = str;
        this.num = num;
    }

    public ChatMessage(int num, String str, String recipient) {
        this.str = str;
        this.num = num;
        this.recipient = recipient;
    }

    public int getNum() {
        return num;
    }

    public String getStr() {
        return str;
    }

    public String getRecipient() {return recipient;}

}
