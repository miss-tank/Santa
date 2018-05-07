package com.example.android.printersettings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

//    private ImageView leftImageView;
//    private ImageView rightImageView;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private Cursor cursor;
    private  int columnIndex;
    Button  leftUpload;
    Button  rightUpload;

    Button clearSettings;
    Button saveSettings;


    private static  final int PICK_LEFT = 1;
    private static  final int PICK_RIGHT = 2;

    private Button changeText;





    private static Context context;
    Bitmap left;
    Bitmap right;


    //color buttons
    Button Red;
    Button Blue;
    Button Green;
    Button Pink;
    Button Black;
    Button Yellow;
    Button Golden;
    Button Cyan;
    Button Purple;
    Button White;



    //color buttons
    Button BRed;
    Button BBlue;
    Button BGreen;
    Button BPink;
    Button BBlack;
    Button BYellow;
    Button BGolden;
    Button BCyan;
    Button BPurple;
    Button BWhite;




    String leftLink;
    String rightLink;

    EditText centerText;
    String centerStringMessage;

    File STORAGE_DIRECTORY=Environment.getExternalStorageDirectory();
    String DEVICE_NAME="";
    BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();


    PrinterSettings settings = new PrinterSettings();

    CheckBox copyrightText;




    static  boolean  PERMISSION_GRANTED = false;
    Bitmap FINAL_BITMAP_CREATED;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainActivity.context = getApplicationContext();

        if (!checkPermission("Create")) {
            requestPermission();
        }



        STORAGE_DIRECTORY=findExternalStorage();


        if (myDevice!=null)
        {
            DEVICE_NAME=myDevice.getName();
        }


        copyrightText= (CheckBox) findViewById(R.id.DateCheckbox);


        copyrightText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                  createCanvas();


            }
        });

        getTextColorButtons();
        getBackgroundColorButtons();



        //leftImageView=(ImageView) findViewById(R.id.leftImageView);
        leftUpload = (Button) findViewById(R.id.ButtonLeftupload);


        //rightImageView=(ImageView) findViewById(R.id.rightImageView);
        rightUpload = (Button) findViewById(R.id.ButtonRightupload);


        //showPreview = (Button) findViewById(R.id.previewImage);
        saveSettings = (Button) findViewById(R.id.saveSettings);

        centerText = (EditText) findViewById(R.id.centerEditText);


        clearSettings = (Button) findViewById(R.id.clear);

        changeText = (Button) findViewById(R.id.changeTextButton);


        changeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PERMISSION_GRANTED)
                {
                    updateCenterText();
                    createCanvas();

                }
                else
                {
                    requestPermission();
                }
            }
        });


        leftUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PERMISSION_GRANTED)
                {

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_LEFT);
                }
                else
                {
                    requestPermission();
                }
            }
        });


        clearSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PERMISSION_GRANTED) {
                    settings.rightImage=null;
                    settings.leftImage= null;
                    settings.setMessageText("   ");
                    leftLink=" ";
                    rightLink=" ";
                    clearImages();

                    settings.footerColor().setARGB(0xff, 0xff, 0xff, 0xff);
                    createCanvas();
                }
            }
        });


        rightUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, PICK_RIGHT);
                }
                else
                {
                    requestPermission();
                }
            }
        });


        saveSettings.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                saveFotter(FINAL_BITMAP_CREATED);
            }


        } );




        //this if for when teh app is started form the paused state
        SharedPreferences getSavedValues = getSharedPreferences("Prefs",0);
        String leftBmpLink = getSavedValues.getString("leftLink"," ");
        String rightBmpLink = getSavedValues.getString("rightLink"," ");

        String aColor = getSavedValues.getString("Acolor"," ");
        String rColor = getSavedValues.getString("Rcolor"," ");
        String gColor = getSavedValues.getString("Gcolor"," ");
        String bColor = getSavedValues.getString("Bcolor"," ");

        String aColorM = getSavedValues.getString("AcolorM"," ");
        String rColorM = getSavedValues.getString("RcolorM"," ");
        String gColorM = getSavedValues.getString("GcolorM"," ");
        String bColorM = getSavedValues.getString("BcolorM"," ");

        String text = getSavedValues.getString("centerText"," ");

        settings.setMessageText(text);

        if(aColor!=" ")
            settings.footerColor().setARGB(Integer.parseInt(aColor), Integer.parseInt(rColor),Integer.parseInt(gColor),Integer.parseInt(bColor));


        if(aColorM!=" ")
            settings.msgFontColor().setARGB(Integer.parseInt(aColorM), Integer.parseInt(rColorM),Integer.parseInt(gColorM),Integer.parseInt(bColorM));


        Boolean x = restoreImage(leftBmpLink , "l");

        System.out.println("For x left " + x);

        x = x && restoreImage(rightBmpLink , "r");
        System.out.println("For x right " + x);

        if(x)
        {
            createCanvas();
        }

    }



    public boolean restoreImage(String link, String side)
    {

        System.out.println("## Link lenth "+ link.length() + "Link '"+ link+"'");
        if(!(link.length()<3))
        {
            Uri uri = Uri.parse(link);
            Log.i("$$", "Uri: " + uri.toString());
            try {
                Bitmap bitmap = getBitmapFromUri(uri);
                System.out.println("**** This is teh ool "+ link.contains("left") );

                if(side.equals("l"))
                {
                    System.out.println("** In left");
                    settings.setLeftImage(bitmap);
                }
                else {
                    System.out.println("** In right");
                    settings.setRightImage(bitmap);
                }
                //view.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }



    protected void getTextColorButtons()
    {
        Red = (Button) findViewById(R.id.redColor);
        Blue = (Button) findViewById(R.id.blueColor);
        Green = (Button) findViewById(R.id.greenColor);
        Pink = (Button) findViewById(R.id.pinkColor);
        Black = (Button) findViewById(R.id.blackColor);
        Yellow = (Button) findViewById(R.id.yellowColor);
        Golden = (Button) findViewById(R.id.goldenColor);
        Cyan = (Button) findViewById(R.id.cyanColor);
        Purple = (Button) findViewById(R.id.purpleColor);
        White = (Button) findViewById(R.id.whiteColor);


        Red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0xcb, 0x00, 0x00);
                createCanvas();
            }
        });

        Blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0x18, 0x3f, 0xff);
                createCanvas();
            }
        });


        Green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0x1C, 0xCF, 0x00);
                createCanvas();
            }
        });

        Pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0xff, 0x03, 0xbc);
                createCanvas();
            }
        });

        Black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0x00, 0x00, 0x00);
                createCanvas();
            }
        });

        Yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0xff, 0xc3, 0x00);
                createCanvas();
            }
        });

        Golden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0xda, 0xa5, 0x20);
                createCanvas();
            }
        });

        Cyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0x17, 0xff, 0xff);
                createCanvas();
            }
        });

        Purple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0x7e, 0x00, 0xe0);
                createCanvas();
            }
        });

        White.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.msgFontColor().setARGB(0xff, 0xff, 0xff, 0xff);
                createCanvas();

            }
        });
    }


    protected void getBackgroundColorButtons()
    {
        BRed = (Button) findViewById(R.id.BredColor);
        BBlue = (Button) findViewById(R.id.BblueColor);
        BGreen = (Button) findViewById(R.id.BgreenColor);
        BPink = (Button) findViewById(R.id.BpinkColor);
        BBlack = (Button) findViewById(R.id.BblackColor);
        BYellow = (Button) findViewById(R.id.ByellowColor);
        BGolden = (Button) findViewById(R.id.BgoldenColor);
        BCyan = (Button) findViewById(R.id.BcyanColor);
        BPurple = (Button) findViewById(R.id.BpurpleColor);
        BWhite = (Button) findViewById(R.id.BwhiteColor);


        BRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0xcb, 0x00, 0x00);
                createCanvas();
            }
        });

        BBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0x18, 0x3f, 0xff);
                createCanvas();
            }
        });

        BGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0x1C, 0xCF, 0x00);
                createCanvas();
            }
        });

        BPink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0xff, 0x03, 0xbc);
                createCanvas();
            }
        });

        BBlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0x00, 0x00, 0x00);
                createCanvas();
            }
        });

        BYellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0xff, 0xc3, 0x00);
                createCanvas();
            }
        });

        BGolden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0xda, 0xa5, 0x20);
                createCanvas();
            }
        });

        BCyan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0x17, 0xff, 0xff);
                createCanvas();
            }
        });

        BPurple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0x7e, 0x00, 0xe0);
                createCanvas();
            }
        });


        BWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings.footerColor().setARGB(0xff, 0xff, 0xff, 0xff);
                createCanvas();
            }
        });
    }


    protected void onStart()
    {
        super.onStart();


    }


    private void requestPermission() {
        System.out.println("PERMISSION_REQUEST_CODE code  before evrything: " + PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE+1);
        System.out.println("PERMISSION_REQUEST_CODE code  after request : " + PERMISSION_REQUEST_CODE);

    }

    private static boolean checkPermission(String s) {


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        System.out.println("&& checkPermission "  + s);
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            System.out.println("*** , it is a valid build SDK");
            int result2 = ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            System.out.println("&& result2 " + result2 );

            int result3 = ContextCompat.checkSelfPermission(MainActivity.context, Manifest.permission.READ_EXTERNAL_STORAGE);
            System.out.println("&& result3 " + result3 );

            int final_result = result2 & result3 ;
            System.out.println("&& final_result " + final_result );

            if(final_result==0)
                PERMISSION_GRANTED=true;

        }

        return PERMISSION_GRANTED;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if(requestCode!=PERMISSION_REQUEST_CODE)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                //System.out.println("&& SDK capable");

                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    showMessageOKCancel("You need to allow access to External Storage to proceed.",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                                PERMISSION_REQUEST_CODE);
                                        checkPermission("m");
                                    }
                                }
                            });
                    return;
                }
            }
        }
        checkPermission("m");
    }


    private File findExternalStorage()
    {
        String path;
        String thePath=Environment.getExternalStorageDirectory().getAbsolutePath();
        File file = new File("/system/etc/vold.fstab");
        FileReader fr = null;
        BufferedReader br = null;

        try {
            fr = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if (fr != null) {
                br = new BufferedReader(fr);
                String s = br.readLine();
                while (s != null) {
                    if (s.startsWith("dev_mount")) {
                        String[] tokens = s.split("\\s");
                        path = tokens[2]; //mount_point
                        if (!Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) {
                            //TEST THIS PATH to see if it works
                            if (testPath(path))
                            {
                                thePath=path;
                                break;
                            }
                        }
                    }
                    s = br.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File (thePath);

    }


    private boolean testPath(String path)
    {
        File file = new File(path + "/SantaPrinterTester");
        try {
            if (file.createNewFile())
            {
                //System.out.println("File is created!");
            } else
            {
                //System.out.println("File already exists.");
            }

        } catch (IOException e)
        {
            return false;
        }
        try {
            if (file.createNewFile())
            {
                return false;
            } else
            {
                return true;
            }

        } catch (IOException e)
        {
            return false;
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        String stateSavedL  = savedInstanceState.getString("Save_LeftBmp");
        String stateSavedR  = savedInstanceState.getString("Save_LeftBmp");


        try {
            if(stateSavedL!=null ) {
                Bitmap bitmapL = getBitmapFromUri(Uri.parse(stateSavedL));
                //    leftImageView.setImageBitmap(bitmapL);
            }

            if(stateSavedR!=null) {
                Bitmap bitmapR = getBitmapFromUri(Uri.parse(stateSavedR));

                    //rightImageView.setImageBitmap(bitmapR);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
//        savedInstanceState.putString("Save_LeftBmp", leftLink);
//        savedInstanceState.putString("Save_rightBmp", rightLink);
//        savedInstanceState.putString("Acolor", String.valueOf(settings.footerColor().getA()));
//        System.out.println("Saved A color as "+ String.valueOf(settings.footerColor().getA()));
//
//        savedInstanceState.putString("Rcolor", String.valueOf(settings.footerColor().getR()));
//        System.out.println("Saved R color as "+ String.valueOf(settings.footerColor().getR()));
//
//        savedInstanceState.putString("Gcolor", String.valueOf(settings.footerColor().getG()));
//        System.out.println("Saved G color as "+ String.valueOf(settings.footerColor().getG()));
//
//        savedInstanceState.putString("Bcolor", String.valueOf(settings.footerColor().getB()));
//        System.out.println("Saved B color as "+ String.valueOf(settings.footerColor().getB()));


    }



    public void saveInsatances()
    {

        SharedPreferences saveValues = getSharedPreferences("Prefs",0);
        SharedPreferences.Editor editor = saveValues.edit();
        String saveCenterText = settings.getMessageText();

        editor.putString("Acolor", String.valueOf(settings.footerColor().getA()));
        editor.putString("Rcolor", String.valueOf(settings.footerColor().getR()));
        editor.putString("Gcolor", String.valueOf(settings.footerColor().getG()));
        editor.putString("Bcolor", String.valueOf(settings.footerColor().getB()));

        editor.putString("AcolorM", String.valueOf(settings.msgFontColor().getA()));
        editor.putString("RcolorM", String.valueOf(settings.msgFontColor().getR()));
        editor.putString("GcolorM", String.valueOf(settings.msgFontColor().getG()));
        editor.putString("BcolorM", String.valueOf(settings.msgFontColor().getB()));

        editor.putString("centerText", saveCenterText);
        editor.commit();

    }


    public void clearImages()
    {
        SharedPreferences saveValues = getSharedPreferences("Prefs",0);
        SharedPreferences.Editor editor = saveValues.edit();

        editor.putString("leftLink", " ");
        editor.putString("rightLink", " ");


        editor.commit();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        updateCenterText();

        SharedPreferences saveValues = getSharedPreferences("Prefs",0);
        SharedPreferences.Editor editor = saveValues.edit();

        if (requestCode == PICK_LEFT && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                leftLink = uri.toString();
                editor.putString("leftLink", leftLink);
                Log.i("$$", "Uri: " + uri.toString());
                try {
                    Bitmap bitmap = getBitmapFromUri(uri);
                    left=bitmap;
                    //leftImageView.setImageBitmap(bitmap);
                    settings.setLeftImage(bitmap);
                    createCanvas();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else  if (requestCode == PICK_RIGHT && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                rightLink = uri.toString();
                editor.putString("rightLink", rightLink);
                Log.i("$$", "Uri: " + uri.toString());
                try {
                    Bitmap bitmap = getBitmapFromUri(uri);
                    right=bitmap;
                    //rightImageView.setImageBitmap(bitmap);
                    settings.setRightImage(bitmap);
                    createCanvas();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        editor.commit();
    }



    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }



    public void createCanvas()
    {
        int w = 100;
        int h = 80;
//
//        Drawable myDrawable = getResources().getDrawable(R.drawable.bell);
//        Bitmap myLogo = ((BitmapDrawable) myDrawable).getBitmap();



        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf);

        bmp=Bitmap.createScaledBitmap (bmp, 2000,200,true);

        Canvas cv = new Canvas(bmp);
        ImageView canvasView;
        cv.drawColor(Color.TRANSPARENT);

        canvasView = (ImageView) findViewById(R.id.CanvasPreview);

        Paint messagePaint = new Paint();
        Paint datePaint = new Paint();
        Paint footerPaint = new Paint();


        System.out.println("%% A is "+settings.msgFontColor().getA());
        System.out.println("%% R is "+settings.msgFontColor().getR());
        System.out.println("%% G is "+settings.msgFontColor().getG());
        System.out.println("%% B is "+settings.msgFontColor().getB());




        messagePaint.setARGB(settings.msgFontColor().getA(), settings
                        .msgFontColor().getR(), settings.msgFontColor().getG(),
                settings.msgFontColor().getB());

        messagePaint.setTextSize(settings.getMessageTextFontSize());




        footerPaint.setARGB(settings.footerColor().getA(), settings
                        .footerColor().getR(), settings.footerColor().getG(),
                settings.footerColor().getB());




        datePaint.setARGB(settings.copyrightFontColor().getA(), settings
                .copyrightFontColor().getR(), settings.copyrightFontColor()
                .getG(), settings.copyrightFontColor().getB());
        datePaint.setTextSize(settings.getCopyrightFontSize());

        String messagePaintText=settings.getMessageText();

        int footerHeight=settings.getFooterHeight();
        Rect whiteFooter = new Rect();
        whiteFooter.set(0, bmp.getHeight()-footerHeight, bmp.getWidth(), bmp.getHeight());


        String datePaintText="";
        if(copyrightText.isChecked()) {
            datePaintText = settings.getCopyrightText();
        }

        Rect bounds = new Rect();
        messagePaint.getTextBounds(messagePaintText, 0, messagePaintText.length(), bounds);
        int messagePaintTextWidth = bounds.width();
        int messagePaintTextHeight = bounds.height();

        datePaint.getTextBounds(datePaintText, 0, datePaintText.length(), bounds);
        int datePaintTextWidth = bounds.width();
        int datePaintTextHeight = bounds.height();

        int messagePaintTextLeft=(bmp.getWidth()-messagePaintTextWidth)/2;
        int messagePaintTextTop=(bmp.getHeight()-footerHeight)+((footerHeight-messagePaintTextHeight)/2)+60;

        int datePaintTextTop = (bmp.getHeight()-datePaintTextHeight)-0;
        int datePaintTextLeft = (bmp.getWidth()-datePaintTextWidth)-10;

        cv.drawRect(whiteFooter, footerPaint);

        //Default
//        cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.bell), 0, (bmp.getHeight()-footerHeight)+((footerHeight-settings.getLeftImage().getHeight())/2), null); // 155 is the center of the bottom
//        cv.drawBitmap(BitmapFactory.decodeResource(context.getResources(),
//                R.drawable.bell), bmp.getWidth()-settings.getRightImage().getWidth(), (bmp.getHeight()-footerHeight)+((footerHeight-settings.getRightImage().getHeight())/2), null); // 155 is the center of the bottom, 150 is the width of the holly



        if (settings.leftImage != null) {

            int btmpwidth;

            int  btmpHeight = (bmp.getHeight()-footerHeight)+((footerHeight-settings.getLeftImage().getHeight())/2);


            //btmpHeight(bmp.getHeight()-footerHeight)+((footerHeight-settings.getLeftImage().getHeight())/2)
            cv.drawBitmap(settings.getLeftImage(), 0, (bmp.getHeight()-footerHeight)+((footerHeight-settings.getLeftImage().getHeight())/2), null); // 155 is the center of the bottom
        }


        if(settings.rightImage != null)
        {
            //int  btmpHeight = (bmp.getHeight()-footerHeight)+((footerHeight-settings.getLeftImage().getHeight())/2);

//            System.out.println("**** Height info ****");
//            System.out.println("bmp.getHeight() = "+bmp.getHeight());
//            System.out.println("footerHeight = "+footerHeight);
//            System.out.println("settings.getRightImage().getHeight() = "+ settings.getRightImage().getHeight());
//            System.out.println("bmp.getHeight()-footerHeight = "+ (bmp.getHeight()-footerHeight));
//            System.out.println("((footerHeight-settings.getRightImage().getHeight())/2 = "+ ((footerHeight-settings.getRightImage().getHeight())/2)  );
//            System.out.println("**** Height info END ****");

            //int btmpHeight;
            int btmpwidth;

           int  btmpHeight = (bmp.getHeight()-footerHeight)+((footerHeight-settings.getLeftImage().getHeight())/2);
           // btmpHeight = ((footerHeight-settings.getLeftImage().getHeight())/2) ;
           // btmpHeight=-60;
//
//            btmpwidth=bmp.getWidth()-settings.getRightImage().getWidth();
//            System.out.println("btmpwidth = "+btmpwidth);
//            System.out.println("btmpHeight = "+btmpHeight);
//            System.out.println("btm width  = "+settings.getRightImage().getWidth());
//            System.out.println("btm heigth  = "+settings.getRightImage().getHeight());




            cv.drawBitmap(settings.getRightImage(), bmp.getWidth()-settings.getRightImage().getWidth(), btmpHeight, null); // 155 is the center of the bottom, 150 is the width of the holly
        }

//        cv.drawBitmap(myLogo, bmp.getWidth()-myLogo.getWidth(), btmpHeight, null); // 155 is the center of the bottom, 150 is the width of the holly
//        cv.drawBitmap(myLogo, 0, (bmp.getHeight()-footerHeight)+((footerHeight-myLogo.getHeight())/2), null); // 155 is the center of the bottom
//

        cv.drawText(settings.getMessageText(), messagePaintTextLeft,  messagePaintTextTop, messagePaint);
        cv.drawText(datePaintText, datePaintTextLeft,  datePaintTextTop, datePaint);


        canvasView.setImageBitmap(bmp);

        FINAL_BITMAP_CREATED=bmp;
        saveInsatances();
    }


    public void updateCenterText()
    {
        centerStringMessage = centerText.getText().toString();
        settings.setMessageText(centerStringMessage);
    }

    public void saveFotter(Bitmap bmp)
    {
        Uri myURI = getImageUri(0);
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            FileOutputStream outfile = new FileOutputStream(myURI.getPath());
            bmp.compress(CompressFormat.PNG, 0, outfile);

            try {
                outfile.write(os.toByteArray());
                outfile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "unable to save" + myURI.getPath(), Toast.LENGTH_LONG).show();
        }
    }


    public Uri getImageUri( int offset) {
        File f = new File(STORAGE_DIRECTORY + "/SantaTrain");
        if(f.exists())
        {
            System.out.println("f exists");
            String sdir="Footer";

            File f2 = new File(STORAGE_DIRECTORY + "/SantaTrain/"+sdir+"/");
            if(f2.exists())
            {
                System.out.println("f2 exists");

                if(f2.isDirectory())
                {
                    System.out.println("f22 is a directory");
                    File file = new File(STORAGE_DIRECTORY + "/SantaTrain/"+sdir+"/", sdir + "_"+ ".png");
                    Uri imgUri = Uri.fromFile(file);
                    System.out.println("^^^ this the uri "+ imgUri );
                    System.out.println("f22 is a directory " + imgUri);
                    Toast.makeText(getApplicationContext(), f2.getPath() + " Saved Successfuly ", Toast.LENGTH_LONG).show();

                    MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String s, Uri u) {
                                    System.out.println("This is teh string "+ s);
                                    System.out.println("This is teh uri "+ u);
                                }
                            }
                    );
                    return imgUri;
                }
                else
                {
                    System.out.println("f is not a  directory");

                    Toast.makeText(getApplicationContext(), f2.getPath() + " is not a directory!", Toast.LENGTH_LONG).show();
                    finish();
                    return null;
                }
            }
            else
            {
                System.out.println("f2 doesnt exist");

                if (f2.mkdir())
                {
                    System.out.println("f2  making dir");

                    Toast.makeText(getApplicationContext(), "making dir " + f2.getPath(), Toast.LENGTH_LONG).show();
                    return getImageUri(offset);
                }
                else
                {
                    System.out.println("f2 not making dir");

                    Toast.makeText(getApplicationContext(), "couldn't make dir " + f2.getPath(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }

        }
        else
        {
            System.out.println("f doesnt exist");
            if (f.mkdir())
            {
                System.out.println("f  making dir");

                Toast.makeText(getApplicationContext(), "making dir " + f.getPath(), Toast.LENGTH_LONG).show();
                return getImageUri(offset);
            }
            else
            {
                System.out.println("f not making dir");

                Toast.makeText(getApplicationContext(), "couldn't make dir " + f.getPath(), Toast.LENGTH_LONG).show();
                finish();
            }
        }
        return null;
    }




}
