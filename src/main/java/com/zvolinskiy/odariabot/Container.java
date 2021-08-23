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
    private static final int CONNECTION_TIMEOUT = 1000;

    public static String getDataFromSites(String siterUrl, String containerId) throws IOException {
        final URL url = new URL(siterUrl + containerId);
        final HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(CONNECTION_TIMEOUT);
        con.setReadTimeout(CONNECTION_TIMEOUT);

        try (final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "windows-1251"))) {
            String inputLine;
            final StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            return content.toString();
        } catch (final Exception ex) {
            return "Данные не найдены";
        }
    }

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

    public static String bkpDataProcessing(String data) {
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
    }
}
