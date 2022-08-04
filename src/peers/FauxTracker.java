class FauxTracker {
    String[] P2={"eeee","ffff","gggg","hhhh","pppp"};
    final FileInfo file = new FileInfo("gardevoir", 282, 2, "Sh4d0WBA1l",P2);
    final java.util.List<String> logs = new java.util.LinkedList<String>();


    public String okay() {
        return "ok\n";
    }

    public String notokay() {
        return "notok\n";
    }
    public String list() {
        return "list [gardevoir 282 2 Sh4d0WBA1l]\n";
    }

    public String peers() {
        return "peers 282 [Ruby:2003 Sapphire:2016 Emerald:2005]\n";
    }

}