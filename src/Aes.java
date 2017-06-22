/**
 * Created by admin on 2017/5/29.
 */
public class Aes {

    public enum KeySize{
        Bits128(4,10),Bits192(6,12),Bits256(8,14);
        /**以字为单位的种子密钥长度,16bytes=1word=128bits*/
        private int nk;
        /**轮密钥的次数*/
        private int nr;
        private KeySize(int nk,int nr){
            this.nk = nk;
            this.nr = nr;
        }
    }

    private char[][] rcon ={
            {0x00,0x00,0x00,0x00},
            {0x01,0x00,0x00,0x00},
            {0x02,0x00,0x00,0x00},
            {0x04,0x00,0x00,0x00},
            {0x08,0x00,0x00,0x00},
            {0x10,0x00,0x00,0x00},
            {0x20,0x00,0x00,0x00},
            {0x40,0x00,0x00,0x00},
            {0x80,0x00,0x00,0x00},
            {0x1b,0x00,0x00,0x00},
            {0x36,0x00,0x00,0x00}
    };

    char sbox[] =
            {
                    0x63, 0x7C, 0x77, 0x7B, 0xF2, 0x6B, 0x6F, 0xC5, 0x30, 0x01, 0x67, 0x2B, 0xFE, 0xD7, 0xAB, 0x76,
                    0xCA, 0x82, 0xC9, 0x7D, 0xFA, 0x59, 0x47, 0xF0, 0xAD, 0xD4, 0xA2, 0xAF, 0x9C, 0xA4, 0x72, 0xC0,
                    0xB7, 0xFD, 0x93, 0x26, 0x36, 0x3F, 0xF7, 0xCC, 0x34, 0xA5, 0xE5, 0xF1, 0x71, 0xD8, 0x31, 0x15,
                    0x04, 0xC7, 0x23, 0xC3, 0x18, 0x96, 0x05, 0x9A, 0x07, 0x12, 0x80, 0xE2, 0xEB, 0x27, 0xB2, 0x75,
                    0x09, 0x83, 0x2C, 0x1A, 0x1B, 0x6E, 0x5A, 0xA0, 0x52, 0x3B, 0xD6, 0xB3, 0x29, 0xE3, 0x2F, 0x84,
                    0x53, 0xD1, 0x00, 0xED, 0x20, 0xFC, 0xB1, 0x5B, 0x6A, 0xCB, 0xBE, 0x39, 0x4A, 0x4C, 0x58, 0xCF,
                    0xD0, 0xEF, 0xAA, 0xFB, 0x43, 0x4D, 0x33, 0x85, 0x45, 0xF9, 0x02, 0x7F, 0x50, 0x3C, 0x9F, 0xA8,
                    0x51, 0xA3, 0x40, 0x8F, 0x92, 0x9D, 0x38, 0xF5, 0xBC, 0xB6, 0xDA, 0x21, 0x10, 0xFF, 0xF3, 0xD2,
                    0xCD, 0x0C, 0x13, 0xEC, 0x5F, 0x97, 0x44, 0x17, 0xC4, 0xA7, 0x7E, 0x3D, 0x64, 0x5D, 0x19, 0x73,
                    0x60, 0x81, 0x4F, 0xDC, 0x22, 0x2A, 0x90, 0x88, 0x46, 0xEE, 0xB8, 0x14, 0xDE, 0x5E, 0x0B, 0xDB,
                    0xE0, 0x32, 0x3A, 0x0A, 0x49, 0x06, 0x24, 0x5C, 0xC2, 0xD3, 0xAC, 0x62, 0x91, 0x95, 0xE4, 0x79,
                    0xE7, 0xC8, 0x37, 0x6D, 0x8D, 0xD5, 0x4E, 0xA9, 0x6C, 0x56, 0xF4, 0xEA, 0x65, 0x7A, 0xAE, 0x08,
                    0xBA, 0x78, 0x25, 0x2E, 0x1C, 0xA6, 0xB4, 0xC6, 0xE8, 0xDD, 0x74, 0x1F, 0x4B, 0xBD, 0x8B, 0x8A,
                    0x70, 0x3E, 0xB5, 0x66, 0x48, 0x03, 0xF6, 0x0E, 0x61, 0x35, 0x57, 0xB9, 0x86, 0xC1, 0x1D, 0x9E,
                    0xE1, 0xF8, 0x98, 0x11, 0x69, 0xD9, 0x8E, 0x94, 0x9B, 0x1E, 0x87, 0xE9, 0xCE, 0x55, 0x28, 0xDF,
                    0x8C, 0xA1, 0x89, 0x0D, 0xBF, 0xE6, 0x42, 0x68, 0x41, 0x99, 0x2D, 0x0F, 0xB0, 0x54, 0xBB, 0x16
            };

