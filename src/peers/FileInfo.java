public class FileInfo {
    private String name = "";
    private int pieceSize;
    private int nbrPieces;
    private String key;

    public FileInfo(String name, int pieceSize, int nbrPieces, String key) {
        this.name = name;
        this.pieceSize = pieceSize;
        this.nbrPieces = nbrPieces;
        this.key = key;
    }

    // public File(int pieceSize, int nbrPieces, String key) {
    //     this.pieceSize = pieceSize;
    //     this.nbrPieces = nbrPieces;
    //     this.key = key;
    // }

    public String getName() {
        return this.name;
    }

    public int getNbrPieces() {
        return this.nbrPieces;
    }

    public int getPieceSize() {
        return this.pieceSize;
    }
    public int getLength() {
        return this.pieceSize*this.nbrPieces;
    }

    public String getKey() {
        return this.key;
    }

    public int getSize() {
        return this.nbrPieces * this.pieceSize;
    }

}