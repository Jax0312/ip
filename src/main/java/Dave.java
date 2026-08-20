public class Dave {

    static String NAME = "Dave";
    static String SEPARATOR = "____________________________________________________________";

    public static void main(String[] args) {

        sendGreetings();
        sendByeMessage();
    }

    private static void sendGreetings() {
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
    }

    private static void sendByeMessage() {
        System.out.println("The wind calls. Farewell!");
        System.out.println(SEPARATOR);
    }
}
