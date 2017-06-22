import java.util.concurrent.Callable;

/**
 * Created by vickey on 2017/6/1.
 * 多线程ECB文本解密
 */
public class InvCipherCallable implements Callable {

    private byte[] cipherText;
    private Aes aes;

    InvCipherCallable(Aes aes, byte[] cipherText){
        this.aes = aes;
        this.cipherText = cipherText;
    }

    @Override
    public byte[] call() throws Exception {


        byte[] invCipherText = new byte[AesPower.AesPower_packLength];

        for(int i = 0 ; i <AesPower.AesPower_packTimes ; i++){
            byte[] temp_text = new byte[16];

            System.arraycopy(cipherText,i * 16,temp_text,0,16);

            byte[] temp_invCipherText = aes.invCipher(temp_text);//一次解密

            //一次解密结果加入到结果中
            System.arraycopy(temp_invCipherText,0,invCipherText,i*16,16);

        }
        return invCipherText;
    }
}
