import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * 多线程加密
 * Created by vickey on 2017/6/1.
 */
public class AesPower {
    //这里swing 用于获取界面对象，向前台传递信息，不需要情况下可以删掉
    private SwingAES swing;
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

    private static int const_packLength = Constant.AES_pack_length; //默认长度

    public static int AesPower_packLength ; //每个线程处理长度，必须为16的倍数
    public static int AesPower_packTimes ;  //读取次数 = 每组的长度/16 = (int)(1e6)

    private String keys = "1234567890123456";
    private Aes.KeySize keySize = Aes.KeySize.Bits128;

    public Aes getAesPower_aes(){
        return new Aes(keySize,keys.getBytes());
    }

    private static int AesPower_count;// 线程数 = 每次读取长度/每组的长度 = 2
    private static int AesPower_readLength ;//= (int)(2*16e6)


    /**
     * @param readLength //每次读取数
     * @param packLength
     *
     * **/
    AesPower(String keys, Aes.KeySize keySize, int readLength, int packLength){

        this.keys= keys;
        this.keySize = keySize;
        this.AesPower_readLength = readLength;
        this.AesPower_count = readLength/packLength;
        this.AesPower_packLength = packLength;
        this.AesPower_packTimes = packLength/16;
    }

    public void fileClipher(String input,String output){

        File f = new File(input);
        long cipherTimes = f.length() / AesPower_readLength;
        int last_length =(int) (f.length() % AesPower_readLength);

        int add_length = 0 ;

        if(last_length != 0) {
            cipherTimes++;
            add_length = 16 - (int)(f.length()%16);
        }

        String message = "**********多线程文件加密**********\n"
                + input +"\n"
                + "文件大小为"+f.length()+"bytes"+"\n"
                + "每次读取 "+AesPower_readLength+ " bytes"+" 要读取"+cipherTimes+"次"+"\n"
                + "尾部文件长度为："+last_length+"\n"
                + AesPower_count+"个线程进行加密"+"\n"
                + "文件开始加密"+"\n";

        setMessage(message);
        System.out.println(message);
        setProgressBarMax((int)cipherTimes);
        int index = 0;
        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数

        FileInputStream fis = null;
        FileOutputStream fos = null;

        byte[] buffer = new byte[AesPower_readLength];
        int temp = 0;
        try{
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);
            AesDao aesDao = new AesDaoImp();
            /**加密写入头部数据**/
            String header_add_length = String.valueOf(add_length); //最后不足16K，补足的长度
            fos.write(aesDao.textCipher(getAesPower_aes(),header_add_length.getBytes(),header_add_length.getBytes().length),0,16);
     //       String str_read_length = String.valueOf(read_length); //每次读取的大小
     //       String str_last_length = String.valueOf(last_length); //尾长

       //     fos.write(textCipher(aes,str_read_length.getBytes(),str_read_length.getBytes().length),0,16);
      //      fos.write(textCipher(aes,str_last_length.getBytes(),str_last_length.getBytes().length),0,16);

                while (true) {
                    temp = fis.read(buffer, 0, AesPower_readLength);

                    if (temp == -1) {
                        break;
                    }
                    //如果不是尾部，则进行多线程加密
                    if(temp != last_length){
                        //创建一个线程池
                        ExecutorService pool = Executors.newFixedThreadPool(AesPower_count);
                        Future<byte[]>[] futures = new Future[AesPower_count];
                      for(int i = 0; i <AesPower_count ;i++){
                            byte[] temp_plainText = new byte[AesPower_packLength];
                            System.arraycopy(buffer,i*AesPower_packLength,temp_plainText,0,AesPower_packLength);
                            futures[i]= pool.submit(new CipherCallable(getAesPower_aes(),temp_plainText));
                        }
                        byte[] temp_cipher = new byte[AesPower_readLength];

                        for(int i = 0 ; i < AesPower_count ; i++){
                            System.arraycopy(futures[i].get(),0,temp_cipher,i*AesPower_packLength,AesPower_packLength);
                        }
                        fos.write(temp_cipher, 0,AesPower_readLength);
                        pool.shutdown();//关闭线程池
                        index++;
                        progressBarMoveTo(index);

                    }else{ //尾部进行最后一次非多线程加密
                        byte[] out_buffer = aesDao.textCipher(getAesPower_aes(), buffer, temp);
                        fos.write(out_buffer, 0, out_buffer.length);
                        index++;
                        progressBarMoveTo(index);
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

        message ="文件加密结束"+"\n"
                +"总耗时为："+(endMili-startMili)+"毫秒  "+"约为"+(endMili-startMili)/1000.0+"秒"+"\n"
                +"****************************"+"\n";
        setMessage(message);
        System.out.println(message);

    }

    public void fileInvClipher(String input, String output) {
        File f= new File(input);

        int header_length = 16 ; //头长度
        long file_length = f.length()-header_length;
        long invCipherTimes = file_length / AesPower_readLength;
        int last_length =(int) (file_length % AesPower_readLength);
        if(last_length != 0) {
            invCipherTimes++;
        }else{
            last_length = AesPower_readLength; //如果正好除尽，则最后一次为读入长度，而不为0
        }

        String message = "**********多线程文件解密**********\n"
                +"文件开始解密";
        System.out.println(message);
       setMessage(message);
        setProgressBarMax((int)invCipherTimes);
        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数
        FileInputStream fis = null;
        FileOutputStream fos = null;


        int temp = 0;
        try{
            fis = new FileInputStream(input);
            fos = new FileOutputStream(output);
            AesDao aesDao = new AesDaoImp();
            int remove_last = 0; //这里是增加的位

            /**读取头部信息**/
            byte[] read_head = new byte[const_packLength];
            temp = fis.read(read_head,0, 16);
            read_head = aesDao.textInvCliper(getAesPower_aes(),read_head,const_packLength);
            remove_last = Integer.parseInt(new String(read_head).trim());
            message = "remove_last :" + remove_last +"\n"
                    +"文件大小为："+f.length() +"\n"
                    +"每次读取大小为："+ AesPower_readLength+"bytes 要读取"+invCipherTimes +"次" +"\n"
                    +"尾部长度为："+last_length +"\n"
                    + AesPower_count+"个线程进行解密"+"\n";
            setMessage(message);
            System.out.println(message);

            byte[] buffer = new byte[AesPower_readLength];

            //前n-1次用多线程解密
            for(int index = 0 ; index<invCipherTimes - 1;index++){
                temp = fis.read(buffer, 0, AesPower_readLength);
                ExecutorService pool = Executors.newFixedThreadPool(AesPower_count);
                Future<byte[]>[] futures = new Future[AesPower_count];
                for(int i = 0; i <AesPower_count ;i++){
                    byte[] temp_clipherText = new byte[AesPower_packLength];
                    System.arraycopy(buffer,i*AesPower_packLength,temp_clipherText,0,AesPower_packLength);
                    futures[i]= pool.submit(new InvCipherCallable(getAesPower_aes(),temp_clipherText));
                }
                byte[] temp_invCipher = new byte[AesPower_readLength];

                for(int i = 0 ; i < AesPower_count ; i++){
                    System.arraycopy(futures[i].get(),0,temp_invCipher,i*AesPower_packLength,AesPower_packLength);
                }
                fos.write(temp_invCipher, 0,AesPower_readLength);
                pool.shutdown();//关闭线程池
                progressBarMoveTo(index);
            }
                //最后一次读取
                temp = fis.read(buffer, 0,last_length);
                byte[] out_buffer = aesDao.textInvCliper(getAesPower_aes(),buffer,last_length); //这里temp 为实际读取数据量，而且，加密时会补上16倍数，所有一定为16倍数
                fos.write(out_buffer, 0, last_length-remove_last); //最后写出实际尾部长度
                progressBarMoveTo((int)invCipherTimes);
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

        message = "文件解密结束\n"
                + "总耗时为："+(endMili-startMili)+"毫秒  "+"约为"+(endMili-startMili)/1000.0+"秒\n"
                + "****************************\n";
        setMessage(message);
        System.out.println(message);

    }


/*
    public static void main(String[] args) throws ExecutionException, InterruptedException {


        String keys = "1234567890123456";
        Aes aes = new Aes(Aes.KeySize.Bits128, keys.getBytes());
      //  AesPower aesPower = new AesPower(aes,(int)(2*16e6),(int)(16e6));
        AesPower aesPower = new AesPower(keys,Aes.KeySize.Bits128,(int)(6*16e1),(int)(16e1));

        aesPower.fileClipher("C:\\AES\\txt\\柏林墙下演说.txt","C:\\AES\\txt\\柏林墙下演说1.txt");
        aesPower.fileInvClipher("C:\\AES\\txt\\柏林墙下演说1.txt","C:\\AES\\txt\\柏林墙下演说2.txt");
    }*/
}
