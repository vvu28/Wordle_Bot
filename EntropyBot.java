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

    public static double calculateAvgBits(String guess, List<String> wordsRemaining){
        double total = wordsRemaining.size()+0.0;
        // List<String> officialAnswers = loadWords("OfficialAnswers.txt");
        //count occurences of result pattern
        Map<String, Integer> patternCounts = new HashMap<>();
        for (String solution: wordsRemaining){ 
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

    public static String findOptimal(List<String> possibleMatches){
        Map<String, Double> matchBits = new HashMap<>();
        List<String> officialAnswers = loadWords("OfficialAnswers.txt");
        // System.out.println("Evaluating entropy for " + possibleMatches.size() + " candidates");

        //run through every word of the possible matches remaining
        for(String match: officialAnswers){
            double bits = calculateAvgBits(match, possibleMatches);
            matchBits.put(match, bits);
        }
        Map.Entry<String, Double> maxEntry = Collections.max(matchBits.entrySet(), Map.Entry.comparingByValue()); //finds highest bits
        String highest = maxEntry.getKey();
        if (possibleMatches.size() == 1) return possibleMatches.get(0);
        return highest;
    }

    public static void avgGuessTotal(){
        List<String> matches = loadWords("OfficialAnswers.txt");
        // Map<String, String> guessResult = new HashMap<>(); //stores guesses + results
        int guessTotal = 0;
        int lossTotal = 0;
        for(String answer: matches){
            List<String> candidates = new ArrayList<>(matches);
            int sum = 0;
            for (int i = 0; i<6; i++){
                String guess = findOptimal(candidates);
                String eval = guessEval(answer, guess);
                // guessResult.put(guess, eval);
                sum++;
                candidates = possibleMatches(eval, guess, candidates);
                if (candidates.size()==1){
                    guessTotal+=sum+1;
                    break;
                }
                if(i==5&&candidates.size()!=1){
                    lossTotal++;
                    guessTotal+=7;
                }
            }
        }
        double average = (double) guessTotal/matches.size();
        System.out.println("Avg:" + average + ", losses:" + lossTotal);
    }

    public static void main(String[] args){
        // System.out.println("test");
        // try {
        //     avgGuessTotal();
        // } 
        // catch (Exception e) {
        //     e.printStackTrace();
        // }       
        // System.out.println("test");

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
            if(result.equals("22222")){
                System.out.println("Congratulations! You win");
                break;
            }
            System.out.println("Recommendation: " + findOptimal(matches));
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