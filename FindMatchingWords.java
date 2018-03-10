import java.io.*;
import java.util.*;

public class FindMatchingWords {
    private static class Result {
        String editString;
        double editDistance;
        double threshold;

        private Result(String s, double i, double threshold) {
            this.editString = s;
            this.editDistance = i;
            this.threshold = threshold;
        }
    }

    private static class MatchResult {
        int matchCount;
        ArrayList<String> list = new ArrayList<>();

        MatchResult(int matchCount, ArrayList<String> list) {
            this.matchCount = matchCount;
            this.list = list;
        }
    }

    private static MatchResult findMatchCount(String [] a,String [] b){
        int matchCount = 0;
        ArrayList<Integer> matchedWordsIndex = new ArrayList<>();
        ArrayList<String> matchedWords = new ArrayList<>();
        for(int i=0;i<a.length;i++) {
            if(a[i].length()<=1) continue;
            for(int j=0;j<b.length;j++) {
                if(b[j].length() <= 1) continue;
                if(a[i].matches(b[j]) && !matchedWordsIndex.contains(j))
                {
                    matchedWordsIndex.add(j);
                    matchedWords.add(b[j]);
                    matchCount++;
                }
            }
        }
        return new MatchResult(matchCount, matchedWords);
    }

    private static Result editDistance(String source, String target, double insertionCost, double deletionCost, double substitutionCost) {
        String editString = "";
        double editDistance;
        double totalInsertionCost=0;
        double totalDeletionCost=0;
        double totalSubstitutionCost=0;

        int m = target.length();
        int n = source.length();

        double[][] dp = new double[m+1][n+1];
        for(int i=0;i<=m;i++) {
            dp[i][0] = i*insertionCost;
        }
        for(int j=0;j<=n;j++) {
            dp[0][j] = j*deletionCost;
        }

        for(int i=1;i<=m;i++) {
            for(int j=1;j<=n;j++) {
                if(target.charAt(i-1)==source.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1];
                }
                else {
                    dp[i][j] = Math.min(Math.min(dp[i-1][j-1] + substitutionCost, dp[i-1][j] + insertionCost), dp[i][j-1] + deletionCost);
                }
            }
        }

        editDistance = dp[m][n];

        int i=m, j=n;
        while(true) {
            if(i==0 && j==0) {
                editString = new StringBuffer(editString).reverse().toString();
                break;
            }
            else if(i==0 && j>0) {
                editString += "d";
                j--;
                totalDeletionCost += deletionCost;
            }
            else if(i>0 && j==0) {
                editString += "i";
                i--;
                totalInsertionCost += insertionCost;
            }
            else {
                if(dp[i-1][j-1] <= dp[i-1][j] && dp[i-1][j-1] <= dp[i][j-1]) {
                    if(dp[i-1][j-1] == dp[i][j]) {
                        editString += "m";
                    }
                    else {
                        editString += "s";
                        totalSubstitutionCost += substitutionCost;
                    }
                    i--;
                    j--;
                }
                else if(dp[i-1][j] <= dp[i][j-1]) {
                    editString += "i";
                    i--;
                    totalInsertionCost += insertionCost;
                }
                else if(dp[i][j-1] < dp[i-1][j]) {
                    editString += "d";
                    j--;
                    totalDeletionCost += deletionCost;
                }

            }
        }

