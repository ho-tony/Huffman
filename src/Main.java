import java.io.*;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        String res = compress("research triangle rotation dividend weakness survival reckless analysis surprise opponent emphasis practice restless unlikely monopoly decisive password relation workshop forecast profound civilian interest midnight creation behavior exchange perceive aviation ordinary ideology exercise resident");
        System.out.println(res);
    }

    private static String compress(String s) {
        if (s.isEmpty() || s.length() == 1) return s;
        int[] freqTable = new int[128];

        //regular for loop due to string immutability
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            freqTable[c]++;
        }

        PriorityQueue<Node> pq = new PriorityQueue<>((a,b) -> a.freq - b.freq);

        for (int i = 0; i < freqTable.length; i++) {
            if (freqTable[i] == 0) continue;
            pq.add(new Node((char) i, freqTable[i]));
        }
        if (pq.isEmpty()) return s;

        Node leastFreqNode = pq.poll();

        Node root = createHuffmanTree(pq, freqTable, leastFreqNode);
        saveHuffmanTree("tree", root);

        String[] encodeTable = new String[128];

        createCharacterCodes(new StringBuilder(), root, encodeTable);


        String compressedString = encode(s, encodeTable);
        createEncodedFile(compressedString);
        return compressedString;
    }

    public static void createEncodedFile(String s) {
        System.out.println(s.length());
        BitSet bs = new BitSet(1);

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') bs.set(i);
        }

        byte[] bytes = bs.toByteArray();
        try {
            FileOutputStream fileOut = new FileOutputStream("compressed.bin");
            fileOut.write(bytes);
            fileOut.close();

        } catch (IOException x) {
            System.out.println("An error has been found creating encoded file");
        }
    }

    /*
    Have to append this to the start of the bytestream of the huffman generated code later on.
     */
    public static void saveHuffmanTree(String name, Node treeRoot) {
        try {
            FileOutputStream fileOut = new FileOutputStream(name + ".bin");
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(treeRoot);
            objectOut.close();
            fileOut.close();

        } catch (IOException x) {
            System.out.println("File output error found");
        }
    }

    private static void createCharacterCodes(StringBuilder cur, Node n, String[] encodeTable) {
        if (n == null) return;
        encodeTable[n.ch] = cur.toString();
        createCharacterCodes(cur.append('0'), n.left, encodeTable);
        cur.deleteCharAt(cur.length() - 1);
        createCharacterCodes(cur.append('1'), n.right, encodeTable);
        cur.deleteCharAt(cur.length() - 1);
    }

    private static String encode(String s, String[] encodeTable) {

        for (int i = 0; i < encodeTable.length; i++) {
            if (encodeTable[i] == null) continue;
            String ch = "" + (char) i;
            s = s.replace("" + (char) i, encodeTable[i]);
        }

        return s;
    }


    private static String decode(String s) {
        return "";
    }

    private static Node createHuffmanTree(PriorityQueue<Node> pq, int[] freqTable, Node adjNode) {
        if (pq.isEmpty()) return adjNode;
        Node lowestNode = pq.poll();

        Node parentNode = new Node(adjNode.freq + lowestNode.freq);


        if (adjNode.freq <= lowestNode.freq) {
            parentNode.left = lowestNode;
            parentNode.right = adjNode;
        } else {
            parentNode.left = adjNode;
            parentNode.right = lowestNode;
        }

        return createHuffmanTree(pq, freqTable, parentNode);

    }

    private static class Node implements Serializable {
        Node left;
        Node right;
        char ch;
        int freq;


        private Node() {}
        private Node(int freq) {
            this.freq = freq;
        }
        private Node(char ch, int freq) {
            this.ch = ch;
            this.freq = freq;
        }
    }
}
