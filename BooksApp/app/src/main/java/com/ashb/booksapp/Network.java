package com.ashb.booksapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Network {

    private static final String jsonURL = "https://ces-web2.southwales.ac.uk/students/16022599/books.json";
    private static final String xmlURL = "https://ces-web2.southwales.ac.uk/students/16022599/books.xml";

    /* Decodes URL data to JSON String */
    static String retrieveJSONBooks() {
        HttpURLConnection url_connection = null;
        BufferedReader reader = null;
        String json_string = null;

        try {
            // opens the connection and connects to the URL with a GET request
            url_connection = (HttpURLConnection) new URL(jsonURL).openConnection();
            url_connection.setRequestMethod("GET");
            url_connection.connect();

            // creates an input stream to read from the URL
            InputStream input_stream = url_connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input_stream));

            // builds a string from the URL
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) return null;
            json_string = builder.toString();
            Log.d("JSON String", json_string);

        } catch (IOException err) {
            err.printStackTrace();
        }
        // returns the JSON string
        return json_string;
    }

    /* Decodes URL data to XML String */
    static String retrieveXMLBooks() {
        String xml_string = "";
        HttpURLConnection url_connection = null;

        try {
            // opens the connection and connects to the URL with a GET request
            url_connection = (HttpURLConnection) new URL(xmlURL).openConnection();
            url_connection.setRequestMethod("GET");
            url_connection.connect();

            // creates an input stream to read from the URL
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            url_connection.getInputStream()));

            // builds a string from the URL
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);

            in.close();
            Log.d("Response", response.toString());
            xml_string = response.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // returns the XML string
        return xml_string;
    }
}
