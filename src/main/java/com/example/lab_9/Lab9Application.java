package com.example.lab_9;

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

@SpringBootApplication
public class Lab9Application implements CommandLineRunner {

	private static Logger LOG = LoggerFactory.getLogger(Lab9Application.class);

	public static void main(String[] args) {
		LOG.info("STARTING THE APPLICATION");
		SpringApplication.run(Lab9Application.class, args);
		LOG.info("APPLICATION FINISHED");
	}

	@Override
	public void run(String... args) {
		if (args.length == 0) {
			System.out.println("Please provide the name of the faculty, institute, or department as a command-line argument.");
			return;
		}

		String inputQuery = args[0];
		String facultyUrl = findFacultyUrl(inputQuery);

		if (facultyUrl != null) {
			scrapeFacultyPage(facultyUrl);
		} else {
			System.out.println("Faculty not found for input: " + inputQuery);
		}
	}

	private String findFacultyUrl(String query) {
		try {
			Document document = Jsoup.connect("https://atlas.herzen.spb.ru/faculty.php").get();
			Elements facultyLinks = document.select("a.alist");

			for (Element facultyLink : facultyLinks) {
				String facultyName = facultyLink.text();
				if (facultyName.equalsIgnoreCase(query)) {
					return facultyLink.absUrl("href");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void scrapeFacultyPage(String facultyUrl) {
		try {
			Document document = Jsoup.connect(facultyUrl).get();
			Elements teacherRows = document.select("table tr");

			for (Element teacherRow : teacherRows) {
				Elements columns = teacherRow.select("td");
				for (Element column : columns) {
					System.out.print(column.text() + "\t");
				}
				System.out.println();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}