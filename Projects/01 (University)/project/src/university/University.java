package university;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class represents a university education system.
 * 
 * It manages students and courses.
 *
 */
public class University {

    private static final int FIRST_STUDENT_ID = 10000;
    private static final int FIRST_COURSE_CODE = 10;
    private static final int MAX_STUDENTS = 1000;
    private static final int MAX_COURSES = 50;
    private static final int MAX_COURSES_PER_STUDENT = 25;
    private static final int MAX_STUDENTS_PER_COURSE = 100;

    private final String name;
    private String rectorFirst;
    private String rectorLast;
    private final Student[] students = new Student[MAX_STUDENTS];
    private final Course[] courses = new Course[MAX_COURSES];
    private int studentCount = 0;
    private int courseCount = 0;

	/**
	 * Constructor
	 * @param name name of the university
	 */
	public University(String name){
		this.name = requireNotBlank(name, "university name");
	}
	
	/**
	 * Getter for the name of the university
	 * 
	 * @return name of university
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Defines the rector for the university
	 * 
	 * @param first first name of the rector
	 * @param last	last name of the rector
	 */
	public void setRector(String first, String last){
		this.rectorFirst = requireNotBlank(first, "rector first name");
		this.rectorLast = requireNotBlank(last, "rector last name");
	}
	
	/**
	 * Retrieves the rector of the university with the format "First Last"
	 * 
	 * @return name of the rector
	 */
	public String getRector(){
		return rectorFirst + " " + rectorLast;
	}
	
	/**
	 * Enrol a student in the university
	 * The university assigns ID numbers 
	 * progressively from number 10000.
	 * 
	 * @param first first name of the student
	 * @param last last name of the student
	 * 
	 * @return unique ID of the newly enrolled student
	 */
	public int enroll(String first, String last){
		if (studentCount == students.length) {
			throw new IllegalStateException("maximum student capacity reached");
		}
		String validFirst = requireNotBlank(first, "student first name");
		String validLast = requireNotBlank(last, "student last name");
		int id = FIRST_STUDENT_ID + studentCount;
		students[studentCount] = new Student(id, validFirst, validLast);
		studentCount++;
		logger.info("New student enrolled: " + id + ", " + validFirst + " " + validLast);
		return id;
	}
	
	/**
	 * Retrieves the information for a given student.
	 * The university assigns IDs progressively starting from 10000
	 * 
	 * @param id the ID of the student
	 * 
	 * @return information about the student
	 */
	public String student(int id){
		Student student = findStudent(id);
		return student == null ? null : student.description();
	}
	
	/**
	 * Activates a new course with the given teacher
	 * Course codes are assigned progressively starting from 10.
	 * 
	 * @param title title of the course
	 * @param teacher name of the teacher
	 * 
	 * @return the unique code assigned to the course
	 */
	public int activate(String title, String teacher){
		if (courseCount == courses.length) {
			throw new IllegalStateException("maximum course capacity reached");
		}
		String validTitle = requireNotBlank(title, "course title");
		String validTeacher = requireNotBlank(teacher, "course teacher");
		int code = FIRST_COURSE_CODE + courseCount;
		courses[courseCount] = new Course(code, validTitle, validTeacher);
		courseCount++;
		logger.info("New course activated: " + code + ", " + validTitle + " " + validTeacher);
		return code;
	}
	
	/**
	 * Retrieve the information for a given course.
	 * 
	 * The course information is formatted as a string containing 
	 * code, title, and teacher separated by commas, 
	 * e.g., {@code "10,Object Oriented Programming,James Gosling"}.
	 * 
	 * @param code unique code of the course
	 * 
	 * @return information about the course
	 */
	public String course(int code){
		Course course = findCourse(code);
		return course == null ? null : course.description();
	}
	
	/**
	 * Register a student to attend a course
	 * @param studentID id of the student
	 * @param courseCode id of the course
	 */
	public void register(int studentID, int courseCode){
		Student student = findStudentOrThrow(studentID);
		Course course = findCourseOrThrow(courseCode);
		if (student.isRegisteredFor(courseCode)) {
			return;
		}
		if (student.courseCount == student.courses.length) {
			throw new IllegalStateException("student course capacity reached");
		}
		if (course.studentCount == course.students.length) {
			throw new IllegalStateException("course attendee capacity reached");
		}

		student.courses[student.courseCount++] = courseCode;
		course.students[course.studentCount++] = studentID;
		logger.info("Student " + studentID + " signed up for course " + courseCode);
	}
	