    char isbox[] =
            {
                    0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5, 0x38, 0xBF, 0x40, 0xA3, 0x9E, 0x81, 0xF3, 0xD7, 0xFB,
                    0x7C, 0xE3, 0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4, 0xDE, 0xE9, 0xCB,
                    0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D, 0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E,
                    0x08, 0x2E, 0xA1, 0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B, 0xD1, 0x25,
                    0x72, 0xF8, 0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4, 0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92,
                    0x6C, 0x70, 0x48, 0x50, 0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D, 0x84,
                    0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC, 0xD3, 0x0A, 0xF7, 0xE4, 0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06,
                    0xD0, 0x2C, 0x1E, 0x8F, 0xCA, 0x3F, 0x0F, 0x02, 0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B,
                    0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2, 0xCF, 0xCE, 0xF0, 0xB4, 0xE6, 0x73,
                    0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD, 0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8, 0x1C, 0x75, 0xDF, 0x6E,
                    0x47, 0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E, 0xAA, 0x18, 0xBE, 0x1B,
                    0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79, 0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4,
                    0x1F, 0xDD, 0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xEC, 0x5F,
                    0x60, 0x51, 0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D, 0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF,
                    0xA0, 0xE0, 0x3B, 0x4D, 0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53, 0x99, 0x61,
                    0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77, 0xD6, 0x26, 0xE1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D
            };

    /**以字为单位的种子密钥长度,16bytes=4word=128bits*/
    private int nk;
    /**轮密钥的次数*/
    private int nr;
    /**以字节为单位的明文长度,固定值16字节*/
    private static final int nb = 16;
    /**密钥轮值表数组*/
    private byte[] keys;
    private byte[] data;

    public Aes(KeySize keySize,byte[] keys){
        setNkNr(keySize);
        keyExpansion(keys);
    }

    private void setNkNr(KeySize keySize){
        nk = keySize.nk;
        nr = keySize.nr;
    }

    /**
     * 密钥扩展,创建密钥调度表
     * @param  inkeys 种子密钥
     */
    private void keyExpansion(byte[] inkeys){
        if(inkeys == null ||
                (inkeys.length != 16 && inkeys.length != 24 && inkeys.length != 32))
            throw new IllegalArgumentException("keys length out of range{16,24,32}");
        keys = new byte[nb*(nr+1)];//轮密钥次数nr，再加上轮值之前的1次addRoundKey预处理
        System.arraycopy(inkeys, 0, keys, 0, inkeys.length);
        int totalRow = nb*(nr+1)/4;
        byte[] tmp = new byte[4];
        int index = 0;
        for(int row = nk;row < totalRow;row++){
            //循环一次处理一个round的密钥, 一行4字节
            index = 4*(row - 1);
            tmp[0] = keys[index];
            tmp[1] = keys[index+1];
            tmp[2] = keys[index+2];
            tmp[3] = keys[index+3];
            if(row%nk == 0)
                tmp = xorRcon(row, subWord(rotWord(tmp)));
            else if(nk == 8 && (row % nk == 4)){
                tmp = subWord(tmp);
            }

            keys[row*4] = (byte) (keys[(row-nk)*4] ^ tmp[0]);
            keys[row*4+1] = (byte) (keys[(row-nk)*4+1] ^ tmp[1]);
            keys[row*4+2] = (byte) (keys[(row-nk)*4+2] ^ tmp[2]);
            keys[row*4+3] = (byte) (keys[(row-nk)*4+3] ^ tmp[3]);
        }

    }

