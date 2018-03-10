import java.io.*;
import java.util.*;
// In my implementation of the Levenshtein Edit Distance algorithm, I have the source string along y-axis/2nd dimension
// of the dp matrix and target string along the x-axis/1st dimension of the dp matrix
// size of dp matrix is target.length+1 by source.length by 1
// cost of insertion, deletion and substitution are the parameters passed to the function.
// I return the edit string which tells all the steps to take to convert source string to target string
// The edit distance is the value at dp[m][n] where m=target.length and n=source.length


// the below code is basically to be used to identify matching references of the same entity strictly based on string
// matching and slight intelligence like disjoint substring, removal of punctuations and numbers etc but not knowledge based

// the program will output a percentage based score. For eg. if score is 9, then match percentage is (10-9)/100 i.e. 10% match


public class EditDistance {

    private static class Result {
        String editString;
        double editDistance;

        private Result(String s, double i) {
            this.editString = s;
            this.editDistance = i;
        }
    }

    private static int findMatchCount(String [] a,String [] b){
        int matchCount = 0;

        for(int i = 0, j = 0;i < a.length && j < b.length;){
            int res = a[i].compareTo(b[j]);
            if(res == 0){
                matchCount++;
                i++;
                j++;
            }else if(res < 0){
                i++;
            }else{
                j++;
            }
        }
        return matchCount;
    }

    private static Result editDistance(String source, String target, double insertionCost, double deletionCost, double substitutionCost) {
        String editString = "";
        double editDistance;
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
            }
            else if(i>0 && j==0) {
                editString += "i";
                i--;
            }
            else {
                if(dp[i-1][j-1] <= dp[i-1][j] && dp[i-1][j-1] <= dp[i][j-1]) {
                    if(dp[i-1][j-1] == dp[i][j]) editString += "m";
                    else editString += "s";
                    i--;
                    j--;
                }
                else if(dp[i-1][j] < dp[i][j-1]) {
                    editString += "i";
                    i--;
                }
                else if(dp[i][j-1] < dp[i-1][j]) {
                    editString += "d";
                    j--;
                }

            }
        }


        return new Result(editString, editDistance);
    }

    public static void main(String[] args) {
        double insertionCost = 1.0;
        double deletionCost = 1.0;
        double substitutionCost = 2.0;

        System.out.println("Enter the words- ");
        Scanner in = new Scanner(System.in);
        String source = in.next();
        String target = in.next();

        // removing all punctuation
        source = source.replaceAll("\\p{P}", "");
        target = target.replaceAll("\\p{P}", "");


        Result result = editDistance(source, target, insertionCost, deletionCost, substitutionCost);
        System.out.println(result.editString);
        System.out.println(result.editDistance);

    }
}

