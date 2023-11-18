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

import java.io.IOException;
import java.util.ArrayList;

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

		if (input.startsWith("факультет") || input.startsWith("институт")) {
			// Обработка факультета или института
			Element facultyElement = getFacultyOrInstituteElement(input, doc);
			// получаем кафедры
			Element parentLi = facultyElement.parent();
			Elements innerLinks = parentLi.select("a.alist");

			for (Element i : innerLinks) {
				String href = i.attr("href");
				System.out.println(href);
				Document d = Jsoup.connect(BASE_URL + href).get();
				System.out.println(d.getAllElements());
			}
			} else {

		}
	}

	private void parseDepartment(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			Elements teacherElements = doc.select("table#teachersTable tbody tr");

			//List<Teacher> teachers = new ArrayList<>();

			for (Element teacherElement : teacherElements) {
				// TODO: Извлеките данные о преподавателе из строки таблицы и добавьте в список teachers
			}

			//writeCsv(teachers, "output.csv");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	private void writeCsv(List<Teacher> teachers, String fileName) {
//		try (FileWriter writer = new FileWriter(fileName)) {
//			writer.write("ФИО, Должность, Контакты\n");
//
//			for (Teacher teacher : teachers) {
//				writer.write(teacher.getName() + "," + teacher.getPosition() + "," + teacher.getContacts() + "\n");
//			}
//
//			System.out.println("CSV файл успешно создан: " + fileName);
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

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
}