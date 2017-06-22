/**
 * Created by admin on 2017/5/31.
 */
public interface AesDao {

    public void setSwing(SwingAES swing); //前台信息传递
    public abstract byte[] textCipher(Aes aes, byte[] plainText, int plain_length); //文本加密

    public abstract byte[] textInvCliper(Aes aes, byte[] cipherText, int cipher_length);//文本解密

    public void initRcon(); //用于CBC初始化

    public abstract byte[] textCipherCBC(Aes aes, byte[] plainText, int plain_length); //文本加密

    public abstract byte[] textInvCliperCBC(Aes aes, byte[] cipherText, int cipher_length);//文本解密

    public abstract void fileClipher(Aes aes, String type, String input, String output, int read_length);

    public abstract void fileInvClipher(Aes aes, String type, String input, String output);



}

