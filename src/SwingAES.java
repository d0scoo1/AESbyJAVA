import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SwingAES {

    private SwingAES swingAES ;
    public void setSwingAES(SwingAES s){
        this.swingAES = s;
    }

    public static void main(String[] args) {
        SwingAES s = new SwingAES();
        s.initPanel();
        s.setSwingAES(s);
    }
    private JFrame frame ;
    private void initPanel(){
        // 创建 JFrame 实例
            frame = new JFrame("AES _14416512_Vickey");
        // Setting the width and height of frame
        frame.setSize(600, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        /****/
        int windowWidth = frame.getWidth(); //获得窗口宽
        int windowHeight = frame.getHeight();//获得窗口高
        Toolkit kit = Toolkit.getDefaultToolkit(); //定义工具包
        Dimension screenSize = kit.getScreenSize(); //获取屏幕的尺寸
        int screenWidth = screenSize.width; //获取屏幕的宽
        int screenHeight = screenSize.height; //获取屏幕的高
        frame.setLocation(screenWidth/2-windowWidth/2, screenHeight/2-windowHeight/2);//设置窗口居中显示
        frame.setResizable(false);

        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);

        // 设置界面可见
        frame.setVisible(true);
    }

    private JTextField keyText;
    private JTextField plainText;
    private JTextField cipherText;
    private JTextField invcipherText;
    private JTextField filecipherText;
    private JTextField fileinvcipherText;

    private JRadioButton way_ContentButton;
    private JRadioButton way_FileButton;
    private JRadioButton ECBButton;
    private JRadioButton CBCButton;
    private JRadioButton size128Button;
    private JRadioButton size192Button;
    private JRadioButton size256Button;

    private JButton cipherButton;
    private JButton invcipherButton;
    private JButton clearButton;
    private JButton fileButton;
    private JButton invfileButton;

    private JLabel readLengthLabel;
    private JSlider sliderM;
    private JSlider sliderN;
    private JSlider sliderMulti;

    private JTextArea textArea;
    private JProgressBar progressBar;

    private Aes aes;
    private int readLength = 16;
    private int multi = 0;
    private String way = "纯文本";
    private String keySize = "128";
    private String type = "ECB";
    private String key = "1234567890123456";


    private void placeComponents(JPanel panel) {

        int x =50,y=20,space= 30;

        panel.setLayout(null);

        JLabel wayLabel = new JLabel("文件类型:");
        wayLabel.setBounds(x,y,100,25);
        panel.add(wayLabel);

        ButtonGroup wayGroup = new ButtonGroup();
        way_ContentButton = new JRadioButton("纯文本",true);
        wayGroup.add(way_ContentButton);
        way_ContentButton.setBounds(120,y,100,25);
        panel.add(way_ContentButton);

        way_FileButton = new JRadioButton("文件",false);
        wayGroup.add(way_FileButton);
        way_FileButton.setBounds(220,y,100,25);
        panel.add(way_FileButton);
        addWayButtonActionListener(way_ContentButton);
        addWayButtonActionListener(way_FileButton);
        y+=space;

        JLabel typeLabel = new JLabel("工作模式:");
        typeLabel.setBounds(x,y,100,25);
        panel.add(typeLabel);

        ButtonGroup typeGroup = new ButtonGroup();
        ECBButton = new JRadioButton("ECB",true);
        typeGroup.add(ECBButton);
        ECBButton.setBounds(120,y,50,25);
        panel.add(ECBButton);

        CBCButton = new JRadioButton("CBC",false);
        typeGroup.add(CBCButton);
        CBCButton.setBounds(170,y,50,25);
        panel.add(CBCButton);

        //监听
        addTypeButtonActionListener(ECBButton);
        addTypeButtonActionListener(CBCButton);

        y+=space;

        JLabel AESLabel = new JLabel("密钥长度:");
        AESLabel.setBounds(x,y,100,25);
        panel.add(AESLabel);

        ButtonGroup keySizeGroup = new ButtonGroup();
        size128Button = new JRadioButton("128",true);
        keySizeGroup.add(size128Button);
        size128Button.setBounds(120,y,50,25);
        panel.add(size128Button);

        size192Button = new JRadioButton("192",false);
        keySizeGroup.add(size192Button);
        size192Button.setBounds(170,y,50,25);
        panel.add(size192Button);

        size256Button = new JRadioButton("256",false);
        keySizeGroup.add(size256Button);
        size256Button.setBounds(220,y,50,25);
        panel.add(size256Button);

        //监听
        addKeySizeButtonActionListener(size128Button);
        addKeySizeButtonActionListener(size192Button);
        addKeySizeButtonActionListener(size256Button);
        y+=space;

        // 创建 JLabel
        JLabel keyLabel = new JLabel("KEY:");
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        keyLabel.setBounds(x,y,50,25);
        panel.add(keyLabel);

        /* 
         * 创建文本域用于用户输入
         */
        keyText = new JTextField(20);
        keyText.setBounds(100,y,400,25);
        keyText.setText("1234567890123456");
        panel.add(keyText);
        y+=space;
        JLabel plainLabel = new JLabel("明文:");
        plainLabel.setBounds(x,y,50,25);
        panel.add( plainLabel);

        plainText = new JTextField(20);
        plainText.setBounds(100,y,400,25);
        panel.add(plainText);

        y+=space;
        JLabel cipherLabel = new JLabel("密文:");
        cipherLabel.setBounds(x,y,50,25);
        panel.add(cipherLabel);

        cipherText = new JTextField(20);
        cipherText.setBounds(100,y,400,25);
        panel.add(cipherText);

        y+=space;
        JLabel invcipherLabel = new JLabel("解密:");
        invcipherLabel.setBounds(x,y,50,25);
        panel.add(invcipherLabel);

        invcipherText = new JTextField(20);
        invcipherText.setBounds(100,y,400,25);
        panel.add(invcipherText);
        y+=50;

        JLabel filecipherLabel = new JLabel("加密文件:");
        filecipherLabel.setBounds(x,y,100,25);
        panel.add(filecipherLabel);

        filecipherText = new JTextField(20);
        filecipherText.setBounds(120,y,350,25);
        panel.add(filecipherText);

        // 创建从文件读取按钮
        fileButton = new JButton("File");
        fileButton.setBounds(x+430, y, 80, 25);
        panel.add(fileButton);

        y+=space;

        JLabel fileinvcipherLabel = new JLabel("解密文件:");
        fileinvcipherLabel.setBounds(x,y,100,25);
        panel.add(fileinvcipherLabel);

        fileinvcipherText = new JTextField(20);
        fileinvcipherText.setBounds(120,y,350,25);
        panel.add(fileinvcipherText);

        invfileButton = new JButton("File");
        invfileButton.setBounds(x+430, y, 80, 25);
        panel.add(invfileButton);
        y+=40;

        JLabel readLengthTipLabel = new JLabel("每次读取字节数：");
        readLengthTipLabel.setBounds(x,y,110,25);
        panel.add(readLengthTipLabel);

        readLengthLabel = new JLabel("（M*16*10^N）bytes   约为（M*16*10^N / 10^6) M");
        readLengthLabel.setBounds(x+110,y,400,25);
        panel.add(readLengthLabel);

        y+=space;

        JLabel MLabel = new JLabel("M:");
        MLabel.setBounds(x,y,20,25);
        panel.add(MLabel);

        sliderM = new JSlider(1,9,1);
        sliderM.setMajorTickSpacing(1);
        sliderM.setPaintTicks(true);
        sliderM.setSnapToTicks(true);
        sliderM.setPaintLabels(true);
        sliderM.setBounds(x+20, y,200, 50);
        panel.add(sliderM);


        JLabel NLabel = new JLabel("N:");
        NLabel.setBounds(x+240,y,20,25);
        panel.add(NLabel);

        sliderN = new JSlider(0,7,0);
        sliderN.setMajorTickSpacing(1);
        sliderN.setPaintTicks(true);
        sliderN.setSnapToTicks(true);
        sliderN.setPaintLabels(true);
        sliderN.setBounds(x+260, y,200, 50);
        panel.add(sliderN);

        addJSliderListener(sliderM);
        addJSliderListener(sliderN);
        y+=50;

        JLabel multiTipLabel = new JLabel("文件多线程：（0为不采用多线程，仅ECB）");
        multiTipLabel.setBounds(x,y,300,25);
        panel.add(multiTipLabel);
        y+=space;
        sliderMulti = new JSlider(0,12,0);
        sliderMulti.setMajorTickSpacing(1);
        sliderMulti.setPaintTicks(true);
        sliderMulti.setSnapToTicks(true);
        sliderMulti.setPaintLabels(true);
        sliderMulti.setBounds(x, y,400, 50);
        panel.add(sliderMulti);
        setSliderMultiListener(sliderMulti);
        y+=50;



        // 创建加密按钮
        cipherButton = new JButton("加密");
        cipherButton.setBounds(200, y, 80, 25);
        panel.add(cipherButton);

        // 创建解密按钮
        invcipherButton = new JButton("解密");
        invcipherButton.setBounds(300, y, 80, 25);
        panel.add(invcipherButton);

        // 创建清空按钮
        clearButton = new JButton("Clear");
        clearButton.setBounds(400, y, 80, 25);
        panel.add(clearButton);

        y+=40;

        textArea = new JTextArea(8,40);
        textArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(x,y,480,200);
        panel.add(scrollPane);
        y=y+200;

        progressBar=new JProgressBar();
        progressBar.setBounds(x,y,480,25);
        progressBar.setVisible(true);
        progressBar.setStringPainted(true);
        panel.add(progressBar);

        cipherButton.addActionListener(new cipherActionListener());
        invcipherButton.addActionListener(new invcipherActionListener());
        clearButton.addActionListener(new clearActionListener());
        fileButton.addActionListener(new fileActionListener());
        invfileButton.addActionListener(new invfileActionListener());

        fileButton.setEnabled(false);
        invfileButton.setEnabled(false);

      //  invcipherText.setEnabled(false);
        filecipherText.setEnabled(false);
        fileinvcipherText.setEnabled(false);
    }
    private void addWayButtonActionListener(JRadioButton button){
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileButton.setEnabled(false);
                invfileButton.setEnabled(false);
                plainText.setEnabled(false);
                cipherText.setEnabled(false);
             //   invcipherText.setEnabled(false);
                filecipherText.setEnabled(false);
                fileinvcipherText.setEnabled(false);

                if(button.getText() == "纯文本"){
                    way = "纯文本";

                    plainText.setEnabled(true);
                    cipherText.setEnabled(true);


                }else{//文件加密
                    way = "文件";
                    fileButton.setEnabled(true);
                    invfileButton.setEnabled(true);

                    filecipherText.setEnabled(true);
                    fileinvcipherText.setEnabled(true);

                }
            }
        });
    }

    private void addTypeButtonActionListener(JRadioButton button){
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type = button.getText();
                System.out.println("加密模式设置为："+type);
                if(type!="ECB") {
                    sliderMulti.setValue(0);
                    multi = 0;
                    sliderMulti.setEnabled(false);
                }else
                    sliderMulti.setEnabled(true);
            }
        });
    }

    private void addKeySizeButtonActionListener(JRadioButton button){
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                keySize = button.getText();
                System.out.println("密钥长度设置为："+keySize);
                String key = "";
                for(int i = 1 ; i <= Integer.parseInt(keySize)/8;i++){
                    key = key + i%10;
                }
                keyText.setText(key);
            }
        });
    }

    private void addJSliderListener(JSlider sliders){
        sliders.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                int m = sliderM.getValue();
                int n = sliderN.getValue();

                readLength = m*16;
                readLength = readLength*(int)Math.pow(10.0,(double)n);

                String label = readLength + "bytes  ";
                if(n>=4){
                    label = label + " 约为 "+((double)readLength)/1000000+" M";
                }
                readLengthLabel.setText(label);

            }
        });

    }
    private void setSliderMultiListener(JSlider sliders){
        sliders.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                multi = sliderMulti.getValue();
            }
        });
    }

    private class clearActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {

            plainText.setText("");
            cipherText.setText("");
            invcipherText.setText("");
            textArea.setText("");
            filecipherText.setText("");
            fileinvcipherText.setText("");
        }
    }

    private class cipherActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("加密");

            key = keyText.getText();
            if(validate("加密")){
                cipher();
               // getAes();

            }
        }
    }

    private class invcipherActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println("解密");

            key = keyText.getText();
            if(validate("解密")){
              //  getAes();
                invCipher();
            }

        }
    }

    private class fileActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("C:/"));
          //  FileNameExtensionFilter filter = new FileNameExtensionFilter(
         //           "JPG & GIF Images", "jpg", "gif");
          //  chooser.setFileFilter(filter); //  过滤器可以不要
            int returnVal = chooser.showOpenDialog(frame);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
               filecipherText.setText(chooser.getSelectedFile().getPath());
                System.out.println("设置加密文件为: " +chooser.getSelectedFile().getPath());

            }
        }
    }

    private class invfileActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("C:/AES/"));
            int returnVal = chooser.showOpenDialog(frame);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                fileinvcipherText.setText(chooser.getSelectedFile().getPath());
                System.out.println("解密文件为: " +chooser.getSelectedFile().getPath());
                textArea.append("解密文件为: " +chooser.getSelectedFile().getPath()+"\n");
            }
        }
    }

    private boolean validate(String button){

        if (keyText.getText().isEmpty()) {
            System.out.println("请输入KEY");
            return false;
        }else{
            int temp_keySize = Integer.parseInt(keySize);

            if((temp_keySize / 8) != key.length()){ //判断密钥长度是否正确
                System.out.println("密钥长度不对！");
                return false;
            }
        }

        if(button == "加密") {
            if(way == "纯文本" && plainText.getText().isEmpty()){
                System.out.println("请输入加密纯文本");
                return false;
            }
            if(way == "文件"){
                if(filecipherText.getText().isEmpty()) {
                    System.out.println("请输入加密文件");
                    return false;
                }
                if(type != "ECB" && multi>0){
                    System.out.println("多线程加密仅支持ECB！请更改加密模式！");
                    return false;
                }


            }

        }
        if(button == "解密"){
            if(way == "纯文本" && cipherText.getText().isEmpty()){
                System.out.println("请输入解密纯文本");
                return false;
            }
            if(way == "文件" ){
                if(fileinvcipherText.getText().isEmpty()) {
                    System.out.println("请输入解密文件");
                    return false;
                }
                if(type != "ECB" && multi>0) {
                    System.out.println("多线程解密仅支持ECB！");
                    return false;
                }
            }
        }

        if(multi>0 && readLength/16%multi!=0){
            System.out.println("每次读取字节数除以线程数必须为16的倍数");
            return false;
        }
        return true;
    }

    private Aes getAes(){
        if(keySize == "128")
            return new Aes(Aes.KeySize.Bits128,key.getBytes());
        if(keySize == "192")
            return new Aes(Aes.KeySize.Bits192,key.getBytes());

        return new Aes(Aes.KeySize.Bits256,key.getBytes());
    }
    private AesPower getAesPower(){
        if(keySize == "128")
            return new AesPower(key, Aes.KeySize.Bits128,readLength,readLength/multi);
        if(keySize == "192")
            return new AesPower(key, Aes.KeySize.Bits192,readLength,readLength/multi);

        return new AesPower(key, Aes.KeySize.Bits256,readLength,readLength/multi);
    }

    //加密
    private void cipher(){
        aesMessage();
        AesDao aesDao = new AesDaoImp();
        aesDao.setSwing(swingAES);
        if(way == "纯文本"){
            String temp_plainText = plainText.getText();
            byte[] temp_cipherText = null;

            if(type == "ECB" ){
               temp_cipherText = aesDao.textCipher(getAes(),temp_plainText.getBytes(),temp_plainText.getBytes().length);
            }
            if(type == "CBC"){//如果CBC，要先初始化
                aesDao.initRcon(); //初始化
                temp_cipherText = aesDao.textCipherCBC(getAes(),temp_plainText.getBytes(),temp_plainText.getBytes().length);
            }
            cipherText.setText(Util.formatHex(temp_cipherText));
            textCipherMessage(temp_cipherText);
        }else{
            String path_read = filecipherText.getText();
            if(multi>0){  //多线程

                AesPower aesPower = getAesPower();
                aesPower.setSwing(swingAES);
                new Thread(){
                    public void run(){
                        aesPower.fileClipher(path_read, Util.getPath_cipher(path_read));
                    }
                }.start();

            }else {
                new Thread(){
                    public void run(){
                        aesDao.fileClipher(getAes(), type, path_read, Util.getPath_cipher(path_read), readLength); //16e7 160M
                    }
                }.start();
                System.out.println(path_read);
            }
            fileinvcipherText.setText(Util.getPath_cipher(path_read));
        }
    }
    //解密
    private void invCipher(){
        aesMessage();
        AesDao aesDao = new AesDaoImp();
        aesDao.setSwing(swingAES);
        if(way == "纯文本"){

            byte[] temp_cipherText = Util.hexStringToBytes(cipherText.getText());
            byte[] temp_invCipherText = null;
            if(type == "ECB" ){
                temp_invCipherText = aesDao.textInvCliper(getAes(),temp_cipherText,temp_cipherText.length);
            }
            if(type == "CBC"){//如果CBC，要先初始化
                aesDao.initRcon(); //初始化
                temp_invCipherText = aesDao.textInvCliperCBC(getAes(),temp_cipherText,temp_cipherText.length);
            }
            invcipherText.setText(new String(temp_invCipherText));
            textInvCipherMessage(temp_invCipherText);
        }else{
            String path_read = fileinvcipherText.getText();
            if(multi>0){//多线程

                AesPower aesPower = getAesPower();
                aesPower.setSwing(swingAES);
                new Thread(){
                    public void run(){
                        aesPower.fileInvClipher(path_read, Util.getPath_invCipher(path_read));
                    }
                }.start();

            }else {
                new Thread(){
                    public void run(){
                        aesDao.fileInvClipher(getAes(), type, path_read, Util.getPath_invCipher(path_read));
                    }
                }.start();

                System.out.println(path_read);
            }
        }
    }

    public void setTextArea(String string){
        textArea.append(string+"\n");
    }



    private void textCipherMessage(byte[] cipher){
        String m = "------纯文本加密------\n"
        + "明文为："+ plainText.getText()+"\n"
        + "明文16进制为："+ Util.formatHex(plainText.getText().getBytes())+"\n"
        + "密文16进制为："+ Util.formatHex(cipher)+"\n";
        setTextArea(m);
    }
    private void textInvCipherMessage(byte[] invCipher){
        String m =  "------纯文本解密------\n"
                + "密文16进制为："+cipherText.getText()+"\n"
                + "解密16进制为："+ Util.formatHex(invCipher)+"\n"
                + "解密结果为:"+(new String(invCipher)) +"\n";
        setTextArea(m);
    }

    private void aesMessage(){
        String m = "------------AES信息------------\n"
                + "工作模式为："+type+"  " + "密钥长度为："+keySize+"\n"
                + "密钥为："+ key +"\n";

        setTextArea(m);
    }



    public JProgressBar getProgressBar(){
        return progressBar;
    }
}