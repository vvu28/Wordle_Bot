import java.io.*;
import java.util.*;
public class bot{
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
    //check if a word is in the answer list
    public static boolean isValid(String gI){
        List<String> officialGuesses = loadWords("OfficialGuesses.txt");
        if(officialGuesses.contains(gI)){
            return true;
        }
        return false;
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

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        List<String> officialAnswers = loadWords("OfficialAnswers.txt");
        //pick a random word
        Random rand = new Random();
        String answer = officialAnswers.get(rand.nextInt(officialAnswers.size()));
        String guess;
        for(int i = 0; i<6; i++){
        guess = in.nextLine().toUpperCase();
        if(isValid(guess)){
            System.out.println(guessEval(answer, guess));
            System.out.println();
            if(guessEval(answer, guess).equals("22222")){
                System.out.println("Congratulations! You win");
                break;
            }
        }
        else{
            System.out.println("invalid answer\n");
            i--;
        }
        }
        System.out.println(answer);
        in.close();
    }
    /** let:
     * gray = 0
     * yellow = 1
     * green = 2
     * Let's give 2 coordinates: X is position, Y is color
     * 
     */
}