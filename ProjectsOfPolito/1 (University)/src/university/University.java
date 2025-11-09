package university;
import java.util.logging.Logger;

/**
 * This class represents a university education system.
 * 
 * It manages students and courses.
 *
 */
public class University {
    
    private String name;
    private String rectorFirst;
    private String rectorLast;
    private Student[] students = new Student[1000];
    private Course[] courses = new Course[50];
    private int studentCount = 0;
    private int courseCount = 0;

// R1
	/**
	 * Constructor
	 * @param name name of the university
	 */
	public University(String name){
		this.name = name;
		// Example of logging
		// logger.info("Creating extended university object");
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
		this.rectorFirst = first;
		this.rectorLast = last;
	}
	
	/**
	 * Retrieves the rector of the university with the format "First Last"
	 * 
	 * @return name of the rector
	 */
	public String getRector(){
		return rectorFirst + " " + rectorLast;
	}
	
// R2
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
		int id = 10000 + studentCount;
		students[studentCount] = new Student(id, first, last);
		studentCount++;
		logger.info("New student enrolled: " + id + ", " + first + " " + last);
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
		for (int i = 0; i < studentCount; i++) {
			if (students[i].id == id) {
				return students[i].id + " " + students[i].firstName + " " + students[i].lastName;
			}
		}
		return null;
	}
	
// R3
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
		int code = 10 + courseCount;
		courses[courseCount] = new Course(code, title, teacher);
		courseCount++;
		logger.info("New course activated: " + code + ", " + title + " " + teacher);
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
		for (int i = 0; i < courseCount; i++) {
			if (courses[i].code == code) {
				return courses[i].code + "," + courses[i].title + "," + courses[i].teacher;
			}
		}
		return null;
	}
	
// R4
	/**
	 * Register a student to attend a course
	 * @param studentID id of the student
	 * @param courseCode id of the course
	 */
	public void register(int studentID, int courseCode){
		Student s = null;
		Course c = null;
		
		for (int i = 0; i < studentCount; i++) {
			if (students[i].id == studentID) {
				s = students[i];
				break;
			}
		}
		
		for (int i = 0; i < courseCount; i++) {
			if (courses[i].code == courseCode) {
				c = courses[i];
				break;
			}
		}
		
		if (s != null && c != null) {
			s.courses[s.courseCount++] = courseCode;
			c.students[c.studentCount++] = studentID;
			logger.info("Student " + studentID + " signed up for course " + courseCode);
		}
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
		Course c = null;
		for (int i = 0; i < courseCount; i++) {
			if (courses[i].code == courseCode) {
				c = courses[i];
				break;
			}
		}
		
		if (c == null) return "";
		
		String result = "";
		for (int i = 0; i < c.studentCount; i++) {
			if (i > 0) result += "\n";
			result += student(c.students[i]);
		}
		return result;
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
		Student s = null;
		for (int i = 0; i < studentCount; i++) {
			if (students[i].id == studentID) {
				s = students[i];
				break;
			}
		}
		
		if (s == null) return "";
		
		String result = "";
		for (int i = 0; i < s.courseCount; i++) {
			if (i > 0) result += "\n";
			result += course(s.courses[i]);
		}
		return result;
	}

// R5
	/**
	 * records the grade (integer 0-30) for an exam can 
	 * 
	 * @param studentId the ID of the student
	 * @param courseID	course code 
	 * @param grade		grade ( 0-30)
	 */
	public void exam(int studentId, int courseID, int grade) {
		Student s = null;
		Course c = null;
		
		for (int i = 0; i < studentCount; i++) {
			if (students[i].id == studentId) {
				s = students[i];
				break;
			}
		}
		
		for (int i = 0; i < courseCount; i++) {
			if (courses[i].code == courseID) {
				c = courses[i];
				break;
			}
		}
		
		if (s != null && c != null) {
			s.examCourses[s.examCount] = courseID;
			s.examGrades[s.examCount] = grade;
			s.examCount++;
			c.grades[c.gradeCount++] = grade;
			logger.info("Student " + studentId + " took an exam in course " + courseID + " with grade " + grade);
		}
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
		Student s = null;
		for (int i = 0; i < studentCount; i++) {
			if (students[i].id == studentId) {
				s = students[i];
				break;
			}
		}
		
		if (s == null || s.examCount == 0) 
			return "Student " + studentId + " hasn't taken any exams";
		
		double sum = 0;
		for (int i = 0; i < s.examCount; i++) {
			sum += s.examGrades[i];
		}
		double avg = sum / s.examCount;
		return "Student " + studentId + " : " + avg;
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
		Course c = null;
		for (int i = 0; i < courseCount; i++) {
			if (courses[i].code == courseId) {
				c = courses[i];
				break;
			}
		}
		
		if (c == null) return null;
		if (c.gradeCount == 0) 
			return "No student has taken the exam in " + c.title;
		
		double sum = 0;
		for (int i = 0; i < c.gradeCount; i++) {
			sum += c.grades[i];
		}
		double avg = sum / c.gradeCount;
		return "The average for the course " + c.title + " is: " + avg;
	}
	

// R6
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
		double[] scores = new double[studentCount];
		int validCount = 0;
		
		for (int i = 0; i < studentCount; i++) {
			if (students[i].examCount > 0) {
				double sum = 0;
				for (int j = 0; j < students[i].examCount; j++) {
					sum += students[i].examGrades[j];
				}
				double avg = sum / students[i].examCount;
				double bonus = ((double) students[i].examCount / students[i].courseCount) * 10;
				scores[i] = avg + bonus;
				validCount++;
			} else {
				scores[i] = -1;
			}
		}
		
		String result = "";
		int count = 0;
		for (int k = 0; k < 3 && count < validCount; k++) {
			int maxIdx = -1;
			double maxScore = -1;
			for (int i = 0; i < studentCount; i++) {
				if (scores[i] > maxScore) {
					maxScore = scores[i];
					maxIdx = i;
				}
			}
			if (maxIdx != -1) {
				if (count > 0) result += "\n";
				result += students[maxIdx].firstName + " " + students[maxIdx].lastName + " : " + scores[maxIdx];
				scores[maxIdx] = -1;
				count++;
			}
		}
		return result;
	}

// R7
    /**
     * This field points to the logger for the class that can be used
     * throughout the methods to log the activities.
     */
    public static final Logger logger = Logger.getLogger("University");

    private class Student {
        int id;
        String firstName;
        String lastName;
        int[] courses = new int[25];
        int courseCount = 0;
        int[] examCourses = new int[25];
        int[] examGrades = new int[25];
        int examCount = 0;
        
        Student(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }
    
    private class Course {
        int code;
        String title;
        String teacher;
        int[] students = new int[100];
        int studentCount = 0;
        int[] grades = new int[100];
        int gradeCount = 0;
        
        Course(int code, String title, String teacher) {
            this.code = code;
            this.title = title;
            this.teacher = teacher;
        }
    }
}