package shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Tests of finding the largest bounding box in a collection.
 */
public class ShapeCollectionTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private ShapeCollection loadCollection(String content) throws IOException, InvalidInputException {
        Path path = folder.newFile().toPath();
        Files.writeString(path, content);
        ShapeCollection collection = new ShapeCollection();
        collection.read(path.toString());
        return collection;
    }

    @Test
    public void singleWinnerIsFound() throws Exception {
        ShapeCollection collection = loadCollection("""
                3
                n 0 0 2
                k 5 5 5
                h 2 2 3
                """);

        Shape largest = collection.findLargestBoundingBox();
        assertEquals("Circle", largest.getTypeName());
        assertEquals(100.0, largest.boundingBoxArea(), 0.001);
    }

    @Test
    public void tieReturnsTheFirstShape() throws Exception {
        ShapeCollection collection = loadCollection("""
                2
                n 0 0 4
                k 10 10 2
                """);

        Shape largest = collection.findLargestBoundingBox();
        assertEquals("Square", largest.getTypeName());
        assertEquals(16.0, largest.boundingBoxArea(), 0.001);

        assertSame(collection.getShapes().getFirst(), largest);
    }

    @Test
    public void emptyCollectionHasNoWinner() {
        ShapeCollection collection = new ShapeCollection();
        assertNull(collection.findLargestBoundingBox());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shapesListIsReadOnly() throws Exception {
        ShapeCollection collection = loadCollection("1\nk 0 0 2\n");
        collection.getShapes().add(new Square(0, 0, 1));
    }
}