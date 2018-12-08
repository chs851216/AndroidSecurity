package csie.mcu.edu.tw.group5.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class AndroidFileClient {

    private static final int ZERO = 0x00000000;

    private int flag;
    private int fileSize;
    private String result;

    private byte[] buffer = new byte [8192];

    private ArrayList<String> urlScore;

    private Socket socket;
    private FileInputStream FileInputStream;
    private DataInputStream DataInputStream;
    private DataOutputStream DataOutputStream;

    public AndroidFileClient(String host, int port, String file, int flag) {
        this.result = "";
        this.flag = flag;
        try {
            this.socket = new Socket(host, port);
            this.DataOutputStream = new DataOutputStream(this.socket.getOutputStream());
            this.DataInputStream = new DataInputStream(this.socket.getInputStream());
            this.FileInputStream = new FileInputStream(file);
            this.buffer = new byte[8192];
            this.fileSize = this.FileInputStream.available();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if(this.socket == null) return false;
        else return true;
    }

    public String connect() throws IOException {
        return this.DataInputStream.readUTF();
    }

    public void sendFlag() throws IOException {
        this.DataOutputStream.writeByte(this.flag);
    }

    public void sendFileSize() throws IOException {
        this.DataOutputStream.writeInt(this.fileSize);
    }

    public void sendFile() throws IOException {
        while(this.fileSize > 0) {
            int len = this.FileInputStream.read(this.buffer, 0, this.buffer.length);
            this.DataOutputStream.write(this.buffer, 0, len);
            this.fileSize -= len;
        }
    }

    public String recvDecodeStart() throws IOException {
        return this.DataInputStream.readUTF();
    }

    public String recvDecodeEnd() throws IOException {
        return this.DataInputStream.readUTF();
    }

    public String detectStart() throws IOException {
        return this.DataInputStream.readUTF();
    }

    public String detectEnd() throws IOException {
        return this.DataInputStream.readUTF();
    }

    public int recievevUrlCount() throws IOException {
        return this.DataInputStream.readInt();
    }

    public String recvResultWOT() throws IOException {
        int size = this.DataInputStream.readInt();
        this.receieveMessage(size);
        return this.printArrayList(this.urlScore);
    }

    public String recvResultVT() throws IOException {
        int size = this.DataInputStream.readInt();
        this.receieveMessage(size);
        return this.printArrayList(this.urlScore);
    }

    private String printArrayList(ArrayList<String> arrayList) {
        StringBuilder stringBuilder = new StringBuilder();
        // lambda for java jdk 1.8
        //arrayList.forEach(score -> stringBuilder.append(score + "\r\n"));

        for(String urlScore : arrayList) {
            stringBuilder.append(urlScore + "\r\n");
        }

        return stringBuilder.toString();
    }

    public String recvResultPermission() throws IOException {
        this.result = result;
        return this.DataInputStream.readUTF();
    }

    public void close() throws IOException {
        this.DataOutputStream.flush();
        this.DataOutputStream.close();
        this.DataInputStream.close();
    }

    private boolean receieveMessage(int size) {
        this.urlScore = new ArrayList<>();

        String tmp;
        while(size > 0) {
            try {
                tmp = this.DataInputStream.readUTF();
                this.urlScore.add(tmp);
                size -= 1;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		int flag = 7;
        //permission is 1
        //static URL is 2
        //dynamic URL is 4
        //ex permission and dynamic is 1 + 4 = 5
        AndroidFileClient fileClient = new AndroidFileClient("127.0.0.1", 32954, "HONEYMOON_v1.0_apkpure.com.apk", flag);
        System.out.println(fileClient.connect());
        fileClient.sendFlag();
        fileClient.sendFileSize();
        fileClient.sendFile();
        System.out.println(fileClient.recvDecodeStart());
        System.out.println(fileClient.recvDecodeEnd());
        System.out.println(fileClient.detectStart());
        System.out.println(fileClient.detectEnd());

        if((flag & 1) != ZERO) {
			System.out.println(fileClient.recvResultPermission());
		}

		if((flag & 2) != ZERO) {
			System.out.println(fileClient.recievevUrlCount());
			System.out.println(fileClient.recvResultWOT());
			System.out.println(fileClient.recvResultVT());
		}
        fileClient.close();
    }
}