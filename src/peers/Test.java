//import java.net.*;
import java.io.IOException;
class Test{
    public static void main(String[] args) throws IOException {
        String[] P1={"aaa","bbb","ccc","ddd"};
        String[] P2={"eeee","ffff","gggg","hhhh","pppp"};
        String[] P3={"ss","tt","vv"};

        FileInfo file1 = new FileInfo("test1.txt", 3, 4, "4566", P1 );
        FileInfo file2 = new FileInfo("test2.txt", 4, 5, "4577", P2 );
        FileInfo file3 = new FileInfo("test3.txt", 2, 3, "4588", P3 );

        FileInfo[] Files={file1,file2,file3};
        String[] buffermap={"1111","11111","100"};
        String Dir="/media/hedi/D/free-ProjetReseauxG1/src/peers/Peer1";
        Peers peer = new Peers("128.68.22.15", 88, Files, buffermap,Dir);
        System.out.println(peer.announce());
        System.out.println(peer.update());
        System.out.println(peer.have("4566"));
        byte[] str = peer.have("4566");
        System.out.println(new String(str));
        /*int[] P1Manquant = {5,6,7};
        System.out.println(peer.getpieces("4566", P1Manquant));*/
        int[] pieces={1,2,3,4,5};
        //System.out.println(peer.data("4588",pieces));
        /*peer.writePart(Dir+"/seed/test1.txt", 1,peer.getPraties(Dir+"/leech/test3.txt", 1));
        peer.writePart(Dir+"/seed/test1.txt", 3,peer.getPraties(Dir+"/leech/test3.txt", 3));
        peer.writePart(Dir+"/seed/test1.txt", 2,peer.getPraties(Dir+"/leech/test3.txt", 2));*/
       
       
       /* peer.writePart(Dir+"/seed/test2.txt", 2,peer.getPraties(Dir+"/leech/test3.txt", 2));
        peer.writePart(Dir+"/seed/test2.txt", 0,peer.getPraties(Dir+"/leech/test3.txt", 0));*/
       //peer.writePart(Dir+"/leech/test22.webp", 0,peer.getPraties(Dir+"/seed/image.webp", 0));
       peer.writePart(Dir+"/leech/test12.jpg", 4,peer.getPraties(Dir+"/seed/image2.jpg", 4));
       peer.writePart(Dir+"/leech/test12.jpg", 0,peer.getPraties(Dir+"/seed/image2.jpg", 0));
       peer.writePart(Dir+"/leech/test12.jpg", 1,peer.getPraties(Dir+"/seed/image2.jpg", 1));
       peer.writePart(Dir+"/leech/test12.jpg", 3,peer.getPraties(Dir+"/seed/image2.jpg", 3));
       peer.writePart(Dir+"/leech/test12.jpg", 2,peer.getPraties(Dir+"/seed/image2.jpg", 2));
       peer.writePart(Dir+"/leech/test12.jpg", 5,peer.getPraties(Dir+"/seed/image2.jpg", 5));

    }
}