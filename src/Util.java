/**
 * Created by admin on 2017/6/1.
 */
public  class Util {

    private String path_cipher ;
    private String path_invCipher;

    public static String getPath_cipher(String path){
       return path.replaceAll("\\.","_cipher.");
    }
    public static String getPath_invCipher(String path){
        return path.replaceAll("\\.","_inv.");
    }

    /**
     * 16进制format
     * **/
    public static String formatHex(byte[] bytes){

        StringBuffer m = new StringBuffer();
        for(int i = 0;i<bytes.length;i++){
            if((bytes[i]&0xff) > 0x0f)
                m.append(String.format("%x", bytes[i]));
            else
                m.append(String.format("0%x", bytes[i]));
            m.append(" ");
        }

        return m.toString();

    }

    /**
     * 16进制字符串转换为byte[]
     * **/
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase().replace(" ", "");
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
