import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import java.util.StringTokenizer;

//ok | notok | list | peers | interested | have | getpieces | data |
class Parsing {
    private static String ipReal = "([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])";
    private static String port = ":[0-9]{1,5}";
    private static String ipPort = "(" + ipReal + "\\.){3}" + ipReal + port;
    private static String empty = "";
    private static String ip = "[0-9]{1,8}:[0-9]{1,5}";
    private static String key = "[a-zA-Z_0-9]+";
    private static String buffermap = "[01]+";
    private static String indexPiece = "[0-9]+:[01]+";
    private static String fileDesc = "[a-zA-Z_0-9]+\\.[a-zA-Z_0-9]{3,4} [0-9]+ [0-9]+ [a-zA-Z_0-9]+";
    private static String index = "[0-9]+";
    public static boolean parse__bool(String string) throws Exception {
        if (string.matches("OK\n"))
        {
            //System.out.println("this is a OK like message");
            return true;
        }
        if (string.matches("NOK\n"))
        {
            //System.out.println("this is a NOK like message");
            return true;
        }
        if (string.matches("list \\[" + empty + "\\]\n") || string.matches("list \\["+ fileDesc + "\\]\n") || string.matches("list \\[" + fileDesc + " +" + fileDesc + "\\]\n"))
        {
            //System.out.println("this is a LIST like message");
            return true;
        }
        //TOFIX: Decimal pointe instead of integer
        /*if (string.matches("peers [a-zA-Z_0-9]+ \\[["+ ip + "\\." + ip + "\\." + ip + "\\." + ip + ":[0-9]{1,5} ]+\\]\n"))
        {
            //System.out.println("this is a PEERS like message");
            return true;
        }
        */
        if (string.matches("peers [a-zA-Z_0-9]+ \\["+ ipPort + "\\]\n") || string.matches("peers [a-zA-Z_0-9]+ \\[" + ipPort + " +" + ipPort + "\\]\n"))
        {
            //System.out.println("this is a PEERS like message");
            return true;
        }        
        if (string.matches("data [a-zA-Z_0-9]+ \\[" + indexPiece + "\\]\n") || string.matches("data [a-zA-Z_0-9]+ \\[" + indexPiece + " +" + indexPiece + "\\]\n"))
        {
            //System.out.println("this is a DATA like message");
            return true;
        }

        if (string.matches("getpieces [a-zA-Z_0-9]+ \\[" + index + "\\]\n") || string.matches("getpieces [a-zA-Z_0-9]+ \\[" + index + " +" + index + "\\]\n"))
        {
            //System.out.println("this is a GETPIECES like message");
            return true;
        }
        if (string.matches("interested [a-zA-Z_0-9]+\n"))
        {
            //System.out.println("this is a INTERESTED like message");
            return true;
        }
        if (string.matches("have [a-zA-Z_0-9]+ [01]+\n"))
        {
            //System.out.println("this is a HAVE like message");
            return true;
        }
        throw new Exception();
    }
    public static String[] parse(String string) {
        String[] tokens = {"incorrect"};
        try {
            if (Parsing.parse__bool(string.replaceAll("\\s]+", "]"))) {
                tokens = string.replace("[", "").replace("]", "").replace("\n", "").trim().split(" ");
                return tokens;
                }
            }
        catch(Exception e) {
                System.out.println("Something went wrong: message not recognised");
            }
        return tokens;
    }

    /*public static void main(String[] args) {
        String lol = "abcabc";
        System.out.println(lol.matches("(abc)+"));
        String[] P1={"aaa","bbb","ccc","ddd"};
        String[] P2={"eeee","ffff","gggg","hhhh","pppp"};
        String[] P3={"ss","tt","vv"};

        FileInfo file1 = new FileInfo("test1.txt", 3, 4, "4566", P1 );
        FileInfo file2 = new FileInfo("test2.txt", 4, 5, "4577", P2 );
        FileInfo file3 = new FileInfo("test3.txt", 2, 3, "4588", P3 );

        FileInfo[] Files={file1,file2,file3};
        String[] buffermap={"1101","11111","100"};
        Peers peer = new Peers("128.68.22.15", 88, Files, buffermap,"./Peer1/seed","./Peer1/leech");
        byte[] str = peer.have("4566");
        System.out.println(new String(str));
        int[] pieces={1,3,4};
       // System.out.println(new String (peer.data("4577",pieces)));


        
        //String string = "getpieces d4f51dsfsd4f65ze4r56ddf45sd [1 8]\n";
        //String string = "list [file_a.dat 2097152 1024 18qe4f5s4ef54sef5sd4f5s4d5f4sd5f4s file_b.dat 545 66 5a4zd5]\n";
        String string = "peers qfqdAZER864SD532148ZE6R789Ef84555 [167.12.4.5:8080 12.12.12.12:2222]\n";
        //String string = "data qsd5qsd8qsd [8:01001100111001 5:10100]\n";
        String[] marie = Parsing.parse(string);
        for (int i = 0; i < marie.length; i++)
            System.out.println(marie[i]);
        String[] jess = Parsing.parse(new String("je suis un test negatif"));
    }*/
 }