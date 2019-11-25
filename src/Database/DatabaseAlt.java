package Database;

import com.google.gson.Gson;
import question.Question;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseAlt {
    class QuestionResponse {
        int response_code;
        List<SerializedQuestion> results;

        List<Question> getQuestions() {
            return results.stream()
                    .map(SerializedQuestion::toRealQuestion)
                    .collect(Collectors.toList());
        }
    }

    class SerializedQuestion {
        String category;
        String question;
        String correct_answer;
        List<String> incorrect_answers;

        Question toRealQuestion() {
            Question q = new Question();
            q.setQuestion(question);
            q.setRightAnswer(correct_answer);
            q.alternatives.add(correct_answer);
            q.alternatives.addAll(incorrect_answers);
            return q;
        }
    }

    class TokenResponse {
        private int response_code;
        private String response_message;
        private String token;
    }

    Gson deserializer = new Gson();
    String apiToken;

    public DatabaseAlt() {
        try {
            URL tokenRequestURL = new URL("https://opentdb.com/api_token.php?command=request");
            TokenResponse tr = deserializer.fromJson(
                    new InputStreamReader(
                            tokenRequestURL.openStream()),
                    TokenResponse.class);
            if (tr.response_code == 0)
                apiToken = tr.token;
            else
                throw new IOException();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public List<Question> getQuestions(String wantedCategory, int numberOfQuestions) {
        try {
            int categoryId = 9;
            URL questionRequest = new URL(
                    "https://opentdb.com/api.php?amount="
                            + numberOfQuestions
                            + "&category=" + categoryId
                            + "&type=multiple"
                            + "&token=" + apiToken);
            QuestionResponse qr = deserializer.fromJson(
                    new InputStreamReader(
                            questionRequest.openStream()),
                    QuestionResponse.class);
            return qr.getQuestions();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public void resetCount() {}

    public void shuffleLists() {}
}
