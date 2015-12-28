package arraystring;

public class TwoSum {
    public int[] twoSum1(int[] numbers, int target) {
        int result[] = new int[2];
        for(int i = 0; i < numbers.length; i++) {
            for(int j = i; j < numbers.length; j++) {
                if(numbers[j] == (target - numbers[i])) {
                    result[0] = i+1;
                    result[1] = j+1;
                    return result;
                }
            }
        }
        throw new IllegalArgumentException("no solution");
    }
//
//    public int[] twoSum2(int[] numbers, int target) {
//
//
//
//    }

    public static void main(String args[]) {
        TwoSum ts = new TwoSum();
        int numbers[] = {2, 7, 11, 15};
        int target = 22;
        int[] result = ts.twoSum1(numbers, target);
        for(int e: result){
            System.out.println(e);
        }


    }
}
