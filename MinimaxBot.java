import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MinimaxBot {
    //create a list to store all the words
    public static List<String> loadWords(String text){
        List<String> words = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(text))){
            String line;
            while((line=br.readLine())!=null){
                String trimmed = line.substring(1, 6);
                words.add(trimmed);
            }
        }
        catch (IOException e){
            System.out.println("error");
        }
        return words;
    }

    public static String guessEval(String answer, String guess){
        char[] result = new char[5];
        boolean[] used = new boolean[5];
        //for greens
        for(int i = 0; i<5;i++){ 
            if(answer.charAt(i)==guess.charAt(i)){
                result[i]='2';
                used[i] = true;
            }
        }
        //for yellows + greys
        for(int i = 0; i<5; i++){ // i represents guess letter
            if (result[i]=='2') continue;
            char letter = guess.charAt(i);
            boolean yellow = false;
            for (int j = 0; j<5; j++){ //j represents answer letter
                if(used[j]==false && answer.charAt(j)==letter){
                    result[i] = '1';
                    used[j] = true;
                    yellow = true;
                    break;
                }
            }
            if(!yellow){
                result[i] = '0'; //gray
            }
        }
        String Str = new String(result);
        return Str;
    }

    public static String minimax(int guessNumber, int nodeIndex, boolean isMax, String[] possibleAnswers, int h){
        /**Pseudocode
         * minimax(){
         * 
         * if we've reached the max depth of the tree just return the raw value
         * 
         * if it's max's turn we want the largest possible value (run recursively)
         * 
         * else if it's min's turn we want the smallest possible value (run recursively)
         * }
         * Honestly, I'm still not completely sure which value we want to min and max
         * I'm thinking bits of information gained -log2(p)
         * So, we're starting the code from the top but building up from the bottom
         * */
    }
    public static void main(String[] args){
        List<String> possibleMatches = loadWords("OfficialAnswers.txt"); //matches = leaves

    }
}
/**Principles:
 * we want an algorithm that minimizes moves for the WORST POSSIBLE SCENARIO
 * maximized information (bits)--> minimized moves
 * Game is trying to minimize information
 * 
 * If maximizer (me) has upperhand, board has + value
 * minimizer has upperhand, - value
 * 
 * So in the example, scores[] are leaves of game tree
 * for us, the possible answers are gonna be the leaves
 * The program will check each word and see possible answers after the guess, and so on (recursive)
 * 
 * EXAMPLE
 static int minimax(int depth, int nodeIndex, boolean  isMax,
            int scores[], int h)
    depth = current depth of tree
        number of guesses
    nodeIndex = index of current node in scores[]
        the node is the player who's either minning or maxxing
        So this is the player's position (index) within the possible matches
    isMax = true during maximizers turn
    scores[] = "leaves" of game tree
    h = max height of tree

    
 */