import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

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

    public static double calculateAvgBits(String guess, List<String> wordsRemaining){
        List<String> possibleMatches = loadWords("OfficialAnswers.txt");
        double total = wordsRemaining.size()+0.0;
        //count occurences of result pattern
        Map<String, Integer> patternCounts = new HashMap<>();
        for (String solution: possibleMatches){ //we want to check outcome for every solution
            String pattern = guessEval(solution, guess);
            patternCounts.put(pattern, patternCounts.getOrDefault(pattern, 0)+1);
        }
        // calculate bits + plug into formula
        double entropy=0.0;
        for (int count: patternCounts.values()){ 
            //matches remaining
            double p = count/total; //probability of result
            double bits = -1*(Math.log(p)/Math.log(2)); //bits of info
            entropy +=p*bits;
        }
        return entropy;
    }

    public static List<String> possibleMatches(String result, String guess, List<String> currentMatches){
        List<String> newMatches = new ArrayList<>();
        for(int i = 0; i<currentMatches.size(); i++){ 
            String candidate = currentMatches.get(i);
            String eval = guessEval(candidate, guess);
            if(eval.equals(result)){
                newMatches.add(candidate);
            }
        }
        return newMatches;
    }

    public static List<Wordval> initValList(List<String> wordsRemaining){
        List<String> possibleAnswers = loadWords("OfficialAnswers.txt");
        List<Wordval> valList = new ArrayList<>();
        for(String word: possibleAnswers){
            Wordval[] children = new Wordval[6];
            Wordval in = new Wordval(word, calculateAvgBits(word, wordsRemaining), children); 
            valList.add(in);
        }
        return valList;
    }

    public static Wordval minimax(Wordval node, boolean isMax){
        if(node.isLeaf()) return node;
        Wordval best = new Wordval();
        if(isMax){
            best.setValue(Integer.MIN_VALUE);
            for(Wordval child: node.children){
                best.setValue(Math.max(best.value, minimax(child, false).value));
            }
            return best;
        }
        else{
            best.setValue(Integer.MAX_VALUE);
            for(Wordval child: node.children){
                best.setValue(Math.min(best.value, minimax(child, false).value));
            }
            return best;
        }
    }

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        List<String> possibleAnswers = loadWords("OfficialAnswers.txt"); //matches = leaves
        List<String> possibleGuesses = loadWords("OfficialGuesses.txt");
        List<Wordval> valList = initValList(possibleAnswers);
        // for (int i = 0; i<20; i++){
        //     Wordval word = valList.get(i);
        //     System.out.println(word.word + ", " + word.value);
        // }

        while(true){
        List<String> wordsRemaining = possibleAnswers;
        //pick a random word
        Random rand = new Random();
        String answer = possibleAnswers.get(rand.nextInt(possibleAnswers.size()));

        for(int i = 0; i<6; i++){
            boolean isMax = false;
            if(i%2==0) isMax = true;
            // System.out.println("test");
            // System.out.println(valList.get(0).word + ", " + valList.get(0).value);
            Wordval suggestion = minimax(valList.get(0), isMax);
            // // System.out.println("test");
            System.out.println("\nRecommendation: "+suggestion.word);
            System.out.println(answer);
            String guess = in.nextLine().toUpperCase();
            if(possibleGuesses.contains(guess)){
                String eval = guessEval(answer, guess);
                System.out.println(eval);
                if(eval.equals("22222")){
                    System.out.println("Congrats! You win.");
                    break;
                }
                wordsRemaining = possibleMatches(eval, guess, wordsRemaining);
            }
            else{
                System.out.println("error");
                i--;
            }
        }
        System.out.println("Play again?");
        if(in.nextLine().equalsIgnoreCase("no")) break;
    }
    in.close();
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