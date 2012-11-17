import java.util.*;

public class Main {

    public static void main (String args[]) {
        EmailSender emailer = new EmailSender();
        emailer.connect("goldberg.chain.reaction", "n0n0n0n0",
                        "goldberg.chain.reaction@gmail.com", "imap.gmail.com",
                        465);
        try{
            emailer.send("michaelv03@gmail.com", "Test", "Hello testing");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
                      
