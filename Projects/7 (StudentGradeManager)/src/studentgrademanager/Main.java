package studentgrademanager;

public class Main {
    public static void main(String[] args) {
        GradeBook gradeBook = new GradeBook();
        gradeBook.addStudent(new Student("S001", "Amina"));
        gradeBook.addStudent(new Student("S002", "Luca"));

        gradeBook.recordGrade("S001", "Mathematics", 88.0);
        gradeBook.recordGrade("S001", "Programming", 94.0);
        gradeBook.recordGrade("S002", "Mathematics", 52.0);
        gradeBook.recordGrade("S002", "Programming", 64.0);

        System.out.println("Student grade summary");
        for (Student student : gradeBook.listStudentsByAverage()) {
            System.out.printf("%s: average %.2f, %s, highest %.2f, lowest %.2f%n",
                    student.getName(),
                    student.calculateAverage(),
                    student.isPassing() ? "PASS" : "FAIL",
                    student.getHighestGrade(),
                    student.getLowestGrade());
        }
    }
}
