import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by admin on 2017/5/31.
 */
public class AesDaoImp implements AesDao{
    //这里swing 用于获取界面对象，向前台传递信息，不需要情况下可以删掉
    private SwingAES swing = null;
    public void setSwing(SwingAES swing){
        this.swing = swing;
    }
    private void setMessage(String message){
        if(swing!=null)
            swing.setTextArea(message);
    }

    private void setProgressBarMax(int n){
        if(swing!=null)
            swing.getProgressBar().setMaximum(n);
    }
    public void progressBarMoveTo(int i){
        if(swing!=null)
            new Thread(){
                public void run(){
                    swing.getProgressBar().setValue(i);
                }
            }.start();
    }
    public AesDaoImp(){

    }


    private static int const_packLength = Constant.AES_pack_length; //默认长度

    /**
     *文本加密
     * **/
    public byte[] textCipher(Aes aes, byte[] plainText , int plain_length) {

        byte[] cipherText ; //密文

        int cipherTimes = plain_length / const_packLength ; //加密次数
        int last_length = plain_length % const_packLength;

        if(last_length == 0)
            cipherText = new byte[plain_length]; //密文
        else
            cipherText = new byte[(cipherTimes+1) * const_packLength];

        for(int i = 0 ; i < cipherTimes ; i++){
            byte[] temp_text = new byte[const_packLength];
            System.arraycopy(plainText,i * const_packLength,temp_text,0,16);
          //  for(int j = 0 ; j < const_packLength ; j ++)
          //      temp_text[j] = plainText[i * const_packLength + j];

            byte[] temp_cipherText = aes.cipher(temp_text);//一次加密
            //一次加密结果加入到密文中
            System.arraycopy(temp_cipherText,0,cipherText,i*const_packLength,16);
           // for(int j = 0 ; j < const_packLength ; j++)
           //     cipherText[i*const_packLength + j] = temp_cipherText[j];
        }
        //如果不为16倍数，补一次加密，
        if(last_length != 0 ){
            byte[] temp_text = new byte[last_length];

            System.arraycopy(plainText,cipherTimes * const_packLength,temp_text,0,last_length);
         //   for(int j = 0 ; j< last_length; j++)
          //      temp_text[j] = plainText[cipherTimes * const_packLength + j];
            byte[] temp_cipherText = aes.cipher(temp_text);//一次加密
            //一次加密结果加入到密文中
            System.arraycopy(temp_cipherText,0,cipherText,cipherTimes * const_packLength,16);
       //     for(int j = 0 ; j < const_packLength ; j++)
          //      cipherText[cipherTimes * const_packLength + j] = temp_cipherText[j];
        }

        return cipherText;
    }

    @Override
    /**
     *文本解密
     * **/
    public byte[] textInvCliper(Aes aes, byte[] cipherText, int cipher_length) {

        int cipherTimes = cipher_length / const_packLength;

        byte[] invCipherText =  new byte[cipher_length];

        for(int i = 0 ; i < cipherTimes; i++ ){
            byte[] temp_cipherText = new byte[const_packLength];

            System.arraycopy(cipherText,i * const_packLength,temp_cipherText,0,16);
         //   for(int j = 0 ; j < const_packLength; j++)
          //      temp_cipherText[j] = cipherText[i * const_packLength + j];

            byte[] temp_invCipherText = aes.invCipher(temp_cipherText);
            System.arraycopy(temp_invCipherText,0,invCipherText,i * const_packLength,16);
        //    for(int j = 0 ; j < const_packLength ; j++)
          //      invCipherText[ i * const_packLength + j] = temp_invCipherText[j];
        }
        return invCipherText;
    }


