import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordFrequency {

    public static Map<String, Integer> countWords(List<String> words) {
        Map<String, Integer> frequency = new HashMap<>();

        for (String word : words) {
            if (word == null || word.isBlank()) {
                continue;
            }

            String normalised = word.toLowerCase();
            frequency.merge(normalised, 1, Integer::sum);
        }

        return frequency;
    }

    public static void main(String[] args) {
        List<String> words = List.of(
                "Java", "Spring", "java", "Kafka", "Spring", "JAVA"
        );

        Map<String, Integer> result = countWords(words);
        result.forEach((word, count) ->
                System.out.println(word + " -> " + count));
    }
}
