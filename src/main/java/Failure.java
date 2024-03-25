class Failure {
  public static void failWithMessage(Object message) {
    System.out.println(message);
    System.exit(1);
  }
}
