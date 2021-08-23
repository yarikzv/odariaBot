package com.zvolinskiy.odariabot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.zvolinskiy.odariabot.Container.*;


public class Bot extends TelegramLongPollingBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;
    //Button's name fields
    private final String VMO_BUTTON = "\uD83D\uDC6E\u200D♀️ Смена ВМО-3";
    private final String VIZ_BUTTON = "\uD83D\uDC68\u200D\uD83D\uDCBB Визировка";
    private final String EVR_BUTTON = "\uD83D\uDC68\u200D✈️ Еврик";
    private final String DOSM_BUTTON = "\uD83D\uDD75️\u200D♂️ Досмотровые";
    private final String DISP_BUTTON = "\uD83D\uDC77\u200D♂️ Диспетчер ТГТ";
    private final String CONT_BUTTON = "\uD83D\uDE9A Проверить контейнер";
    private final String INFO_BUTTON = "☎️ Справка";
    private final String HELP_BUTTON = "/help";

    private static final String CTO_URL = "http://cto.od.ua/ru/rep/a.pub/ispresent.html?cont_no=";
    private static final String BKP_URL = "https://bkport.com/ru/ajax/container-search/";


    public Bot(String bot_name, String bot_token) {
        BOT_NAME = bot_name;
        BOT_TOKEN = bot_token;
    }


    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            setButton(sendMessage);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    // Creating a keyboard
    public void setButton(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        keyboardRowList.add(
                new KeyboardRow(
                        Arrays.asList(
                                new KeyboardButton(VMO_BUTTON),
                                new KeyboardButton(VIZ_BUTTON))));
        keyboardRowList.add(
                new KeyboardRow(
                        Arrays.asList(
                                new KeyboardButton(EVR_BUTTON),
                                new KeyboardButton(DOSM_BUTTON))));
        keyboardRowList.add(
                new KeyboardRow(
                        Arrays.asList(
                                new KeyboardButton(DISP_BUTTON),
                                new KeyboardButton(CONT_BUTTON))));
        keyboardRowList.add(
                new KeyboardRow(
                        Arrays.asList(
                                new KeyboardButton(INFO_BUTTON),
                                new KeyboardButton(HELP_BUTTON))));
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
    }

    // deleting html-tags from Description field
    public String formattingOfDescriptionField(String info) {
        if (info.contains("<br>")) {
            info = info.replaceAll("<br>", "\n");
        }
        if (info.contains("</a>")) {
            info = info.replaceAll("</a>", "");
        }
        info = info.replaceAll("<a.*\">", "");
        return info;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        String day = LocalDate.now().getDayOfWeek().name();
        Message message = update.getMessage();
        //If the message  contains container number, checking present on terminal
        if (update.hasMessage() && message.getText().matches("[A-Za-z]{4}\\d{7}")) {
            String containerId = message.getText();
            try {
                sendMsg(message, bkpDataProcessing(getDataFromSites(BKP_URL, containerId)));
                sendMsg(message, ctoDataProcessing(getDataFromSites(CTO_URL, containerId)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (message != null && message.hasText()) {

            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Добро пожаловать! \nВыбери раздел на кнопках внизу, " +
                            "и узнай, кто сегодня на смене.");
                    break;
                case VMO_BUTTON:
                    try {
                        // ColorID: 3 - color "Grape"
                        sendMsg(message, CalendarData.getData("3"));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case VIZ_BUTTON:
                    try {
                        // ColorID: 8 - color "Graphite"
                        sendMsg(message, CalendarData.getData("8"));
                        if (!day.equals("SATURDAY") && !day.equals("SUNDAY")) {
                            // ColorID: 2 - color "Sage"
                            sendMsg(message, CalendarData.getData("2"));
                        }

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case EVR_BUTTON:
                    try {
                        // ColorID: 10 - color "Basil"
                        sendMsg(message, CalendarData.getData("10"));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case DOSM_BUTTON:
                    try {
                        // ColorID: 4 - color "Flamingo"
                        sendMsg(message, CalendarData.getData("4"));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case INFO_BUTTON:
                    try {
                        // ColorID: 5 - color "Banana"
                        sendMsg(message, formattingOfDescriptionField(CalendarData.getData("5")));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case DISP_BUTTON:
                    try {
                        // ColorID: 9 - color "Blueberry"  !!!
                        sendMsg(message, CalendarData.getData("9"));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case CONT_BUTTON:
                    try {
                        execute(SendMessage.builder()
                                .text("Введите номер контейнера:")
                                .chatId(message.getChatId().toString())
                                .build());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }

                    break;
                case HELP_BUTTON:
                    try {
                        // ColorID: 1 - color "Peacock"
                        sendMsg(message, formattingOfDescriptionField(CalendarData.getData("7")));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
}
