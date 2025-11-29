import java.io.*;
import java.util.*;

public class EntropyBot{
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

    public static List<String> possibleMatches(String result, String guess, List<String> currentMatches){
        for(int i = currentMatches.size()-1;i>=0;i--){ // repeats for every word on answers list
            String candidate = currentMatches.get(i);
            String eval = guessEval(candidate, guess);
            if(!eval.equals(result)){
                currentMatches.remove(i);
            }
        }
        return currentMatches;
    }

    public static double calculateAvgBits(String guess, List<String> possibleMatches){
        double total = possibleMatches.size()+0.0;
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
    //this assumes playing on hard mode
    public static String findOptimal(Map<String, String> guessResult, List<String> possibleMatches){
        Map<String, Double> matchBits = new HashMap<>();
        //run through every word of the possible matches remaining
        for(String match: possibleMatches){
            double bits = calculateAvgBits(match, possibleMatches);
            matchBits.put(match, bits);
        }
        Map.Entry<String, Double> maxEntry = Collections.max(matchBits.entrySet(), Map.Entry.comparingByValue()); //finds highest bits
        String highest = maxEntry.getKey();
        return highest;
    }

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        List<String> matches = loadWords("OfficialAnswers.txt");//load official answer list
        //pick a random word
        Random rand = new Random();
        String answer = matches.get(rand.nextInt(matches.size()));

        String guess;
        Map<String, String> guessResult = new HashMap<>(); //stores guesses + results
        //prompt guess
        for(int i = 0; i<6; i++){
        guess = in.nextLine().toUpperCase();
        if(isValid(guess)){
            //evaluate guess 
            String result = guessEval(answer, guess);
            System.out.println(result+"\n");
            matches = possibleMatches(result, guess, matches); //determine matches left
            guessResult.put(guess, result); //add guess + result to hashmap
            System.out.println("Recommendation: " + findOptimal(guessResult, matches));
            if(result.equals("22222")){
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
}