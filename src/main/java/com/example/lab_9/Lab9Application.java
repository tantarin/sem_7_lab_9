package com.example.lab_9;

import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class Lab9Application implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(Lab9Application.class);
	private static final String BASE_URL = "https://atlas.herzen.spb.ru/";
	private static final String FACULTY_URL = BASE_URL + "faculty.php";

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(Lab9Application.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) throws IOException {
		String input = args[0];
		Document doc = Jsoup.connect(FACULTY_URL).get();
		FileWriter writer = new FileWriter("output.csv", true);
		writer.append("Number,Name,Position,Academic Degree\n");

		if (input.startsWith("факультет") || input.startsWith("институт")) {
			// Обработка факультета или института
			Element facultyElement = getFacultyOrInstituteElement(input, doc);
			// получаем кафедры
			Elements innerLinks = facultyElement.parent().select("a.alist");
			for (Element i : innerLinks) {
				if (!i.attr("href").equals("faculty.php")) {
					String chairUrl = BASE_URL + i.attr("href");
					Document chairDocument = Jsoup.connect(chairUrl).get();
					List<Teacher> teachers = parseTable(chairDocument);
					appendToCsv(teachers, writer);
				}
			}
			} else {

		}
	}

	Element getFacultyOrInstituteElement(String input, Document doc) {
		Elements facultyElements = doc.select("li[fac] > a.alist");

		for (Element facultyElement : facultyElements) {
			String facultyName = facultyElement.text();

			if(input.equals(facultyName)) {
				return facultyElement;
			}
		}
		return null;
	}

	List<Teacher> parseTable(Document document) {
		List<Teacher> teachers = new ArrayList<>();
		Elements rows = document.select("table.table_good tr:gt(0)");

		for (Element row : rows) {
			Elements columns = row.select("td");

			if (columns.size() == 4) {
				int number = Integer.parseInt(columns.get(0).text());
				String name = columns.get(1).select("a").text();
				String position = columns.get(2).text();
				String academicDegree = columns.get(3).text();
				Teacher teacher = new Teacher(number, name, position, academicDegree);
				teachers.add(teacher);
			}
		}

		return teachers;
	}

	void appendToCsv(List<Teacher> teachers, FileWriter writer) throws IOException {
		for (Teacher teacher : teachers) {
			writer.append(String.format("%d,%s,%s,%s\n", teacher.getNumber(), teacher.getName(),
					teacher.getPosition(), teacher.getAcademicDegree()));
		}
	}

	 class Teacher {
		private final int number;
		private final String name;
		private final String position;
		private final String academicDegree;

		public Teacher(int number, String name, String position, String academicDegree) {
			this.number = number;
			this.name = name;
			this.position = position;
			this.academicDegree = academicDegree;
		}

		public int getNumber() {
			return number;
		}

		public String getName() {
			return name;
		}

		public String getPosition() {
			return position;
		}

		public String getAcademicDegree() {
			return academicDegree;
		}
	}
}