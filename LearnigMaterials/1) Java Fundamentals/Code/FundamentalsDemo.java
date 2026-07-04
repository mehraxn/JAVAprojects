public class FundamentalsDemo {
    public static void main(String[] args) {
        String name = "Sara";
        int age = 22;

        if (age >= 18) {
            System.out.println(name + " is an adult");
        }

        int[] scores = {25, 28, 30};
        int sum = 0;
        for (int score : scores) {
            sum += score;
        }

        double average = sum / (double) scores.length;
        System.out.println("Average: " + average);
    }
}
