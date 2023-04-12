import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testClientClassExists() {
        try {
            Class.forName("Client");
        } catch (ClassNotFoundException e) {
            fail("Client class not found");
        }
    }

    @Test
    public void testAdderClassExists() {
        try {
            Class.forName("Adder");
        } catch (ClassNotFoundException e) {
            fail("Adder class not found");
        }
    }

    @Test
    public void testClientMainMethod() throws ClassNotFoundException {
        Class<?> clientClass = Class.forName("Client");
        boolean hasMainMethod = false;
        for (Method method : clientClass.getDeclaredMethods()) {
            if (method.getName().equals("main")) {
                hasMainMethod = true;
                break;
            }
        }
        assertTrue(hasMainMethod);
    }

    @Test
    public void testAdderRunnableInterface() throws Exception {
        assertTrue(Runnable.class.isAssignableFrom(Class.forName("Adder")));
    }

    @Test
    public void testAdderConstructor() throws Exception {
        // Get the fields of the Adder class using reflection
        Constructor<?> adderConstructor = Class.forName("Adder").
                                                getDeclaredConstructor(int.class, int.class);
        assertNotNull(adderConstructor);
    }

    @Test
    public void testAdderOutput() {
        try {
            // Get the fields of the Adder class using reflection
            Constructor<?> adderConstructor = Class.forName("Adder").
                    getDeclaredConstructor(int.class, int.class);
            Object adder = adderConstructor.newInstance(2, 3);
            Method runMethod = adder.getClass().getDeclaredMethod("run");
            runMethod.invoke(adder);
            assertEquals("5\n", outContent.toString());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testAdderThread() throws InterruptedException {
        try {
            String input = "2\n3\n";
            ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
            System.setIn(inputStream);

            Method mainMethod = Class.forName("Client").getDeclaredMethod("main", String[].class);
            mainMethod.invoke(null, (Object)new String[0]);

            HashMap<String, String> map = ScalerThread.map;

            String adderThreadName = null;

            if(!map.containsKey("Adder")){
                fail("Adder class not invoked");
            }
            adderThreadName = map.get("Adder");
            assertNotEquals(adderThreadName, Thread.currentThread().getName(), "Adder should be invoked on separate thread");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}