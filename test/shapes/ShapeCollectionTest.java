package shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

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

    private String createFile(String content) throws IOException {
        Path path = folder.newFile().toPath();
        Files.writeString(path, content);
        return path.toString();
    }

    private ShapeCollection loadCollection(String content) throws IOException, InvalidInputException {
        ShapeCollection collection = new ShapeCollection();
        collection.read(createFile(content));
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

        assertSame(collection.getShapes().get(0), largest);
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

    // --- Every kind of shape can be the winner ---

    @Test
    public void circleCanWin() throws Exception {
        // circle r=5: 10 x 10 = 100, square a=9: 81
        assertTrue(loadCollection("2\nn 0 0 9\nk 0 0 5\n").findLargestBoundingBox() instanceof Circle);
    }

    @Test
    public void squareCanWin() throws Exception {
        assertTrue(loadCollection("3\nk 0 0 1\nn 0 0 10\nh 0 0 5\n").findLargestBoundingBox() instanceof Square);
    }

    @Test
    public void regularTriangleCanWin() throws Exception {
        // triangle a=10: 86.6, square a=9: 81
        assertTrue(loadCollection("2\nn 0 0 9\nh 0 0 10\n").findLargestBoundingBox() instanceof RegularTriangle);
    }

    @Test
    public void regularHexagonCanWin() throws Exception {
        // hexagon a=6: 124.7, circle r=5: 100
        assertTrue(loadCollection("2\nk 0 0 5\ns 0 0 6\n").findLargestBoundingBox() instanceof RegularHexagon);
    }

    // --- Position in the collection and in the plane ---

    @Test
    public void singleShapeIsTheWinner() throws Exception {
        ShapeCollection collection = loadCollection("1\nh 0 0 1\n");
        assertSame(collection.getShapes().get(0), collection.findLargestBoundingBox());
    }

    @Test
    public void winnerCanBeTheFirstTheMiddleOrTheLastShape() throws Exception {
        assertEquals(0, indexOfWinner("3\nk 0 0 9\nk 0 0 2\nk 0 0 3\n"));
        assertEquals(1, indexOfWinner("3\nk 0 0 2\nk 0 0 9\nk 0 0 3\n"));
        assertEquals(2, indexOfWinner("3\nk 0 0 2\nk 0 0 3\nk 0 0 9\n"));
    }

    private int indexOfWinner(String content) throws Exception {
        ShapeCollection collection = loadCollection(content);
        return collection.getShapes().indexOf(collection.findLargestBoundingBox());
    }

    @Test
    public void firstOfSeveralTiedShapesWins() throws Exception {
        // the circle r=2 and the square a=4 both have a 4 x 4 bounding box, the smaller circle first
        ShapeCollection collection = loadCollection("4\nk 0 0 1\nn 0 0 4\nk 9 9 2\nn 3 3 4\n");
        assertSame(collection.getShapes().get(1), collection.findLargestBoundingBox());
    }

    @Test
    public void positionOfTheShapeDoesNotMatter() throws Exception {
        ShapeCollection collection = loadCollection("2\nn 1000 -1000 2\nk 0 0 3\n");
        assertTrue(collection.findLargestBoundingBox() instanceof Circle);
    }

    @Test
    public void searchDoesNotChangeTheCollection() throws Exception {
        ShapeCollection collection = loadCollection("3\nk 0 0 1\nn 0 0 4\nh 0 0 2\n");
        Shape first = collection.getShapes().get(0);

        collection.findLargestBoundingBox();
        collection.findLargestBoundingBox();

        assertEquals(3, collection.size());
        assertSame(first, collection.getShapes().get(0));
    }

    // --- Empty collection ---

    @Test
    public void newCollectionIsEmpty() {
        ShapeCollection collection = new ShapeCollection();
        assertEquals(0, collection.size());
        assertTrue(collection.getShapes().isEmpty());
    }

    @Test
    public void fileWithoutShapesGivesACollectionWithoutWinner() throws Exception {
        ShapeCollection collection = loadCollection("0\n");
        assertEquals(0, collection.size());
        assertNull(collection.findLargestBoundingBox());
    }

    // --- State: reading more than once ---

    @Test
    public void shapesAreKeptInTheOrderOfTheFile() throws Exception {
        ShapeCollection collection = loadCollection("3\nn 0 0 1\nk 0 0 1\nh 0 0 1\n");
        assertEquals("Square", collection.getShapes().get(0).getTypeName());
        assertEquals("Circle", collection.getShapes().get(1).getTypeName());
        assertEquals("Regular Triangle", collection.getShapes().get(2).getTypeName());
    }

    @Test
    public void readingASecondFileAddsItsShapesAfterTheOnesAlreadyLoaded() throws Exception {
        ShapeCollection collection = loadCollection("1\nn 0 0 2\n");

        collection.read(createFile("2\nk 0 0 1\nh 0 0 1\n"));

        assertEquals(3, collection.size());
        assertEquals("Square", collection.getShapes().get(0).getTypeName());
        assertEquals("Regular Triangle", collection.getShapes().get(2).getTypeName());
    }

    @Test
    public void winnerIsSearchedAmongTheShapesOfAllFilesRead() throws Exception {
        ShapeCollection collection = loadCollection("1\nn 0 0 2\n");
        collection.read(createFile("1\nk 0 0 5\n"));

        assertTrue(collection.findLargestBoundingBox() instanceof Circle);
    }

    @Test
    public void failedReadKeepsTheShapesLoadedBefore() throws Exception {
        ShapeCollection collection = loadCollection("1\nn 0 0 2\n");
        String invalid = createFile("2\nk 0 0 5\nx 0 0 1\n");

        assertThrows(InvalidInputException.class, () -> collection.read(invalid));

        assertEquals(1, collection.size());
        assertEquals("Square", collection.getShapes().get(0).getTypeName());
    }
}