	/**
	 * Retrieve a list of attendees.
	 * 
	 * The students appear one per row (rows end with `'\n'`) 
	 * and each row is formatted as describe in in method {@link #student}
	 * 
	 * @param courseCode unique id of the course
	 * @return list of attendees separated by "\n"
	 */
	public String listAttendees(int courseCode){
		Course course = findCourse(courseCode);
		if (course == null) return "";

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < course.studentCount; i++) {
			if (i > 0) result.append('\n');
			result.append(student(course.students[i]));
		}
		return result.toString();
	}

	/**
	 * Retrieves the study plan for a student.
	 * 
	 * The study plan is reported as a string having
	 * one course per line (i.e. separated by '\n').
	 * The courses are formatted as describe in method {@link #course}
	 * 
	 * @param studentID id of the student
	 * 
	 * @return the list of courses the student is registered for
	 */
	public String studyPlan(int studentID){
		Student student = findStudent(studentID);
		if (student == null) return "";

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < student.courseCount; i++) {
			if (i > 0) result.append('\n');
			result.append(course(student.courses[i]));
		}
		return result.toString();
	}

	/**
	 * records the grade (integer 0-30) for an exam can 
	 * 
	 * @param studentId the ID of the student
	 * @param courseID	course code 
	 * @param grade		grade ( 0-30)
	 */
	public void exam(int studentId, int courseID, int grade) {
		validateGrade(grade);
		Student student = findStudentOrThrow(studentId);
		Course course = findCourseOrThrow(courseID);
		if (!student.isRegisteredFor(courseID)) {
			throw new IllegalStateException(
					"student " + studentId + " is not registered for course " + courseID);
		}

		student.recordGrade(courseID, grade);
		course.recordGrade(studentId, grade);
		logger.info("Student " + studentId + " took an exam in course " + courseID + " with grade " + grade);
	}

	/**
	 * Computes the average grade for a student and formats it as a string
	 * using the following format 
	 * 
	 * {@code "Student STUDENT_ID : AVG_GRADE"}. 
	 * 
	 * If the student has no exam recorded the method
	 * returns {@code "Student STUDENT_ID hasn't taken any exams"}.
	 * 
	 * @param studentId the ID of the student
	 * @return the average grade formatted as a string.
	 */
	public String studentAvg(int studentId) {
		Student student = findStudent(studentId);
		if (student == null || student.examCount == 0)
			return "Student " + studentId + " hasn't taken any exams";

		return "Student " + studentId + " : " + student.averageGrade();
	}
	
	/**
	 * Computes the average grades of all students that took the exam for a given course.
	 * 
	 * The format is the following: 
	 * {@code "The average for the course COURSE_TITLE is: COURSE_AVG"}.
	 * 
	 * If no student took the exam for that course it returns {@code "No student has taken the exam in COURSE_TITLE"}.
	 * 
	 * @param courseId	course code 
	 * @return the course average formatted as a string
	 */
	public String courseAvg(int courseId) {
		Course course = findCourse(courseId);
		if (course == null) return null;
		if (course.gradeCount == 0)
			return "No student has taken the exam in " + course.title;

		return "The average for the course " + course.title + " is: " + course.averageGrade();
	}
	

	/**
	 * Retrieve information for the best students to award a price.
	 * 
	 * The students' score is evaluated as the average grade of the exams they've taken. 
	 * To take into account the number of exams taken and not only the grades, 
	 * a special bonus is assigned on top of the average grade: 
	 * the number of taken exams divided by the number of courses the student is enrolled to, multiplied by 10.
	 * The bonus is added to the exam average to compute the student score.
	 * 
	 * The method returns a string with the information about the three students with the highest score. 
	 * The students appear one per row (rows are terminated by a new-line character {@code '\n'}) 
	 * and each one of them is formatted as: {@code "STUDENT_FIRSTNAME STUDENT_LASTNAME : SCORE"}.
	 * 
	 * @return info on the best three students.
	 */
	public String topThreeStudents() {
		List<Student> ranked = new ArrayList<>();
		for (int i = 0; i < studentCount; i++) {
			if (students[i].examCount > 0) {
				ranked.add(students[i]);
			}
		}
		ranked.sort(Comparator.comparingDouble(Student::score).reversed()
				.thenComparing(student -> student.lastName)
				.thenComparing(student -> student.firstName)
				.thenComparingInt(student -> student.id));

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < Math.min(3, ranked.size()); i++) {
			Student student = ranked.get(i);
			if (i > 0) result.append('\n');
			result.append(student.firstName).append(' ').append(student.lastName)
					.append(" : ").append(student.score());
		}
		return result.toString();
	}

	private static String requireNotBlank(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " cannot be null or blank");
		}
		return value.trim();
	}

	private Student findStudent(int id) {
		for (int i = 0; i < studentCount; i++) {
			if (students[i].id == id) return students[i];
		}
		return null;
	}

	private Student findStudentOrThrow(int id) {
		Student student = findStudent(id);
		if (student == null) {
			throw new IllegalArgumentException("unknown student ID: " + id);
		}
		return student;
	}

	private Course findCourse(int code) {
		for (int i = 0; i < courseCount; i++) {
			if (courses[i].code == code) return courses[i];
		}
		return null;
	}

	private Course findCourseOrThrow(int code) {
		Course course = findCourse(code);
		if (course == null) {
			throw new IllegalArgumentException("unknown course code: " + code);
		}
		return course;
	}

	private static void validateGrade(int grade) {
		if (grade < 0 || grade > 30) {
			throw new IllegalArgumentException("grade must be between 0 and 30");
		}
	}

    /**
     * This field points to the logger for the class that can be used
     * throughout the methods to log the activities.
     */
    public static final Logger logger = Logger.getLogger("University");

    private static final class Student {
        final int id;
        final String firstName;
        final String lastName;
        final int[] courses = new int[MAX_COURSES_PER_STUDENT];
        int courseCount = 0;
        final int[] examCourses = new int[MAX_COURSES_PER_STUDENT];
        final int[] examGrades = new int[MAX_COURSES_PER_STUDENT];
        int examCount = 0;
        
        Student(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

		String description() {
			return id + " " + firstName + " " + lastName;
		}

		boolean isRegisteredFor(int courseCode) {
			for (int i = 0; i < courseCount; i++) {
				if (courses[i] == courseCode) return true;
			}
			return false;
		}

		void recordGrade(int courseCode, int grade) {
			for (int i = 0; i < examCount; i++) {
				if (examCourses[i] == courseCode) {
					examGrades[i] = grade;
					return;
				}
			}
			if (examCount == examCourses.length) {
				throw new IllegalStateException("student exam capacity reached");
			}
			examCourses[examCount] = courseCode;
			examGrades[examCount] = grade;
			examCount++;
		}

		double averageGrade() {
			double sum = 0.0;
			for (int i = 0; i < examCount; i++) sum += examGrades[i];
			return sum / examCount;
		}

		double score() {
			return averageGrade() + ((double) examCount / courseCount) * 10.0;
		}
    }
    
    private static final class Course {
        final int code;
        final String title;
        final String teacher;
        final int[] students = new int[MAX_STUDENTS_PER_COURSE];
        int studentCount = 0;
        final int[] examStudents = new int[MAX_STUDENTS_PER_COURSE];
        final int[] grades = new int[MAX_STUDENTS_PER_COURSE];
        int gradeCount = 0;
        
        Course(int code, String title, String teacher) {
            this.code = code;
            this.title = title;
            this.teacher = teacher;
        }

		String description() {
			return code + "," + title + "," + teacher;
		}

		void recordGrade(int studentId, int grade) {
			for (int i = 0; i < gradeCount; i++) {
				if (examStudents[i] == studentId) {
					grades[i] = grade;
					return;
				}
			}
			if (gradeCount == grades.length) {
				throw new IllegalStateException("course exam capacity reached");
			}
			examStudents[gradeCount] = studentId;
			grades[gradeCount] = grade;
			gradeCount++;
		}

		double averageGrade() {
			double sum = 0.0;
			for (int i = 0; i < gradeCount; i++) sum += grades[i];
			return sum / gradeCount;
		}
    }
}
