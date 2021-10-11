package com.zvolinskiy.odariabot;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Container {
    private static final int CONNECTION_TIMEOUT = 5000;
    /**
     * Creates connection to sites http://cto.od.ua/ and https://bkport.com/ and
     * gets data from sites.
     *
     * @param siteUrl The URL of page that gives a container status.
     * @param containerId The number of container.
     * @return String with data.
     * @throws IOException If cannot to read data from input stream.
     * */
    public static String getDataFromSites(String siteUrl, String containerId) throws IOException {
        final URL url = new URL(siteUrl + containerId);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setConnectTimeout(CONNECTION_TIMEOUT);
        connection.setReadTimeout(CONNECTION_TIMEOUT);
        // Reading Input Stream and collecting data by StringBuilder
        try (final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                "windows-1251"))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (final Exception ex) {
            ex.printStackTrace();
            return "Данные не найдены";
        }
    }

    /**
     * Gets data from site http://cto.od.ua/.
     * The site is old and uses table layout without CSS.
     * So I need to use Jsoup to parse data from page.
     * If container is present on terminal the table has second row
     * with information about container.
     *
     * @param data Uses String data from getDataFromSites() method.
     * @return String with container state.
     * */
    public static String ctoDataProcessing(String data) {
        Document document = Jsoup.parse(data);
        Elements contTable = document.select("table.scl");
        List<String> contTableData = new ArrayList<>();

        for (Element element : contTable.select("td")) {
            contTableData.add(element.text());
        }
        if (contTableData.size() > 6) {
            String container = contTableData.get(6);
            String contType = contTableData.get(7);
            String contStatus = contTableData.get(9);
            return "Терминал \"КТО\": " + container + " - " + contType + " - " + contStatus;
        } else {
            return "Терминал \"КТО\": Контейнер не найден";
        }
    }

    /**
     * Gets data from site https://bkport.com/.
     * The page returns data in json format, so I use class ContainerBkp member
     * and JSONObject to processing data.
     *
     * @param data Uses String data from getDataFromSites() method.
     * @return String with container state.
     * */
    public static String bkpDataProcessing(String data) {
        if (!data.equals("Данные не найдены")){
            ContainerBkp containerBkp = new ContainerBkp();
            JSONObject object = new JSONObject(data);
            JSONObject resultTag = object.getJSONObject("result");
            containerBkp.setSearchStatus(resultTag.getBoolean("searchStatus"));
            containerBkp.setStatusText(resultTag.getString("statusText"));
            containerBkp.setContainerId(resultTag.getString("containerId"));
            if (containerBkp.isSearchStatus()) {
                containerBkp.setArrivalDate(resultTag.getString("arrivalDate"));
                return "Терминал \"БКП\": " + containerBkp.getContainerId() + " - "
                        + containerBkp.getStatusText()
                        + ".\nПрибытие: "
                        + containerBkp.getArrivalDate().replaceAll("<div>", " ").replaceAll("</div>", "");
            } else {
                return "Терминал \"БКП\": " + containerBkp.getContainerId() + " - "
                        + containerBkp.getStatusText();
            }
        } else {
            return "Ошибка подключения к базе данных БКП. Попробуйте снова.";
        }
    }
}
