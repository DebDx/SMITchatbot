package deb.myapp.smitchatbot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import deb.myapp.smitchatbot.Adapter.ChatMessageAdapter;
import deb.myapp.smitchatbot.Model.ChatMessage;

import static android.Manifest.permission.CALL_PHONE;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    FloatingActionButton btnSend;
    EditText edtTextMag;
    ImageView imageView;
    private boolean side = true;
    private Bot bot;
    public static Chat chat;
    private ChatMessageAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        btnSend = findViewById(R.id.btnSend);
        edtTextMag = findViewById(R.id.edtTextMag);
        imageView = findViewById(R.id.imageView);
        adapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        listView.setAdapter(adapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = edtTextMag.getText().toString();
                String lt=message;
                if(message.toUpperCase().startsWith("MESS MENU")){
                    Calendar calender = Calendar.getInstance();
                    int day = calender.get(Calendar.DAY_OF_WEEK);

                    message=Integer.toString(day);

                }

                String response = chat.multisentenceRespond(message);
                String name =  ("tel:" + chat.multisentenceRespond(edtTextMag.getText().toString()));
                if (TextUtils.isEmpty(message)) {
                    Toast.makeText(MainActivity.this, "Please Enter a query", Toast.LENGTH_SHORT).show();
                    return;
                }




                sendMessage(lt,name,response);
                botsReply(response);
                edtTextMag.setText("");
                listView.setSelection(adapter.getCount() - 1);
            }

            private void calling(String name) {


            }
        });

        Dexter.withActivity(this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    custom();
                    Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
                if (report.isAnyPermissionPermanentlyDenied()) {
                    Toast.makeText(MainActivity.this, "Please Grant Permission", Toast.LENGTH_SHORT).show();
                }

            }


            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).withErrorListener(new PermissionRequestErrorListener() {
            @Override
            public void onError(DexterError error) {
                Toast.makeText(MainActivity.this, "" + error, Toast.LENGTH_SHORT).show();

            }
        }).onSameThread().check();


    }

    private void botsReply(String response) {



            ChatMessage chatMessage = new ChatMessage(false, false, response);
            adapter.add(chatMessage);

    }

    private void sendMessage(String message,String name,String response) {
        if (message.toUpperCase().startsWith("CALL") && !response.toUpperCase().startsWith("SORRY")) {

                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse(name));
                if (ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    startActivity(i);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }

        }
        else
            if(message.toUpperCase().startsWith("PROVIDE") && !response.toUpperCase().startsWith("SORRY")) {
                Intent o = new Intent("android.intent.action.VIEW", Uri.parse(response));
                startActivity(o);
            }

                ChatMessage chatMessage = new ChatMessage(false, true, message);
                adapter.add(chatMessage);


    }


    private void custom() {
        boolean available = isSDCARDAvailable();
        AssetManager assets = getResources().getAssets();
        File fileName = new File(Environment.getExternalStorageDirectory().toString() + "/TBC3/bots/smitbot");
        boolean makeFile = fileName.mkdirs();
        if (fileName.exists()) {
            try {
                for (String dir : assets.list("smitbot")) {
                    File subDir = new File(fileName.getPath() + "/" + dir);
                    boolean subDir_check = subDir.mkdirs();
                    for (String file : assets.list("smitbot/" + dir)) {
                        File newFile = new File(fileName.getPath() + "/" + dir + "/" + file);
                        if (newFile.exists()) {
                            continue;
                        }
                        InputStream in;
                        OutputStream out;
                        in = assets.open("smitbot/" + dir + "/" + file);
                        out = new FileOutputStream(fileName.getPath() + "/" + dir + "/" + file);
                        copyFile(in, out);
                        in.close();
                        out.flush();
                        out.close();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/TBC3";
            AIMLProcessor.extension = new PCAIMLProcessorExtension();
            bot = new Bot("smitbot", MagicStrings.root_path, "chat");
            chat = new Chat(bot);
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer=new byte[1024];
        int read;
        while((read = in.read(buffer)) !=-1){
            out.write(buffer,0,read);
        }
    }

    private static boolean isSDCARDAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)?true:false;

    }



}
