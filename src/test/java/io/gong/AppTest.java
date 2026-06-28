package io.gong;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AppTest {

    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream out;
    private ByteArrayOutputStream err;

    @Before
    public void redirectStreams() {
        out = new ByteArrayOutputStream();
        err = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(err, true, StandardCharsets.UTF_8));
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void defaultRunPrintsReadmeExpectedSlots() {
        App.main(new String[0]);

        String expected = String.join(System.lineSeparator(),
            "Available slot: 07:00",
            "Available slot: 10:00",
            "Available slot: 11:00",
            "Available slot: 12:00",
            "Available slot: 14:00",
            "Available slot: 15:00",
            "Available slot: 17:00",
            "Available slot: 18:00"
        ) + System.lineSeparator();

        assertEquals(expected, stdout());
    }

    @Test
    public void invalidDurationPrintsUsageAndProducesNoSlots() {
        App.main(new String[]{"Alice,Jack", "not-a-number"});

        assertTrue(stdout().isEmpty());
        assertTrue(stderr().contains("Usage:"));
    }

    @Test
    public void blankPersonArgumentPrintsUsageAndProducesNoSlots() {
        App.main(new String[]{" , ", "60"});

        assertTrue(stdout().isEmpty());
        assertTrue(stderr().contains("Usage:"));
    }

    private String stdout() {
        return out.toString(StandardCharsets.UTF_8);
    }

    private String stderr() {
        return err.toString(StandardCharsets.UTF_8);
    }
}
