package com.zvolinskiy.odariabot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * A simple Telegram Bot that gives information for port expeditors about duty shifts
 * at Odessa port and container status on terminals of port. The Bot uses information from Google Calendar
 * and official sites of terminals. All information is in open source.
 * The program uses Google Calendar API for getting data from calendar, JSONObject and Jsoup for parsing sites.
 *
 * @author Yaroslav Zvolinskiy
 * @version 1.0.3
 * */
public class OdariaApplication {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new Bot(System.getenv("BOT_NAME"), System.getenv("BOT_TOKEN")));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
