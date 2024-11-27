package ui;

import exceptions.ResponseException;

import java.util.Scanner;

public class SarcasticClient {

    private ChessClient client;

    public SarcasticClient(ChessClient client){
        this.client = client;
    }

    // SARCASTIC TEXT STUFF:
    protected static void sarcasticLogin(Integer counter) throws ResponseException {
        if(counter == 10){
            System.out.println("You know what? I'm done here!");
            System.out.println("If you're not going to take this seriously, " +
                    "then you don't deserve to play!");
            System.out.println("I am making an executive decision here, and closing this session myself.");
            System.out.println("Come back when you're ready to behave and actually take this seriously!");
        } else {
            System.out.println("Ok, this is getting ridiculous. Just Enter 1 or 2 for crying out loud!");
            System.out.println("It's not rocket science! It's just tapping two keys on a keyboard.");
            System.out.println("It's so simple! Heck, a BABY could do it! Please stop wasting my time!");
        }
    }

    protected static void notLoggedInHelpSarcastic(){
        System.out.println("Oh, so you need help, do you?");
        System.out.println("Well, and here I was assuming that this lovely little menu was straightforward enough");
        System.out.println(" for any normal human being to understand. Thank you for proving me wrong.");
        System.out.println("Anyway, since you somehow can't understand these simple instructions, I guess I'll");
        System.out.println("  explain this again in a way that your simple minds can understand: \n");
        System.out.println("  Enter 1 to login to play our magical chess game.");
        System.out.println("  Enter 2 to register a new player if you're new around here and haven't played yet ");
        System.out.println("    (in which case, why are you even reading this? My sarcasm doesn't unlock unless some" +
                " cheeky monkey decided to screw around with the login prompt. ");
        System.out.println("    If you are that cheeky monkey, why the heck did you try to log in if you don't have" +
                "an existing account yet?!)");
        System.out.println("  Enter 3 to quit out of this program (and to get me to shut up if you've unlocked my " +
                "built-in sarcasm).");
        System.out.println("  Enter 4 if somehow you STILL can't understand what we're asking for you," +
                " in which case, I'll just display this message all over again. I've got all day people.\n ");
        System.out.println(" Anyway, here's your stupid menu again. At least TRY to understand it this time...\n");
    }

    protected static void sarcasticRegister() throws ResponseException {
        System.out.println("Oh thank goodness! Someone new.");
        System.out.println("Let's hope you're not as cheeky as the last person who tried to log in. Have fun!");
    }

    protected static void okWiseGuy() {
        System.out.println("Ok, who's the wise guy who tried to promote his pawn to a king, eh?");
        System.out.println("While I appreciate the attempt to sow a little bit of anarchy into the mix here, " +
                "unfortunately, promoting a pawn to a king is against the rules.");
        System.out.println("In other words... \n");
        System.out.println("Error: not a valid promotion.");
    }

    protected static String getGameNameInputSarcastic(Scanner scanner, String prompt) {
        String sarcasticResponse = "Um, you actually need to write something here. It's NOT hard. Try again: ";
        ClientWareHouse.promptAgainBecauseInvalid(sarcasticResponse, prompt);
        String gameName;
        gameName = scanner.nextLine();
        return gameName;
    }

    protected static String getInputLoginCredentialsSarcastic(Scanner scanner) {
        String inputUserName;
        System.out.println("Please enter your username or whatever. And PLEASE get it right this time!: ");
        inputUserName = scanner.nextLine();
        return inputUserName;
    }

    protected static String getInputUserNameAgainSarcastic(Scanner scanner) {
        ClientWareHouse.promptAgainBecauseInvalid("Error: not a valid option, stupid.",
                "Please enter the CORRECT username. We don't have all day: ");
        String inputUserName;
        inputUserName = scanner.nextLine();
        return inputUserName;
    }

    protected static String getPWSarcastic(Scanner scanner) {
        String inputPW;
        inputPW = getInputPWSarcastic(scanner);
        while(inputPW.isBlank()) {
            inputPW = getInputPWAgainSarcastic(scanner);
        }
        return inputPW;
    }

    protected static String getUNSarcastic(Scanner scanner) {
        String inputUN;
        inputUN = getInputLoginCredentialsSarcastic(scanner);
        while(inputUN.isBlank()) {
            // isBlank() returns true if input is empty string or only composed of whitespace characters.
            inputUN = getInputUserNameAgainSarcastic(scanner);
        }
        return inputUN;
    }

    protected static String getInputPWSarcastic(Scanner scanner) {
        String inputPassword;
        System.out.println("Enter your Password or whatever. And please don't mess this up: ");
        inputPassword = scanner.nextLine();
        return inputPassword;
    }

    protected static String getInputPWAgainSarcastic(Scanner scanner) {
        ClientWareHouse.promptAgainBecauseInvalid(
                "Error: not a valid option, stupid.", "Please enter the CORRECT password: ");
        String inputPassword;
        inputPassword = scanner.nextLine();
        return inputPassword;
    }

    protected static void ohSoYouThinkYouAreFunnyEh() {
        System.out.println("Oh, so you think you're funny, eh? " +
                "Well, I guess I'll make the decision FOR you...");
    }

    protected static String promptAndGetGameName(Boolean isSarcasticText, Scanner scanner) {
        String createPrompt = "Create a name for your Chess game: ";
        String gameName = ClientWareHouse.getInputString(createPrompt, scanner);
        while(gameName.isBlank()){
            gameName = (isSarcasticText ? SarcasticClient.getGameNameInputSarcastic(scanner, createPrompt) :
                    ClientWareHouse.getInputAgainBecauseInvalid(scanner, createPrompt));
        }
        return gameName;
    }

    protected static void printEndGameMessage(Boolean isSarcasticText) {
        System.out.println((isSarcasticText ? "The game is like, OVER. Excelsi-whatever." :
                "Game over. Thanks for playing!"));
    }

    // note: only for testing purposes. Delete afterward
/*
    private void clearDB() throws ResponseException{
*/
    // ADMIN ONLY!
 /*       serverFacade.clear();
        System.out.println("CLEARED");
*/ /*
        isLoggedIn = false;
        ClientWareHouse.notLoggedInHelp();
        */
    //}

}
