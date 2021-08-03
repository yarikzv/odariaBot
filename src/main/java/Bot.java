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
import java.util.List;


public class Bot extends TelegramLongPollingBot {
    private final String BOT_NAME;
    private final String BOT_TOKEN;

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

    // Creating reply keyboard
    public void setButton(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("\uD83D\uDC6E\u200D♀️ Смена ВМО-3"));
        keyboardFirstRow.add(new KeyboardButton("\uD83D\uDC68\u200D\uD83D\uDCBB Визировка"));
        keyboardRowList.add(keyboardFirstRow);
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("\uD83D\uDC68\u200D✈️ Еврик"));
        keyboardSecondRow.add(new KeyboardButton("\uD83D\uDD75️\u200D♂️ Досмотровые"));
        keyboardRowList.add(keyboardSecondRow);
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        keyboardThirdRow.add(new KeyboardButton("/help"));
        keyboardRowList.add(keyboardThirdRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
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
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    sendMsg(message, "Добро пожаловать! \nВыбери раздел на кнопках внизу, " +
                            "и узнай, кто сегодня на смене.");
                    break;
                case "\uD83D\uDC6E\u200D♀️ Смена ВМО-3":
                    try {
                        sendMsg(message, CalendarData.getData("3"));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83D\uDC68\u200D\uD83D\uDCBB Визировка":
                    try {
                        sendMsg(message, CalendarData.getData("8"));
                        if (!day.equals("SATURDAY") && !day.equals("SUNDAY")) {
                            sendMsg(message, CalendarData.getData("2"));
                        }

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83D\uDC68\u200D✈️ Еврик":
                    try {
                        sendMsg(message, CalendarData.getData("10"));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "\uD83D\uDD75️\u200D♂️ Досмотровые":
                    try {
                        sendMsg(message, CalendarData.getData("4"));

                    } catch (IOException | GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/help":
                    sendMsg(message, "Этот бот поможет тебе узнать, кто сегодня на смене." +
                            " Выбери, какое подразделение тебя интересует, и получи информацию." +
                            " Если есть вопросы - пиши @yarikzv");
                    break;
                default:
                    sendMsg(message, "Не понимаю \uD83E\uDDD0");
                    break;
            }
        }
    }
}
