import jdk.swing.interop.SwingInterOpUtils;

public class Dave {
    public static void main(String[] args) {

        String NAME = "Dave";
        String SEPARATOR = "____________________________________________________________";

        String banner = """
                ____
                |  _ \\  __ ___   _____\s
                | | | |/ _` \\ \\ / / _ \\
                | |_| | (_| |\\ V /  __/
                |____/ \\__,_| \\_/ \\___|
                """;
        System.out.println(SEPARATOR);
        System.out.println(banner);
        System.out.printf("Hello! I'm %s.\nAt your service!\n", NAME);
        System.out.println(SEPARATOR);
        System.out.println("The wind calls. Farewell!");
        System.out.println(SEPARATOR);
    }
}