        double threshold = 0;
        int lengthSource = source.length();
        int lengthTarget = target.length();
        int smallerLength = lengthSource > lengthTarget ? lengthTarget:lengthSource;
        int lengthDifference = Math.abs(lengthSource-lengthTarget);
        threshold = substitutionCost*smallerLength + insertionCost*lengthDifference;
        return new Result(editString, editDistance, threshold);
    }

    // this function receives two completely processed strings
    private static double findScoreBetweenWords(String source, String target, double threshold, double editDistance) {

        double score = 0;

        // creating list of words from source and target strings
        String[] sourceArray = source.split(" ");
        String[] targetArray = target.split(" ");
        int sourceArrayLength = sourceArray.length;
        int targetArrayLength = targetArray.length;
        MatchResult matchResult = findMatchCount(sourceArray, targetArray);

        // eg. Kilol and Kilol i.e. complete match
        if(source.matches(target))
            score = 1;

        // eg. Barack Obama and Barack Hussein Obama i.e. complete substring, disjoint in this case
        boolean check = false;
        if(matchResult.matchCount==sourceArrayLength) {
            for(int i=0;i<sourceArrayLength;i++) {
                if(sourceArray[i].matches(matchResult.list.get(i))) check = true;
            }
        }
        else if(matchResult.matchCount==targetArrayLength) {
            for(int i=0;i<targetArrayLength;i++) {
                if(targetArray[i].matches(matchResult.list.get(i))) check = true;
                else check = false;
            }
        }

        if(check) score = 1;




        // Columbia University College and Columbia College in New York i.e. some common words but not regular/disjoint sub-strings
        else if(matchResult.matchCount>0)
            score = (double)matchResult.matchCount*2/(double)(sourceArrayLength+targetArrayLength);


        // eg. New York and California i.e. no matching word, we use edit distance
        else {
            if(editDistance > threshold) score = 0;
            else score = (threshold - editDistance)/threshold;
        }

        return score;
    }

    private static Result findEditDistanceInformation(String source, String target) {
        double insertionCost = 1.0;
        double deletionCost = 1.0;
        double substitutionCost = 2.0;

        return editDistance(source, target, insertionCost, deletionCost, substitutionCost);
    }

    private static List<String> listOfPrefixSuffixToRemove = Arrays.asList("mr", "miss", "ms", "mrs", "dr", "phd", "prof", "jr",
            "sr", "rev", "corp", "corporation", "comp", "company", "llc", "llp", "inc", "incorp", "incorporation", "org",
            "organisation", "organization", "association");


    // this function is to process word for lowercasing, any suffix, reorder for correct first and last name order,
    // remove punctuation
    private static ArrayList<String> processWord(String word, String category) {

        ArrayList<String> words = new ArrayList<>();
        word = word.toLowerCase();

        // remove any person or organisation prefix/suffix
        String[] wordArray = word.split(" ");
        ArrayList<String> listOfWords = new ArrayList<>(Arrays.asList(wordArray));
        for(int i=0;i<listOfWords.size();i++) {
            if(listOfPrefixSuffixToRemove.contains(listOfWords.get(i).replaceAll("\\p{P}", ""))) {
                listOfWords.remove(i);
            }
        }

        String processedWord = "";
        if(category.matches("P")) {
            // add a check if this is a persons file or not
            // reordering first name and last name if there is a comma
            boolean shift = false;
            int indexOfLastName=0;
            if(listOfWords.size()>1) {
                for(int i=0;i<listOfWords.size();i++) {
                    if(listOfWords.get(i).endsWith(",")) {
                        shift = true;
                        indexOfLastName = i;
                        break;
                    }
                }
            }
            if(shift && indexOfLastName!=listOfWords.size()-1) {
                for(int i=indexOfLastName+1;i<listOfWords.size();i++) {
                    processedWord += listOfWords.get(i) + " ";
                }

                for(int i=0;i<=indexOfLastName;i++) processedWord += listOfWords.get(i) + " ";
            }
            else {
                for(int i =0;i<listOfWords.size();i++) {
                    processedWord += listOfWords.get(i) + " ";
                }
            }

            processedWord = processedWord.substring(0, processedWord.length()-1);
        }

        else {
            for(int i =0;i<listOfWords.size();i++) {
                processedWord += listOfWords.get(i) + " ";
            }
            processedWord = processedWord.substring(0, processedWord.length()-1);
        }

        // getting words with and without parentheses if they exist
        String word1 = "";
        String word2 = "";
        if(processedWord.contains("(")) {
            word1 = processedWord.replaceAll("\\(.*\\)", "");
            word1 = word1.replaceAll("\\p{P}", "");
            words.add(word1);

            word2 = processedWord.replace("(", "");
            word2 = word2.replace(")", "");
            word2 = word2.replaceAll("\\p{P}", "");
            words.add(word2);
        }
        else {
            words.add(processedWord.replaceAll("\\p{P}", ""));
        }

        return words;
    }

    public static void main(String[] args) {
        System.out.println("Enter the names of the two files you want to match- ");
        Scanner in = new Scanner(System.in);
        String file1 = in.next();
        String file2 = in.next();

        System.out.println("Enter the type of data- P(Persons), O(Organisations), L(Locations)");
        String category = in.next();

        String filePath = new File("").getAbsolutePath();

        System.out.println("Enter the threshold percentage beyond which 2 words match: ");
        double thresholdPercentage = in.nextDouble();


        System.out.println("Do you want to output the matching score? True (T) or False (F)");
        String printScores = in.next();

        ArrayList<String> sourceList = new ArrayList<>();
        ArrayList<String> targetList = new ArrayList<>();

        String fileSeparator = "\\";
        // create a list of words from the first file
        try {
            FileReader fileReader = new FileReader(filePath + fileSeparator + file1);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sourceList.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create a list of words from the second file
        try {
            FileReader fileReader = new FileReader(filePath + fileSeparator + file2);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                targetList.add(line);
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        double tempScore=0;

        // this is calculated dynamically for every word pair based on the substitution and addition cost
        // substitution_cost*length_of_smallest_string + addition_cost*length_difference
        double threshold = 0;

        // This is the Levenshtein edit distance based on the provided insertion, deletion and substitution costs
        double editDistance = 0;

        ArrayList<ArrayList<String>> matchedStrings = new ArrayList<>();

        ArrayList<String> sourceWords = new ArrayList<>();
        ArrayList<String> targetWords = new ArrayList<>();


        try {
            PrintStream out = new PrintStream(new FileOutputStream("output.txt"));
            System.setOut(out);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // now the two lists are ready
        for(String source_word: sourceList) {
            sourceWords = processWord(source_word, category);

            for(String target_word: targetList) {
                targetWords = processWord(target_word, category);

                ArrayList<Double> scores = new ArrayList<>();

                for(String s:sourceWords) {
                    for(String t:targetWords) {
                        Result editDistanceInfo = findEditDistanceInformation(s, t);
                        threshold = editDistanceInfo.threshold;
                        editDistance = editDistanceInfo.editDistance;
                        scores.add(findScoreBetweenWords(s, t, threshold, editDistance));
                    }
                }

                tempScore = Collections.max(scores);

                String delimiter = "|";


                if(tempScore*100 >= thresholdPercentage) {
                    ArrayList<String> matchedPair = new ArrayList<>();
                    matchedPair.add(source_word);
                    matchedPair.add(target_word);
                    if(printScores.matches("T"))
                        System.out.println(source_word + delimiter + target_word + delimiter + tempScore + "\n");
                    else
                        System.out.println(source_word + delimiter + target_word + "\n");
                    matchedStrings.add(matchedPair);
                }
            }
        }
    }
}