    private void addRoundKey(int row){
        for(int i = 0;i < 4;i++){
            //每轮密钥调度，16字节一组
            //循环一次处理一个round的密钥, 一行4字节,4次共16字节
//          data[i*4] ^= keys[ row*16+i];
//          data[i*4+1] ^= keys[ row*16+1*4+i];
//          data[i*4+2] ^= keys[ row*16+2*4+i];
//          data[i*4+3] ^= keys[ row*16+3*4+i];

            data[i*4] ^= keys[ row*16+4*i];
            data[i*4+1] ^= keys[ row*16+4*i+1];
            data[i*4+2] ^= keys[ row*16+4*i+2];
            data[i*4+3] ^= keys[ row*16+4*i+3];
        }
    }

    /**
     * 进行s-box非线性变换
     */
    private void subBytes(){
        for(int i = 0;i<16;i++)
            data[i] = (byte) sbox[data[i]&0xff];
    }

    /**
     * 进行is-box非线性变换
     */
    private void invSubBytes(){
        for(int i = 0;i<16;i++)
            data[i] = (byte) isbox[data[i]&0xff];
    }


    /**
     * 4x4矩阵(x,y)数组，每行循环左移x个字节
     */
    private void shiftRows(){
        byte[] tmp = new byte[4];
        int n;
        for(int i = 0;i<4;i++){
            n = i*4;
            tmp[0] = data[n];
            tmp[1] = data[n+1];
            tmp[2] = data[n+2];
            tmp[3] = data[n+3];

            data[n] = tmp[i%4];
            data[n+1] = tmp[(1+i)%4];
            data[n+2] = tmp[(2+i)%4];
            data[n+3] = tmp[(3+i)%4];
        }
    }

    /**
     * 4x4矩阵(x,y)数组，每行循环右移x个字节
     */
    private void invShiftRows(){
        byte[] tmp = new byte[4];
        int n;
        for(int i = 0;i<4;i++){
            n = i*4;
            tmp[0] = data[n];
            tmp[1] = data[n+1];
            tmp[2] = data[n+2];
            tmp[3] = data[n+3];

            data[n+i%4] = tmp[0];
            data[n+(1+i)%4] = tmp[1];
            data[n+(2+i)%4] = tmp[2];
            data[n+(3+i)%4] = tmp[3];
        }
    }

    /**
     * 按列混合
     */
    private void mixColumns(){
        byte[] tmp = new byte[4];
        for(int i = 0;i< 4;i++){
            tmp[0] = data[i];
            tmp[1] = data[i+4];
            tmp[2] = data[i+8];
            tmp[3] = data[i+12];

            data[i] = (byte) (gfmulBy02(tmp[0])^gfmulBy03(tmp[1])^gfmulBy01(tmp[2])^gfmulBy01(tmp[3]));
            data[i+4] = (byte) (gfmulBy01(tmp[0])^gfmulBy02(tmp[1])^gfmulBy03(tmp[2])^gfmulBy01(tmp[3]));
            data[i+8] = (byte) (gfmulBy01(tmp[0])^gfmulBy01(tmp[1])^gfmulBy02(tmp[2])^gfmulBy03(tmp[3]));
            data[i+12] = (byte) (gfmulBy03(tmp[0])^gfmulBy01(tmp[1])^gfmulBy01(tmp[2])^gfmulBy02(tmp[3]));

        }

    }

    /**
     * 按列逆混合
     */
    private void invMixColumns(){
        byte[] tmp = new byte[4];
        for(int i = 0;i< 4;i++){
            tmp[0] = data[i];
            tmp[1] = data[i+4];
            tmp[2] = data[i+8];
            tmp[3] = data[i+12];

            data[i+0] = (byte) (gfmulBy0e(tmp[0]) ^ gfmulBy0b(tmp[1]) ^ gfmulBy0d(tmp[2]) ^ gfmulBy09(tmp[3]));
            data[i+4] = (byte) (gfmulBy09(tmp[0]) ^ gfmulBy0e(tmp[1]) ^ gfmulBy0b(tmp[2]) ^ gfmulBy0d(tmp[3]));
            data[i+8] = (byte) (gfmulBy0d(tmp[0]) ^ gfmulBy09(tmp[1]) ^ gfmulBy0e(tmp[2]) ^ gfmulBy0b(tmp[3]));
            data[i+12] = (byte) (gfmulBy0b(tmp[0]) ^ gfmulBy0d(tmp[1]) ^ gfmulBy09(tmp[2]) ^ gfmulBy0e(tmp[3]));

        }
    }