    /****CBC****/
    private byte[] Rcon ;
    //初始化
    public void initRcon(){
        Rcon = new byte[const_packLength];
        for(int i = 0;i<const_packLength;i++)
            Rcon[i] = 0x00;
    }
    //异或
    private byte[] xorRcon(byte[] word){
        for(int i = 0; i<const_packLength;i++)
            word[i] = (byte) (word[i] ^ Rcon[i]);
        return  word;
    }
    //变换初始RCON
    private void updateRcon(byte[] word){
        for(int i = 0; i<const_packLength;i++)
            Rcon[i] = word[i];
    }
    /**
     *文本加密
     * **/
    public byte[] textCipherCBC(Aes aes, byte[] plainText , int plain_length) {

        byte[] cipherText ; //密文

        int cipherTimes = plain_length / const_packLength ; //加密次数
        int last_length = plain_length % const_packLength;

        if(last_length == 0)
            cipherText = new byte[plain_length]; //密文
        else
            cipherText = new byte[(cipherTimes+1) * const_packLength];

        for(int i = 0 ; i < cipherTimes ; i++){
            byte[] temp_text = new byte[const_packLength];
            for(int j = 0 ; j < const_packLength ; j ++)
                temp_text[j] = plainText[i * const_packLength + j];

            temp_text = xorRcon(temp_text);//CBC xor
            byte[] temp_cipherText = aes.cipher(temp_text);  //一次加密
            updateRcon(temp_cipherText); //CBC rcon update

            //一次加密结果加入到密文中
            for(int j = 0 ; j < const_packLength ; j++)
                cipherText[i*const_packLength + j] = temp_cipherText[j];
        }
        //如果不为16倍数，补一次加密，
        if(last_length != 0 ){
            byte[] temp_text = new byte[const_packLength];
            for(int j = 0 ; j< last_length; j++)
                temp_text[j] = plainText[cipherTimes * const_packLength + j];
            for(int j = last_length; j< const_packLength;j++){ //因为要CBC需要16补全所以，不足16补全
                temp_text[j] = 0x00;
            }

            temp_text = xorRcon(temp_text);//CBC xor
            byte[] temp_cipherText = aes.cipher(temp_text);  //一次加密
            updateRcon(temp_cipherText); //CBC rcon update

            //一次加密结果加入到密文中
            for(int j = 0 ; j < const_packLength ; j++)
                cipherText[cipherTimes * const_packLength + j] = temp_cipherText[j];
        }

        return cipherText;
    }

    /**
     *文本解密
     * **/
    @Override
    public byte[] textInvCliperCBC(Aes aes, byte[] cipherText, int cipher_length) {

        int cipherTimes = cipher_length / const_packLength;

        byte[] invCipherText =  new byte[cipher_length];


        for(int i = 0 ; i < cipherTimes; i++ ){
            byte[] temp_cipherText = new byte[const_packLength];

            for(int j = 0 ; j < const_packLength; j++)
                temp_cipherText[j] = cipherText[i * const_packLength + j];

            byte[] temp_invCipherText = aes.invCipher(temp_cipherText); //解密
            temp_invCipherText =  xorRcon(temp_invCipherText);
            updateRcon(temp_cipherText);


            for(int j = 0 ; j < const_packLength ; j++)
                invCipherText[ i * const_packLength + j] = temp_invCipherText[j];
        }
        return invCipherText;
    }


    /**
     * 文件读取要写头部 2个部分，一个是读取次数，一个是尾部长度
     * **/
    @Override
    public void fileClipher(Aes aes, String type, String input, String output, int read_length) {

        File f= new File(input);
        long cipherTimes = f.length() / read_length;
        int last_length =(int) (f.length() % read_length);
        if(last_length != 0)
            cipherTimes++;

        String message = "*********单线程文件加密********\n"
                +input+"\n"
                +"文件大小为"+f.length()+" bytes \n"
                +"每次读取 "+read_length + " bytes"+" 要读取"+cipherTimes+"次"+"\n"
                +"尾部文件长度为："+last_length+"\n"
                +"文件开始加密"+"\n";
        System.out.println(message);
        setMessage(message);
        int index = 0;
        setProgressBarMax((int)cipherTimes);

        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数

        FileInputStream fis = null;
        FileOutputStream fos = null;

        byte[] buffer = new byte[read_length];
        int temp = 0;
        try{
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);

            /**加密写入头部数据**/
            String str_read_length = String.valueOf(read_length); //每次读取的大小
            String str_last_length = String.valueOf(last_length); //尾长

                fos.write(textCipher(aes,str_read_length.getBytes(),str_read_length.getBytes().length),0,16);
                fos.write(textCipher(aes,str_last_length.getBytes(),str_last_length.getBytes().length),0,16);

            if(type == "ECB") {
           //     System.out.println("ECB加密模式");
                while (true) {
                    temp = fis.read(buffer, 0, read_length);
                    index++;
                    progressBarMoveTo(index);
                    if (temp == -1) {
                        break;
                    }
                    byte[] out_buffer = textCipher(aes, buffer, temp);
                    fos.write(out_buffer, 0, out_buffer.length);
                }
            }else if(type == "CBC"){
           //     System.out.println("CBC加密模式");
                initRcon();
                while (true) {
                    temp = fis.read(buffer, 0, read_length);
                    index++;
                    progressBarMoveTo(index);
                    if (temp == -1) {
                        break;
                    }
                    byte[] out_buffer = textCipherCBC(aes, buffer, temp);
                    fos.write(out_buffer, 0, out_buffer.length);
                }
            }

        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            try{
                fis.close();
                fos.close();
            }
            catch(Exception e2){
                System.out.println(e2);
            }
        }

