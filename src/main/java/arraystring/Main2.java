package arraystring;

import java.util.Scanner;

public class Main2 {
    public static int maxLength(String inputs[]) {
        int maxLen = inputs[0].length();
        for(int i = 0; i < inputs.length-1; i++) {
            maxLen = Math.max(maxLen, inputs[i+1].length());
        }
        return maxLen;

    }
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        int count = 0;
        double sum = 0;
        double average = 0;
        String oinput = sc.nextLine();
        String input = oinput.substring(0, oinput.length() - 1);
        String words[] = input.split(" ");
        for(String word: words) {
            double wordLen = 0;
            if(word.startsWith("{") && word.endsWith("}")) {
                String inputs[] = word.substring(1, word.length()-1).split(",");
                wordLen = maxLength(inputs);
            } else {
                wordLen = word.length();
            }
            sum += wordLen;
            count ++;
        }

        if(count > 0) {
            average = sum / count;
        }
        System.out.println(average);
    }
}