    private byte gfmulBy01(byte value){
        return value;
    }

    private byte gfmulBy02(byte value){
        if((int)value > 0x80)
            value = (byte)((value<<1)^0x1b);
        else
            value =  (byte)(value<<1);
        return value;
    }

    private byte gfmulBy03(byte value){
        return (byte) (gfmulBy01(value)^gfmulBy02(value));
    }

    private byte gfmulBy09(byte value){
        return (byte) (gfmulBy01(value)^gfmulBy02(gfmulBy02(gfmulBy02(value))));
    }
    private byte gfmulBy0b(byte value){
        return (byte) (gfmulBy01(value)^gfmulBy02(value)^gfmulBy02(gfmulBy02(gfmulBy02(value))));
    }

    private byte gfmulBy0d(byte value){
        return (byte) (gfmulBy01(value)^gfmulBy02(gfmulBy02(value))^gfmulBy02(gfmulBy02(gfmulBy02(value))));
    }

    private byte gfmulBy0e(byte value){
        return (byte) (gfmulBy02(value)^gfmulBy02(gfmulBy02(value))^gfmulBy02(gfmulBy02(gfmulBy02(value))));
    }

    private byte[] rotWord(byte[] word){
        return new byte[]{word[1],word[2],word[3],word[0]};
    }

    private byte[] subWord(byte[] word){
        return new byte[]{(byte) sbox[word[0]&0xff],(byte) sbox[word[1]&0xff],
                (byte) sbox[word[2]&0xff],(byte) sbox[word[3]&0xff]};
    }

    private byte[] xorRcon(int row,byte[] word){
        byte[] ret = new byte[4];
        ret[0] = (byte) (word[0] ^ rcon[row/nk][0]);
        ret[1] = (byte) (word[1] ^ rcon[row/nk][1]);
        ret[2] = (byte) (word[2] ^ rcon[row/nk][2]);
        ret[3] = (byte) (word[3] ^ rcon[row/nk][3]);
        return ret;
    }

    /**
     * 加密
     * @param plainText
     * @return
     */
    public byte[] cipher(byte[] plainText){

        if(plainText == null || plainText.length == 0)
            throw new IllegalArgumentException("plainText can not be null");
        data = new byte[16];
        System.arraycopy(plainText, 0, data, 0, plainText.length);

        addRoundKey(0);//预处理一次
        for(int row = 1;row < nr;row++){
            subBytes();

            shiftRows();

            mixColumns();

            addRoundKey(row);
        }
        //最后一轮不需要执行mixColumns
        subBytes();
        shiftRows();
        addRoundKey(nr);
        return data;
    }

    /**
     * 解密
     * @param cipherText
     * @return
     */
    public byte[] invCipher(byte[] cipherText){
        if(cipherText == null || cipherText.length == 0)
            throw new IllegalArgumentException("cipherText can not be null");
        data = new byte[16];
        System.arraycopy(cipherText, 0, data, 0, cipherText.length);

        addRoundKey(nr);//预处理一次
        for(int row = nr-1;row > 0;row--){
            invShiftRows();
            invSubBytes();
            addRoundKey(row);
            invMixColumns();
        }
        //最后一轮不需要执行mixColumns

        invShiftRows();
        invSubBytes();
        addRoundKey(0);
//      print();
        return data;
    }
/*
    public static void main(String[] args){
        byte[] keys = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08,
                0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17 };
        Aes aes = new Aes(KeySize.Bits256, keys);
        //--------加密-------------------------
        String plainText = "1234abcd1234abcd";
        byte[] cipherText = aes.cipher(plainText.getBytes());
        System.out.println("plainText: "+plainText);
        StringBuffer m = new StringBuffer();
        for(int i = 0;i< 16;i++){
            if((cipherText[i]&0xff) > 0x0f)
                m.append(String.format("%x", cipherText[i]));
            else
                m.append(String.format("0%x", cipherText[i]));
            m.append(" ");
        }


        System.out.println("cipherText:"+m);

        //--------解密-------------------------
        byte[] ret = aes.invCipher(cipherText);
        String val = new String(ret);
        System.out.println("invCipher cipherText : "+val);


    }*/

}