        long endMili=System.currentTimeMillis();

        message = "文件加密结束"+"\n"
                +"总耗时为："+(endMili-startMili)+"毫秒  "+"约为"+(endMili-startMili)/1000.0+"秒"+"\n"
                +"****************************"+"\n";
        setMessage(message);
        System.out.println(message);

    }

    @Override
    public void fileInvClipher(Aes aes, String type, String input, String output) {

        File f= new File(input);

        System.out.println("*********单线程文件解密********");
        System.out.println("文件开始解密 ");
        setMessage("*********单线程文件解密********\n文件开始解密");
        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数

        FileInputStream fis = null;
        FileOutputStream fos = null;

        int temp = 0;
        try{
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);

            /**读取头部信息**/
            byte[] read_head = new byte[const_packLength];
            fis.read(read_head,0, 16);
            int read_length  =  Integer.parseInt(new String(textInvCliper(aes,read_head,const_packLength)).trim()); //每次读取的大小
            fis.read(read_head,0, 16);
            int last_length = Integer.parseInt(new String(textInvCliper(aes,read_head,const_packLength)).trim()); //尾长

            long cipherTimes = (f.length()-32) / read_length ;
            if( (f.length()-32) % read_length !=0) cipherTimes++;


            setProgressBarMax((int)cipherTimes);

            System.out.println("文件大小为："+f.length());
            System.out.println("每次读取大小为："+read_length+"bytes 要读取"+cipherTimes+"次");
            System.out.println("尾部长度为："+last_length);

            setMessage("文件大小为："+f.length()+"\n每次读取大小为："+read_length+"bytes 要读取"+cipherTimes+"次\n"
            +"尾部长度为："+last_length+"\n");

            //如果尾部长度为0，而文件长度大于等于读取长度，则说明正好除尽。则需要尾部长度置为文件每次读取长度，从而最后一次可以正常输出
            if(((f.length()- 32) >= read_length ) && (last_length == 0))
                last_length = read_length;

            byte[] buffer = new byte[read_length];

            if(type == "ECB") {
              //  System.out.println("ECB解密模式");
                //进行前cipherTimes - 1 次解密
                for (int i = 0; i < cipherTimes - 1; i++) {
                    temp = fis.read(buffer, 0, read_length);
                    byte[] out_buffer = textInvCliper(aes, buffer, read_length);
                    fos.write(out_buffer, 0, out_buffer.length);
                    progressBarMoveTo(i);
                }

                //最后一次读取
                temp = fis.read(buffer, 0, read_length);
                byte[] out_buffer = textInvCliper(aes, buffer, temp); //这里temp 为实际读取数据量，而且，加密时会补上16倍数，所有一定为16倍数
                fos.write(out_buffer, 0, last_length); //最后写出实际尾部长度


            }else if(type == "CBC"){
             //   System.out.println("CBC解密模式");
                initRcon();
                //进行前cipherTimes - 1 次解密
                for (int i = 0; i < cipherTimes - 1; i++) {
                    temp = fis.read(buffer, 0, read_length);
                    byte[] out_buffer = textInvCliperCBC(aes, buffer, read_length);
                    fos.write(out_buffer, 0, out_buffer.length);
                    progressBarMoveTo(i);
                }

                //最后一次读取
                temp = fis.read(buffer, 0, read_length);
                byte[] out_buffer = textInvCliperCBC(aes, buffer, temp); //这里temp 为实际读取数据量，而且，加密时会补上16倍数，所有一定为16倍数
                fos.write(out_buffer, 0, last_length); //最后写出实际尾部长度

            }
            progressBarMoveTo((int)cipherTimes);

        }
        catch(Exception e){
            System.out.println(e);
        }
        finally{
            try{
                fis.close();
                fos.close();
            }
            catch(Exception e2){
                System.out.println(e2);
            }
        }
        long endMili=System.currentTimeMillis();

        String msessage = "文件解密结束\n"
                +"总耗时为："+(endMili-startMili)+"毫秒  "+"约为"+(endMili-startMili)/1000.0+"秒\n"
                +"****************************\n";
        System.out.println(msessage);
        setMessage(msessage);

    }

/*
    public static void main(String args[]){
        String keys = "1234567890123456";
        Aes aes = new Aes(Aes.KeySize.Bits128, keys.getBytes());
        String text = "12345678901234561234567890123456";

        AesDaoImp aesDaoImp = new AesDaoImp();

        System.out.println(Util.getPath_cipher("C:/AES./txt/新建文本文档.txt"));
        System.out.println(Util.getPath_invCipher("C:/AES./txt/新建文本文档_cipher.txt"));

    }*/

}
