import org.furb.exception.IllegalTagsSequence;
import org.furb.exception.InvalidTagException;
import org.furb.service.HtmlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HtmlValidatorTest {

    HtmlValidator validator = new HtmlValidator();
    private String[] tags = new String[]{
            "<html>",
            "<body>",
            "</body>",
            "</html>"
    };

    @BeforeEach
    public void before() {
        validator = new HtmlValidator();
    }

    @Test
    public void should_return_frequency() {
        String[] newTags = new String[]{
                "<html>",
                "<body>",
                "<p>",
                "</p>",
                "<p>",
                "</p>",
                "</body>",
                "</html>"
        };
        assertTrue(validator.isValidTags(newTags));
        assertEquals(1, validator.orderByNameDesc()[0].getFrequency());
    }

    @Test
    public void should_be_insensitive_case() {
        String[] newTags = new String[]{
                "<html>",
                "<body>",
                "<p>",
                "</P>",
                "<P>",
                "</p>",
                "</body>",
                "</html>"
        };
        assertTrue(validator.isValidTags(newTags));
    }

    @Test
    public void should_be_valid_with_other_vector() {
        String[] firstLine = new String[]{
                "<html>"
        };
        String[] secondLine = new String[]{
                "<body>",
                "<h1>",
                "</h1>",
                "<p>",
                "</p>",
                "</body>"
        };
        String[] lastLine = new String[]{
                "</html>"
        };
        assertTrue(validator.isValidTags(firstLine));
        assertTrue(validator.isValidTags(secondLine));
        assertTrue(validator.isValidTags(lastLine));
    }

    @Test
    public void should_be_invalid_with_other_vector_and_final_tag() {
        String[] firstLine = new String[]{
                "<html>"
        };
        String[] secondLine = new String[]{
                "<body>",
                "<h1>",
                "</h1>",
                "<p>",
                "</p>",
        };
        String[] lastLine = new String[]{
                "</h1>",
                "</body>",
                "</html>"
        };
        assertTrue(validator.isValidTags(firstLine));
        assertTrue(validator.isValidTags(secondLine));
        Exception exception = assertThrows(IllegalTagsSequence.class, () -> validator.isValidTags(lastLine));
        System.out.println(exception.getMessage());
        assertEquals("Formato do arquivo inválido!\nTag encontrada: </h1>\nTag esperada: </body>" , exception.getMessage());
    }

    @Test
    public void should_be_valid() {
        assertTrue(validator.isValidTags(tags));
    }


    @Test
    public void should_be_invalid_when_starts_with_space() {
        tags = new String[]{
                "<html>",
                "< body>",
                "</body>",
                "</html>"
        };
        assertThrows(InvalidTagException.class, () -> validator.isValidTags(tags));
    }


    @Test
    public void should_be_invalid() {
        tags = new String[]{
                "<html>",
                "<body>",
                "</html>"
        };
        assertThrows(IllegalTagsSequence.class, () -> validator.isValidTags(tags));
    }

    @Test
    public void should_throws_exception() {
        tags = new String[]{
                "<html>",
                "<body>",
                "</body",
                "</html>"
        };
        assertThrows(InvalidTagException.class, () -> validator.isValidTags(tags));
    }

    @Test
    public void should_sequence_invalid() {
        tags = new String[]{
                "<html>",
                "<body>",
                "</html>",
                "</body>"
        };
        assertThrows(IllegalTagsSequence.class, () -> validator.isValidTags(tags));
    }

    @Test
    public void should_sequence_invalid_without_closed_tag() {
        tags = new String[]{
                "<html>",
                "<body>",
                "</body>"
        };
        validator.isValidTags(tags);
        assertThrows(IllegalTagsSequence.class, () -> validator.checkStackIsEmpty());
    }

    @Test
    public void should_indicate_unexpected_final_tag() {
        tags = new String[]{
                "<html>",
                "<body>",
                "</html>",
                "</body>"
        };
        Exception exception = assertThrows(IllegalTagsSequence.class, () -> validator.isValidTags(tags));
        System.out.println(exception.getMessage());
        assertTrue(exception.getMessage().contains("Formato do arquivo inválido!\nTag encontrada: </html>\nTag esperada: </body>"));
    }

    @Test
    void testValidTags() {
        String[] validTags = {"<html>", "<body>", "<p>", "</p>", "</body>", "</html>"};
        assertTrue(validator.isValidTags(validTags));
    }

    @Test
    void testUnclosedTags() {
        String[] unclosedTags = {"<html>", "<body>", "<p>"};
        Exception exception = assertThrows(IllegalTagsSequence.class, () -> {
            validator.isValidTags(unclosedTags);
            validator.checkStackIsEmpty();
        });
        assertTrue(exception.getMessage().equals("Formato do arquivo inválido! Faltaram as seguintes tags, nesta ordem:\n</p>\n</body>\n</html>"));
    }

    @Test
    void testClosingWithoutOpening() {
        String[] closingWithoutOpening = {"<html>", "</body>"};
        Exception exception = assertThrows(IllegalTagsSequence.class, () -> {
            validator.isValidTags(closingWithoutOpening);
        });
        assertEquals("Formato do arquivo inválido!\nTag encontrada: </body>\nTag esperada: </html>", exception.getMessage());
    }

    @Test
    void testMultipleNestedTags() {
        String[] nestedTags = {"<html>", "<body>", "<div>", "<p>", "</p>", "</div>", "</body>", "</html>"};
        assertTrue(validator.isValidTags(nestedTags));
    }

    @Test
    void testEmptyTagSequence() {
        String[] emptyTags = {};
        assertTrue(validator.isValidTags(emptyTags));
    }

    @Test
    void testCheckStackIsEmptyWhenNoRemainingTags() {
        String[] tags = {"<html>", "<body>", "<p>", "</p>", "</body>", "</html>"};
        validator.isValidTags(tags);
        assertTrue(validator.checkStackIsEmpty());
    }

    @Test
    void testCheckStackIsEmptyWhenNoRemainingTags_attribute() {
        String[] tags = {"<hTml href='link.com.br'>", "<body>", "<p>", "</p>", "</body>", "</html>"};
        validator.isValidTags(tags);
        assertTrue(validator.checkStackIsEmpty());
    }

    public void testInvalidTagException() {
        String[] firstLine = new String[]{
                "< html>"
        };
        Exception exception = assertThrows(IllegalTagsSequence.class, () -> validator.isValidTags(firstLine));
        System.out.println(exception.getMessage());
        assertEquals("A tag < html> é inválida! Sua estrutura não segue os requisitos." , exception.getMessage());
    }

}

