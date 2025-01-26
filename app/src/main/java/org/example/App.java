package org.example;

import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

	public static Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		var host = "localhost";
		var port = "5432";
		var db = "db";
		var user = "denis";
		var password = "password";
		var conUrl = "jdbc:postgresql://%s:%s/%s".formatted(host, port, db);

		try {
			var conn = DriverManager.getConnection(conUrl, user, password);
			var s = conn.getSchema();
			log.info("Schema name: {}", s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
