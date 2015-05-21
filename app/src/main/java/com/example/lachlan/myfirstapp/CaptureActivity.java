package com.example.lachlan.myfirstapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.widget.TextView;

import com.example.lachlan.myfirstapp.code.DatabaseHelper;
import com.example.lachlan.myfirstapp.code.DiskSpace;
import com.example.lachlan.myfirstapp.code.Person;

import java.io.IOException;

public class CaptureActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    // state variables
    private int itemid = 1;
    private int personid = 0;

    public static int totalItems = 4;

    private boolean recording = false;
    private boolean playing = false;
//    private String[] itemNames = new String[5];

    // the media players/recorders
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;

    // form controls
    private Button stopbutton;
    private Button recordbutton;
    private Button playbutton;
    private EditText editText;
    private ImageView imageView;
    private TextView captureTitleTextView;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        stopbutton = (Button)findViewById(R.id.stopbutton);
        recordbutton = (Button)findViewById(R.id.recordbutton);
        playbutton = (Button)findViewById(R.id.playbutton);
        editText = (EditText)findViewById(R.id.edit_message);
        imageView = (ImageView) findViewById(R.id.myimageview);
        captureTitleTextView = (TextView) findViewById(R.id.capturetitle);

        stopbutton.setEnabled(false);

        dbHelper = new DatabaseHelper(getApplicationContext());

        populateTitle();

        showPicture();

        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void populateTitle() {
        Intent intent = getIntent();
        personid = intent.getIntExtra(HomeActivity.INTENT_PERSONID, 0);
        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Person p = db.getPerson(personid);

        captureTitleTextView.setText(R.string.capture_for_label + p.name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    public void playButton(android.view.View view)
    {
        if (!playing && !recording) {
            recordbutton.setEnabled(false);
            playbutton.setEnabled(false);
            stopbutton.setEnabled(true);
            playing = true;
            startPlaying();
        }
    }

    public void recordButton(android.view.View view){

        if (!recording && !playing) {
            recordbutton.setEnabled(false);
            playbutton.setEnabled(false);
            stopbutton.setEnabled(true);
            recording = true;
            startRecording();
        }
    }

    public void stopButton(android.view.View view) {
        handleStopButton();
    }

    private void handleStopButton() {

        if (recording || playing) {
            if (recording) {
                recording = false;
                stopRecording();
            }
            if (playing) {
                playing = false;
                stopPlaying();
            }
            recordbutton.setEnabled(true);
            playbutton.setEnabled(true);
            stopbutton.setEnabled(false);
        }
    }

    private void saveWord() {
        String word = editText.getText().toString();
        dbHelper.saveWord(personid, itemid, word);
    }

    public void backButton(android.view.View view){

        saveWord();

        itemid--;
        if (itemid < 1) itemid = totalItems;
        showPicture();
    }

    public void nextButton(android.view.View view){

        saveWord();

        itemid++;
        if (itemid > totalItems) itemid = 1;
        showPicture();
    }

    private void showPicture() {

        int pictureToUse = 0;

        if (itemid == 1) {
            pictureToUse = R.drawable.church;
        }
        if (itemid == 2) {
            pictureToUse = R.drawable.goat;
        }
        if (itemid == 3) {
            pictureToUse = R.drawable.rooster;
        }
        if (itemid == 4) {
            pictureToUse = R.drawable.spider;
        }

        imageView.setImageResource(pictureToUse);

        String itemName = dbHelper.getWord(personid, itemid);
        editText.setText(itemName);

    }


    private void startPlaying()
    {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                        @Override
                                                        public void onCompletion(MediaPlayer arg0) {
                                                            handleStopButton();
                                                        }
                                                    });
            String filename = DiskSpace.getFilename(itemid);
            mPlayer.setDataSource(filename);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
        }
    }

    private void stopPlaying()
    {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording()
    {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        String filename = DiskSpace.getFilename(itemid);

        mRecorder.setOutputFile(filename);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {

        }

        mRecorder.start();
    }

    private void stopRecording()
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

}
