import java.io.*;
import java.util.ArrayList;

public class ChatFilter {
    BufferedReader br;
    ArrayList<String> badWords = new ArrayList<>();
    /**
     * The ChatFilter constructor.
     * The constructor takes in a filepath that leads to the badWordsFileName
     * and attempts to create a buffered reader that reads from that file.
     * If the filepath does not exist, a FileNotFoundException will be thrown.
     * @param badWordsFileName the filepath that contains the words to be censored.
     * @throws FileNotFoundException
     */
    public ChatFilter(String badWordsFileName) throws FileNotFoundException {
        //Check if the filepath exists
        if (!new File(badWordsFileName).exists()) {
            throw new FileNotFoundException("Error: file specified in the the creation of the server does not exist!");
        } else {
            br = new BufferedReader(new FileReader(badWordsFileName));
        }

        //The array that will store all the bad words
        String line;
        try {
            //Create an ArrayList containing all the bad words in the badWordsFile.
            while ((line = br.readLine()) != null) {
                badWords.add(line);
            }
        } catch (IOException e) {
            //If there ends up being some error in reading the file
            System.out.println("Error in reading process! A null value will be returned");
        }

    }

    /**
     * The filter method.
     * This method takes in a message as a parameter, and then censors all
     * instances of words from the badWords
     * file.
     * If no words were censored, then the normal string is returned
     * @param msg the message to be censored.
     * @return the censored message.
     */
    public String filter(String msg) {

        //Begin checking for bad words
        for (String temp: badWords) {
            if (msg.contains(temp)) {
                String replaceString = "";
                for (int i = 0; i < temp.length(); i++) {
                    replaceString += "*";
                }
              msg = msg.replaceAll(temp, replaceString);
            }
        }
        return msg;
    }
}
