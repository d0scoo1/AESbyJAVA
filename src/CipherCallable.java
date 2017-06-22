import java.util.concurrent.Callable;

/**
 * Created by vickey on 2017/6/1.
 * 多线程ECB文本加密
 */
public class CipherCallable implements Callable {

    private byte[] plainText;
    private Aes aes;


    CipherCallable(Aes aes, byte[] plainText){
        this.aes = aes;
        this.plainText = plainText;
    }

    @Override
    public byte[] call() throws Exception {

        byte[] cipherText = new byte[AesPower.AesPower_packLength];

        for(int i = 0; i < AesPower.AesPower_packTimes ; i++){
            byte[] temp_text = new byte[16];

            System.arraycopy(plainText,i * 16,temp_text,0,16);

            byte[] temp_cipherText = aes.cipher(temp_text);//一次加密

            //一次加密结果加入到密文中
            System.arraycopy(temp_cipherText,0,cipherText,i*16,16);

        }
        return cipherText;
    }

}

